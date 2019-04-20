package com.edison.MainFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Full_ImageViewActivity;
import com.edison.ManageInvitation;
import com.edison.MyConnections;
import com.edison.Object.File_path;
import com.edison.Object.MyPostObject;
import com.edison.Object.SelectedInterest;
import com.edison.R;
import com.edison.Settings;
import com.edison.SingleFeedDetails;
import com.edison.UpdateProfile;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.WINDOW_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    SessionManager sm;
    FontManager fm;
    BoldTextview settings,username,auto_username,invitation_status,manageinviation;
    NormalTextview company,description;
    BoldTextview btn_edt_profile,num_of_post,num_of_views,num_of_connection;
    CircleImageView user_image;
    ProgressBar grpimageprogressbar;
    ExpandableHeightGridView postlist;
    Realm realm;
    MyPostListAdapter myPostListAdapter;
    LinearLayout ll_manageconnection,ll_connection,ll_nodata;


    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);




        return view;
    }

    @Override
    public void onResume() {
        INIT();
        super.onResume();
    }

    private void INIT() {

        sm = new SessionManager(getContext());
        fm = new FontManager(getContext());
        settings = (BoldTextview) view.findViewById(R.id.settings);
        ll_connection = (LinearLayout) view.findViewById(R.id.ll_connection);
        ll_nodata = (LinearLayout) view.findViewById(R.id.ll_nodata);
        ll_manageconnection = (LinearLayout) view.findViewById(R.id.ll_manageconnection);
        manageinviation = (BoldTextview) view.findViewById(R.id.manageinviation);
        invitation_status = (BoldTextview) view.findViewById(R.id.invitation_status);
        num_of_connection = (BoldTextview) view.findViewById(R.id.num_of_connection);
        num_of_views = (BoldTextview) view.findViewById(R.id.num_of_views);
        num_of_post = (BoldTextview) view.findViewById(R.id.num_of_post);
        username = (BoldTextview) view.findViewById(R.id.username);
        auto_username = (BoldTextview) view.findViewById(R.id.auto_username);
        btn_edt_profile = (BoldTextview) view.findViewById(R.id.btn_edt_profile);
        description = (NormalTextview) view.findViewById(R.id.description);
        company = (NormalTextview) view.findViewById(R.id.company);
        user_image = (CircleImageView) view.findViewById(R.id.user_image);
        grpimageprogressbar = (ProgressBar) view.findViewById(R.id.grpimageprogressbar);
        postlist = (ExpandableHeightGridView) view.findViewById(R.id.postlist);
        realm = Realm.getDefaultInstance();

        RealmResults<MyPostObject> homeObjectRealmResults =
                realm.where(MyPostObject.class).contains("post_user_fk",sm.getUserObject().getUser_id()).findAll();
        myPostListAdapter = new MyPostListAdapter(getActivity(), homeObjectRealmResults);
        postlist.setAdapter(myPostListAdapter);
        postlist.setExpanded(true);

        fm.setHorizontalIcon(settings);
        settings.setOnClickListener(this);
        btn_edt_profile.setOnClickListener(this);
        user_image.setOnClickListener(this);
        ll_connection.setOnClickListener(this);
        manageinviation.setOnClickListener(this);
        ll_manageconnection.setVisibility(View.GONE);

        if(sm.getUserObject()!=null)
        {
            grpimageprogressbar.setVisibility(View.VISIBLE);
            username.setText(sm.getUserObject().getName());
            auto_username.setText("@"+sm.getUserObject().getUsername());
            company.setText(sm.getUserObject().getDesignation());
            description.setText(StringEscapeUtils.unescapeJava(sm.getUserObject().getDescription()));
            Picasso.with(getActivity()).load(sm.getUserObject().getProfile_image()).into(user_image, new Callback() {
                @Override
                public void onSuccess() {
                    grpimageprogressbar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    grpimageprogressbar.setVisibility(View.GONE);
                }
            });
        }


        ///get home feed
        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
        {
            getProfile();
        }
        else
        {
            Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
        }




    }

    private void getProfile() {

        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
        }
        catch (Exception e)
        {

        }


        AndroidNetworking.post(Webservcie.get_profile)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


//                        acProgressFlower.dismiss();

                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {

                                JSONObject user = jsonObject1.optJSONObject("user");


                                realm.beginTransaction();
                                realm.clear(SelectedInterest.class);
                                realm.commitTransaction();

                                JSONArray interest = user.optJSONArray("interest");
                                if(interest!=null && interest.length()>0)
                                {
                                    JSONArray sub_category = interest.optJSONObject(0).optJSONArray("sub_category");
                                    if(sub_category!=null && sub_category.length()>0)
                                    {
                                        for (int i=0;i<sub_category.length();i++)
                                        {

                                            SelectedInterest selectedInterest = new SelectedInterest();
                                            selectedInterest.setCategory_id(interest.optJSONObject(0).optString("category_id"));
                                            selectedInterest.setSubcategory_name(sub_category.optJSONObject(i).optString("subcategory_name"));
                                            selectedInterest.setSelected("1");
                                            selectedInterest.setSubcategory_id(Integer.parseInt(sub_category.optJSONObject(i).optString("subcategory_id")));
                                            realm.beginTransaction();
                                            realm.copyToRealmOrUpdate(selectedInterest);
                                            realm.commitTransaction();
                                        }
                                    }

//                                    RealmResults<SelectedInterest> selectedInterest = realm.where(SelectedInterest.class).findAll();
//                                    System.out.println("asdasdasd "+selectedInterest.size());
//                                    System.out.println("asdasdasd "+selectedInterest);
//                                    for (SelectedInterest se:selectedInterest
//                                         ) {
//
//                                        System.out.println("a%^$^$%^$%^$% "+se.getCategory_id());
//                                        System.out.println("a%^$^$%^$%^$% "+se.getSubcategory_name());
//                                        System.out.println("a%^$^$%^$%^$% "+se.getSubcategory_id());
//
//                                    }

                                }



                                if(jsonObject1.optInt("request_count")>0)
                                {
                                    if(jsonObject1.optInt("request_count")==1)
                                    {
                                        manageinviation.setText("MANAGE");
                                    }
                                    else
                                    {
                                        manageinviation.setText("MANAGE ALL");
                                    }

                                    ll_manageconnection.setVisibility(View.VISIBLE);
                                    invitation_status.setText(jsonObject1.optString("request_count")+" Pending Invitations");

                                }
                                else
                                {
                                    ll_manageconnection.setVisibility(View.GONE);
                                }

                                username.setText(user.optString("name"));
                                auto_username.setText("@"+user.optString("username"));
                                num_of_post.setText(user.optString("post_count"));
                                num_of_connection.setText(user.optString("friends_count"));
                                num_of_views.setText(user.optString("view_count"));
                                description.setText(StringEscapeUtils.unescapeJava(user.optString("description")));
                                company.setText(user.optString("designation"));
                                grpimageprogressbar.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity()).load(user.optString("profile_image")).
                                        into(user_image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        grpimageprogressbar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        grpimageprogressbar.setVisibility(View.GONE);
                                    }
                                });

                                ///getFrd list
                               final JSONArray post = jsonObject1.optJSONArray("post");


                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        try {
                                            realm.where(MyPostObject.class).contains("post_user_fk",sm.getUserObject().getUser_id()).findAll().clear();
                                            realm.createOrUpdateAllFromJson(MyPostObject.class, post);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            realm.close();
                                        }

                                    }
                                });


                                if(post.length()>0)
                                {
                                    ll_nodata.setVisibility(View.GONE);
                                }
                                else
                                {
                                    ll_nodata.setVisibility(View.VISIBLE);
                                }

                                myPostListAdapter.notifyDataSetChanged();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
//                        acProgressFlower.dismiss();

                    }
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.settings:

                final PopupMenu menu = new PopupMenu(getActivity(),view);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId())
                        {
                            case R.id.settings:
                                Intent intent = new Intent(getActivity(), Settings.class);
                                startActivity(intent);
                                break;
                        }

                        return true;
                    }
                });

                menu.inflate(R.menu.settings);
                menu.show();


                break;
            case R.id.btn_edt_profile:

                Intent intent1 = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent1);


                break;
            case R.id.ll_connection:

                Intent intent11 = new Intent(getActivity(), MyConnections.class);
                startActivity(intent11);
                break;

            case R.id.manageinviation:

                Intent intent12 = new Intent(getActivity(), ManageInvitation.class);
                startActivity(intent12);
                break;

            case R.id.user_image:

                Intent intent = new Intent(getActivity(), Full_ImageViewActivity.class);
                intent.putExtra("url",sm.getUserObject().getProfile_image());
                startActivity(intent);

                break;

        }

    }


    public class MyPostListAdapter extends BaseAdapter
    {
        RealmResults<MyPostObject> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public MyPostListAdapter(Context context,RealmResults<MyPostObject> suggestListObjectArrayList)
        {
            this.context = context;
            this.suggestListObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return suggestListObjectArrayList.size();
        }




        @Override
        public Object getItem(int size)
        {
            return suggestListObjectArrayList.get(size);
        }
        @Override
        public long getItemId(int size)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup)
        {

            View view = convertview;
            final ViewHolder viewHolder;
            if(view==null)
            {
                view = inflater.inflate(R.layout.mypostdesign,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.ll = (LinearLayout) view.findViewById(R.id.ll);
                viewHolder.txt_view = (BoldTextview) view.findViewById(R.id.txt_view);
                viewHolder.post_image = (ImageView) view.findViewById(R.id.post_image);
                viewHolder.rr_image = (RelativeLayout) view.findViewById(R.id.rr_image);
                viewHolder.grpimageprogressbar1 = (ProgressBar) view.findViewById(R.id.grpimageprogressbar1);
            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }

            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;

            int new_height = (int) (Screenwidth/10);
            new_height = (int) Math.round(new_height*3);
//            new_height = (int) Math.round(new_height);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.ll.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.ll.setLayoutParams(layoutParams);

            final MyPostObject suggestListObject = suggestListObjectArrayList.get(position);
            viewHolder.txt_view.setText(StringEscapeUtils.unescapeJava(suggestListObject.getMessage()));

            if(suggestListObject.getType().equalsIgnoreCase("1"))
            {
                viewHolder.rr_image.setVisibility(View.GONE);
                viewHolder.txt_view.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.rr_image.setVisibility(View.VISIBLE);
                viewHolder.txt_view.setVisibility(View.GONE);
                viewHolder.grpimageprogressbar1.setVisibility(View.VISIBLE);

                File_path file_path = suggestListObject.getFile_path().get(0);
                Picasso.with(context)
                        .load(file_path.getData())
                        .placeholder(R.drawable.profile_img)
                        .into(viewHolder.post_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                viewHolder.grpimageprogressbar1.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                viewHolder.grpimageprogressbar1.setVisibility(View.GONE);
                            }
                        });

            }

            view.setTag(viewHolder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(getActivity(),SingleFeedDetails.class);
                    intent.putExtra("post_pk",suggestListObject.getPost_pk());
                    startActivity(intent);

                }
            });

            return view;
        }

        class ViewHolder
        {
            ImageView post_image;
            LinearLayout ll;
            RelativeLayout rr_image;
            ProgressBar grpimageprogressbar1;
            BoldTextview txt_view,mentor,mentees,peer;
        }



    }
}

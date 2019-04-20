package com.edison.MainFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.ContactsListFriends;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.NearByPeople;
import com.edison.Object.PeopleYouMayKnow;
import com.edison.OtherUserProfileActivity;
import com.edison.R;
import com.edison.SearchActivity;
import com.edison.UpdateProfile;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.SimpleDividerItemDecoration;
import com.edison.Utils.Webservcie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.WINDOW_SERVICE;
import static android.os.Build.VERSION_CODES.M;


public class SearchFragment extends Fragment implements View.OnClickListener {


    RecyclerView recycler_mentor_list;
    View view;
    SessionManager sm;
    ACProgressFlower acProgressFlower;
    FontManager fm;

    LinearLayout ll_nearbypeople;
    Realm realm;
    SuggestListAdapter suggestListAdapter;
    BoldTextview import_contacts;
    boolean loading = false;
    int count = 0;
    private int VisibleItemCount, totalItemCount,firstVisiblesItems ;
    ProgressBar load_more_progrss;
    NormalTextview txtview_search,opencard;
    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private CardView cardBottomSheet;
    NormalTextview close_card;
    FrameLayout mainlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);



        return view;

    }


    @Override
    public void onResume() {

        INIT();
        super.onResume();
    }

    private void INIT() {

        sm = new SessionManager(getContext());
        fm = new FontManager(getActivity());
        recycler_mentor_list = (RecyclerView) view.findViewById(R.id.recycler_mentor_list);
        txtview_search = (NormalTextview) view.findViewById(R.id.txtview_search);
        opencard = (NormalTextview) view.findViewById(R.id.opencard);
        import_contacts = (BoldTextview) view.findViewById(R.id.import_contacts);
        mainlayout = (FrameLayout) view.findViewById(R.id.mainlayout);
        load_more_progrss = (ProgressBar) view.findViewById(R.id.load_more_progrss);
        load_more_progrss.setVisibility(View.GONE);
        ll_nearbypeople = (LinearLayout) view.findViewById(R.id.ll_nearbypeople);
//        recycler_suggesionlist = (ExpandableHeightListView) view.findViewById(R.id.recycler_suggesionlist);
        realm = Realm.getDefaultInstance();
        if (Build.VERSION.SDK_INT >= M) {
            mainlayout.getForeground().setAlpha(0);
        }
        ///
        ///bottom sheet
        cardBottomSheet = (CardView) view.findViewById(R.id.card_caf_details);
        close_card = (NormalTextview) cardBottomSheet.findViewById(R.id.close_card);
        ll_nearbypeople = (LinearLayout) cardBottomSheet.findViewById(R.id.ll_nearbypeople);
        bottomSheetBehavior = BottomSheetBehavior.from(cardBottomSheet);
        cardBottomSheet.setVisibility(View.GONE);

        ll_nearbypeople.setOnClickListener(this);
        txtview_search.setOnClickListener(this);
        import_contacts.setOnClickListener(this);

        RealmResults<PeopleYouMayKnow> homeObjectRealmResults = realm.where(PeopleYouMayKnow.class).findAll();
        System.out.println(">>>>>homeObjectRebeforealmResults>>>>>>home " + homeObjectRealmResults.size());
        suggestListAdapter = new SuggestListAdapter(getActivity(), homeObjectRealmResults);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_mentor_list.setLayoutManager(layoutManager);
        recycler_mentor_list.setItemAnimator(new DefaultItemAnimator());
        recycler_mentor_list.setAdapter(suggestListAdapter);
        recycler_mentor_list.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recycler_mentor_list.setHasFixedSize(true);
        recycler_mentor_list.setItemViewCacheSize(20);
        recycler_mentor_list.setDrawingCacheEnabled(true);
        recycler_mentor_list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        if (NetworkDetector.isNetworkStatusAvialable(getActivity())) {

            getList();
            acProgressFlower = new ACProgressFlower.Builder(getActivity())
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("")
                    .fadeColor(Color.DKGRAY).build();
            acProgressFlower.setCancelable(false);
        } else {
            Toast.makeText(getActivity(), "Please Check your Internet Connections ", Toast.LENGTH_SHORT).show();
        }
        if(sm.getUserObject()!=null)
        {
            if(sm.getUserObject().getName()!=null&&sm.getUserObject().getName().length()>0)
            {

            }else {

                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);

            }
        }

        opencard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowAlert(true);

            }
        });

        close_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowAlert(false);

            }
        });


        recycler_mentor_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                firstVisiblesItems  = layoutManager.findFirstVisibleItemPosition();
                VisibleItemCount = layoutManager.getChildCount();

                if(!loading&& (VisibleItemCount+firstVisiblesItems) >= totalItemCount)
                {
                    ///get home feed
                    if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                    {
                        count = count+10;
                        getList();
                        loading =true;
                        load_more_progrss.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    public void ShowAlert(boolean b) {
        if(b)
        {
            if (Build.VERSION.SDK_INT >= M) {
                mainlayout.getForeground().setAlpha(255);
            }
            cardBottomSheet.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else
        {
            if (Build.VERSION.SDK_INT >= M) {
                mainlayout.getForeground().setAlpha(0);
            }
            cardBottomSheet.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_nearbypeople:

            if (Build.VERSION.SDK_INT >= M) {
                if (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(getActivity(), NearByPeople.class);
                    startActivity(intent);

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            },
                            1);
                }
            }
            else {
                Intent intent = new Intent(getActivity(), NearByPeople.class);
                startActivity(intent);
            }

            break;


            case R.id.txtview_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.import_contacts:
                Intent intentimport_contacts = new Intent(getActivity(), ContactsListFriends.class);
                startActivity(intentimport_contacts);
                break;
        }

    }



    private void getList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("start",count);
            jsonObject.accumulate("end","10");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        System.out.println("^%@& "+jsonObject);

        AndroidNetworking.post(Webservcie.get_suggession)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        load_more_progrss.setVisibility(View.GONE);
                        acProgressFlower.dismiss();
                        try {

                            if (response.optString("success").equalsIgnoreCase("1")) {
                                loading = false;
                                ///// getSuggestionlist
                                final JSONArray suggessions = response.optJSONArray("user");
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        try {
                                            if(count == 0)
                                            {
                                                realm.clear(PeopleYouMayKnow.class);
                                            }

                                            realm.createOrUpdateAllFromJson(PeopleYouMayKnow.class, suggessions);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            realm.close();
                                        }

                                    }
                                });

                                suggestListAdapter.notifyDataSetChanged();


                            } else {
                                if(loading == true)
                                {
                                    Toast.makeText(getActivity(), "End of Suggestions", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {

                        acProgressFlower.dismiss();
                        System.out.println("ERROR>>>>>>>    " + anError);

                    }
                });


    }


    public class SuggestListAdapter extends RecyclerView.Adapter<SuggestListAdapter.MyViewHolder> {

        RealmResults<PeopleYouMayKnow> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;
        public void RefreshData(final int pos, final String diff)
        {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if(diff.equalsIgnoreCase("3"))
                    {
                        suggestListObjectArrayList.get(pos).setFriend_status("0");
                        notifyDataSetChanged();
                    }
                    else
                    {
                        suggestListObjectArrayList.get(pos).setFriend_status("3");
                        notifyDataSetChanged();
                    }

                }
            });


        }

//
        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            CircleImageView request_img;
            ProgressBar request_progress;
            BoldTextview request_name;
            NormalTextview ttl_views,follow, ttl_post, ttl_connection, request_designation;

            public MyViewHolder(View view) {
                super(view);

                request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
                ttl_connection = (NormalTextview) view.findViewById(R.id.ttl_connection);
                ttl_views = (NormalTextview) view.findViewById(R.id.ttl_views);
                ttl_post = (NormalTextview) view.findViewById(R.id.ttl_post);
                request_name = (BoldTextview) view.findViewById(R.id.request_name);
                follow = (NormalTextview) view.findViewById(R.id.follow);
                request_img = (CircleImageView) view.findViewById(R.id.request_img);
                request_progress = (ProgressBar) view.findViewById(R.id.request_progress);
            }
        }

        public SuggestListAdapter(Context context,RealmResults<PeopleYouMayKnow> suggestListObjectArrayList) {
            this.suggestListObjectArrayList = suggestListObjectArrayList;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.suggestlistdesginnew, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {





            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;


            int new_height = Screenwidth / 10;
            new_height = (int) Math.round(new_height * 1.3);
            android.view.ViewGroup.LayoutParams layoutParams = holder.request_img.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            holder.request_img.setLayoutParams(layoutParams);

            final PeopleYouMayKnow suggestListObject = suggestListObjectArrayList.get(position);
            holder.request_name.setText(suggestListObject.getName());
            if(suggestListObject.getDesignation().length()>0)
            {
                holder.request_designation.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.request_designation.setVisibility(View.GONE);
            }
            holder.request_designation.setText(suggestListObject.getDesignation());
            holder.request_progress.setVisibility(View.VISIBLE);
//            String connetion = "<b> <font color='black'>" + suggestListObject.getFriends_count() + " " +
//                    "</font></b> " + " \nconnection";
//            String post = "<b> <font color='black'>" + suggestListObject.getPost_count() + " " +
//                    "</font></b> " + " \npost";
//            String views = "<b> <font color='black'>" + suggestListObject.getViews_count() + " " +
//                    "</font></b> ";
            holder.ttl_connection.setText(suggestListObject.getFriends_count());
            holder.ttl_views.setText(suggestListObject.getViews_count());
            holder.ttl_post.setText(suggestListObject.getPost_count());

            if (suggestListObject.getFriend_status().equalsIgnoreCase("0")) {
                holder.follow.setText("Connect");
                holder.follow.setTextColor(getResources().getColor(R.color.white));
                holder.follow.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
            } else {
                holder.follow.setText("Invitation sent");
                holder.follow.setTextColor(getResources().getColor(R.color.black));
                holder.follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
            }

            Picasso.with(context)
                    .load(suggestListObject.getProfile_image())
                    .placeholder(R.drawable.profile_img)
                    .into(holder.request_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.request_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.request_progress.setVisibility(View.GONE);
                        }
                    });

            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(suggestListObject.getFriend_status().equalsIgnoreCase("0"))
                    {
                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                        {
                            SendOrWithdrawInvitation(suggestListObjectArrayList.get(position).getUser_id(),
                                    position,"1");
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.app_name)
//                                .setMessage("Are you sure want to withdraw invitation ?")
                                .setMessage("Are you sure you want to withdraw invitation for" +
                                        " "+suggestListObjectArrayList.get(position).getName()+"?")
                                .setPositiveButton("withdraw", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                                        {
                                            SendOrWithdrawInvitation(suggestListObjectArrayList.get(position).getUser_id(),
                                                    position,"3");
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }

                }
            });

            holder.request_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                    intent.putExtra("id",suggestListObject.getUser_id());
                    intent.putExtra("name",suggestListObject.getName());
                    startActivity(intent);

                }
            });
            holder.request_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                    intent.putExtra("id",suggestListObject.getUser_id());
                    intent.putExtra("name",suggestListObject.getName());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return suggestListObjectArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(suggestListObjectArrayList.get(position).getUser_id());
        }


    }



//    public class SuggestListAdapter extends BaseAdapter {
//        RealmResults<PeopleYouMayKnow> suggestListObjectArrayList;
//        Context context;
//        LayoutInflater inflater;
//
//        public SuggestListAdapter(Context context, RealmResults<PeopleYouMayKnow> suggestListObjectArrayList) {
//            this.context = context;
//            this.suggestListObjectArrayList = suggestListObjectArrayList;
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return suggestListObjectArrayList.size();
//        }
//
//        public void RefreshData(final int pos, final String diff)
//        {
//            realm.executeTransaction(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//
//                    if(diff.equalsIgnoreCase("3"))
//                    {
//                        suggestListObjectArrayList.get(pos).setFriend_status("0");
//                        notifyDataSetChanged();
//                    }
//                    else
//                    {
//                        suggestListObjectArrayList.get(pos).setFriend_status("3");
//                        notifyDataSetChanged();
//                    }
//
//                }
//            });
//
//
//        }
//
//        @Override
//        public Object getItem(int size) {
//            return suggestListObjectArrayList.get(size);
//        }
//
//        @Override
//        public long getItemId(int size) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertview, ViewGroup viewGroup) {
//
//            View view = convertview;
//            final ViewHolder viewHolder;
//            if (view == null) {
//                view = inflater.inflate(R.layout.suggestlistdesginnew, viewGroup, false);
//                viewHolder = new ViewHolder();
//                viewHolder.request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
//                viewHolder.ttl_connection = (NormalTextview) view.findViewById(R.id.ttl_connection);
//                viewHolder.ttl_views = (NormalTextview) view.findViewById(R.id.ttl_views);
//                viewHolder.ttl_post = (NormalTextview) view.findViewById(R.id.ttl_post);
//                viewHolder.request_name = (BoldTextview) view.findViewById(R.id.request_name);
//                viewHolder.follow = (BoldTextview) view.findViewById(R.id.follow);
//                viewHolder.request_img = (CircleImageView) view.findViewById(R.id.request_img);
//                viewHolder.request_progress = (ProgressBar) view.findViewById(R.id.request_progress);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//
//            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//            final DisplayMetrics displayMetrics = new DisplayMetrics();
//            wm.getDefaultDisplay().getMetrics(displayMetrics);
//            int Screenwidth = displayMetrics.widthPixels;
//
//
//            int new_height = Screenwidth / 10;
//            new_height = (int) Math.round(new_height * 1.5);
//            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.request_img.getLayoutParams();
//            layoutParams.width = new_height;
//            layoutParams.height = (int) new_height;
//            viewHolder.request_img.setLayoutParams(layoutParams);
//
//            final PeopleYouMayKnow suggestListObject = suggestListObjectArrayList.get(position);
//            viewHolder.request_name.setText(suggestListObject.getName());
//            viewHolder.request_designation.setText(suggestListObject.getDesignation());
//            viewHolder.request_progress.setVisibility(View.VISIBLE);
////            String connetion = "<b> <font color='black'>" + suggestListObject.getFriends_count() + " " +
////                    "</font></b> " + " \nconnection";
////            String post = "<b> <font color='black'>" + suggestListObject.getPost_count() + " " +
////                    "</font></b> " + " \npost";
////            String views = "<b> <font color='black'>" + suggestListObject.getViews_count() + " " +
////                    "</font></b> ";
//            viewHolder.ttl_connection.setText(suggestListObject.getFriends_count());
//            viewHolder.ttl_views.setText(suggestListObject.getViews_count());
//            viewHolder.ttl_post.setText(suggestListObject.getPost_count());
//
//            if (suggestListObject.getFriend_status().equalsIgnoreCase("0")) {
//                viewHolder.follow.setText("Connect");
//                viewHolder.follow.setTextColor(getResources().getColor(R.color.white));
//                viewHolder.follow.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
//            } else {
//                viewHolder.follow.setText("Invitation sent");
//                viewHolder.follow.setTextColor(getResources().getColor(R.color.black));
//                viewHolder.follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
//            }
//
//            Picasso.with(context)
//                    .load(suggestListObject.getProfile_image())
//                    .placeholder(R.drawable.profile_img)
//                    .into(viewHolder.request_img, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            viewHolder.request_progress.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError() {
//                            viewHolder.request_progress.setVisibility(View.GONE);
//                        }
//                    });
//
//            viewHolder.follow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if(suggestListObject.getFriend_status().equalsIgnoreCase("0"))
//                    {
//                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
//                        {
//                            SendOrWithdrawInvitation(suggestListObjectArrayList.get(position).getUser_id(),
//                                    position,"1");
//                        }
//                        else
//                        {
//                            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else
//                    {
//                        new AlertDialog.Builder(getActivity())
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setTitle(R.string.app_name)
//                                .setMessage("Are you sure want to withdraw invitation ?")
//                                .setPositiveButton("withdraw", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
//                                        {
//                                            SendOrWithdrawInvitation(suggestListObjectArrayList.get(position).getUser_id(),
//                                                    position,"3");
//                                        }
//                                        else
//                                        {
//                                            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                })
//                                .setNegativeButton("Cancel", null)
//                                .show();
//                    }
//
//                }
//            });
//
//            viewHolder.request_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
//                    intent.putExtra("id",suggestListObject.getUser_id());
//                    intent.putExtra("name",suggestListObject.getName());
//                    startActivity(intent);
//
//                }
//            });
//            viewHolder.request_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
//                    intent.putExtra("id",suggestListObject.getUser_id());
//                    intent.putExtra("name",suggestListObject.getName());
//                    startActivity(intent);
//
//                }
//            });
////
//
//            view.setTag(viewHolder);
//            return view;
//        }
//
//        class ViewHolder {
//            CircleImageView request_img;
//            ProgressBar request_progress;
//            BoldTextview request_name, follow;
//            NormalTextview ttl_views, ttl_post, ttl_connection, request_designation;
//        }
//
//
//    }

//    public void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }

    private void SendOrWithdrawInvitation(String user_id, final int position, final String status) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",user_id);
            jsonObject.accumulate("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);
        AndroidNetworking.post(Webservcie.request_send)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                suggestListAdapter.RefreshData(position,status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });
    }
}

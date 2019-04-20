package com.edison.MainFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.MyFriendsList;
import com.edison.OtherUserProfileActivity;
import com.edison.R;
import com.edison.SearchActivity;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.View.VISIBLE;


public class SearchUserFragment extends Fragment {

    SessionManager sm;
    AutoCompleteTextView searchuser;
    AutocompleteCustomArrayAdapter customerAdapter;
    BoldTextview nodata;
    FontManager fm;
    ArrayList<MyFriendsList> suggestListArrayList = new ArrayList<>();

    //    FilterListAdapter filterListAdapter;
    ListView list_item;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_user, container, false);

        INIT();


        return view;
    }

    private void INIT() {


        sm = new SessionManager(getActivity());
        fm = new FontManager(getActivity());
        searchuser = (AutoCompleteTextView) view.findViewById(R.id.searchuser);
        nodata = (BoldTextview) view.findViewById(R.id.nodata);
        list_item = (ListView) view.findViewById(R.id.list_item);
        searchuser.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {
                if (NetworkDetector.isNetworkStatusAvialable(getActivity()))
                {if(s.length()>0)
                {
                    getUser_List(s.toString(),"1");
                }

                }
                else
                {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });



    }



    private void  getUser_List(String s, String s1) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("search_value", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        AndroidNetworking.post(Webservcie.search_user)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {




                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {

                                try {
                                    customerAdapter = new AutocompleteCustomArrayAdapter(getContext(),
                                            R.layout.suggestlistdesginnew,R.id.request_name,suggestListArrayList);
                                    list_item.setAdapter(customerAdapter);
//                                        list_item.showDropDown();

                                    customerAdapter.notifyDataSetChanged();

                                    nodata.setVisibility(View.GONE);
                                    list_item.setVisibility(View.VISIBLE);
                                }
                                catch (Exception e)
                                {

                                }

                                final JSONArray user = jsonObject1.optJSONArray("users");
                                suggestListArrayList.clear();
                                if(user.length()>0)
                                {

                                    MyFriendsList suggestList = null;
                                    for (int i=0; i<user.length();i++)
                                    {
                                        suggestList = new MyFriendsList();
                                        suggestList.setName(user.optJSONObject(i).optString("name"));
                                        suggestList.setUsername(user.optJSONObject(i).optString("username"));
                                        suggestList.setUser_id(user.optJSONObject(i).optString("user_id"));
                                        suggestList.setProfile_image(user.optJSONObject(i).optString("profile_image"));
                                        suggestList.setDesignation(user.optJSONObject(i).optString("designation"));
                                        suggestList.setFriend_status(user.optJSONObject(i).optString("friend_status"));
                                        suggestList.setFriends_count(user.optJSONObject(i).optString("friends_count"));
                                        suggestList.setViews_count(user.optJSONObject(i).optString("views_count"));
                                        suggestList.setPost_count(user.optJSONObject(i).optString("post_count"));

                                        suggestListArrayList.add(suggestList);
                                    }

                                    try {
//                                        customerAdapter = new AutocompleteCustomArrayAdapter(SearchActivity.this,
//                                                R.layout.suggestlistdesgin,R.id.request_name,suggestListArrayList);
//                                        list_item.setAdapter(customerAdapter);
////                                        list_item.showDropDown();

                                        customerAdapter.notifyDataSetChanged();

                                        nodata.setVisibility(View.GONE);
                                        list_item.setVisibility(View.VISIBLE);
                                    }
                                    catch (Exception e)
                                    {

                                    }

                                }
                                else
                                {
                                    customerAdapter.notifyDataSetChanged();
                                    nodata.setVisibility(VISIBLE);
                                    list_item.setVisibility(View.GONE);
                                }


                            }
                            else
                            {
                                customerAdapter.notifyDataSetChanged();
                                nodata.setVisibility(VISIBLE);
                                list_item.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }




    public class AutocompleteCustomArrayAdapter extends ArrayAdapter<MyFriendsList> {

        final String TAG = "AutocompleteCustomArrayAdapter.java";
        ArrayList<MyFriendsList> tempItems, suggestions;
        Context mContext;
        int layoutResourceId,textViewResourceId;
        ArrayList<MyFriendsList> items;



        public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId,int textViewResourceId,
                                              ArrayList<MyFriendsList> data) {
            super(mContext, layoutResourceId,textViewResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.items = data;
            this.textViewResourceId = textViewResourceId;
            tempItems = new ArrayList<MyFriendsList>(items); // this makes the difference.
            suggestions = new ArrayList<MyFriendsList>();
        }

        public void RefreshData(int position,int diff)
        {
            if(diff==1)
            {
                this.items.get(position).setFriend_status("1");
                notifyDataSetChanged();
            }
            else if(diff==2)
            {
                this.items.get(position).setFriend_status("2");
                notifyDataSetChanged();
            }
            else
            {
                this.items.get(position).setFriend_status("0");
                notifyDataSetChanged();
            }

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.suggestlistdesginnew, parent, false);
            }
            final MyFriendsList people = items.get(position);
            if (people != null) {

                NormalTextview request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
                NormalTextview ttl_connection = (NormalTextview) view.findViewById(R.id.ttl_connection);
                NormalTextview ttl_views = (NormalTextview) view.findViewById(R.id.ttl_views);
                NormalTextview ttl_post = (NormalTextview) view.findViewById(R.id.ttl_post);
                BoldTextview request_name = (BoldTextview) view.findViewById(R.id.request_name);
                NormalTextview follow = (NormalTextview) view.findViewById(R.id.follow);
                LinearLayout ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
                CircleImageView request_img = (CircleImageView) view.findViewById(R.id.request_img);
                final ProgressBar request_progress = (ProgressBar) view.findViewById(R.id.request_progress);


                WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                final DisplayMetrics displayMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int Screenwidth = displayMetrics.widthPixels;


                int new_height = Screenwidth/10;
                new_height = (int) Math.round(new_height*1.5);
                android.view.ViewGroup.LayoutParams layoutParams = request_img.getLayoutParams();
                layoutParams.width = new_height;
                layoutParams.height = (int) new_height;
                request_img.setLayoutParams(layoutParams);


                String connetion = "<b> <font color='black'>" + people.getFriends_count() + " " +
                        "</font></b> " + " \nconnection";
                String post = "<b> <font color='black'>" + people.getPost_count() + " " +
                        "</font></b> " + " \npost";
                String views = "<b> <font color='black'>" + people.getViews_count() + " " +
                        "</font></b> " + " \nviews";
                ttl_connection.setText(people.getFriends_count());
                ttl_views.setText(people.getViews_count());
                ttl_post.setText(people.getPost_count());




                request_progress.setVisibility(VISIBLE);
                /////picasso
                Picasso.with(getContext()).load(people.getProfile_image())
                        .placeholder(R.drawable.profile_img)
//                  .transform(new BlurTransformation(Global_feed_Activity.this, 25, 3))
                        .into(request_img, new Callback() {
                            @Override
                            public void onSuccess() {
                                request_progress.setVisibility(View.GONE);

                            }
                            @Override
                            public void onError() {
                                request_progress.setVisibility(View.GONE);
                            }
                        });

                if (request_name != null)
                    request_designation.setText(people.getDesignation());
                request_name.setText(people.getName());

                if(people.getFriend_status().equalsIgnoreCase("0"))
                {
                follow.setText("Connect");
                follow.setVisibility(View.VISIBLE);
                    ll_bg.setVisibility(View.VISIBLE);
                follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                }
                else if(people.getFriend_status().equalsIgnoreCase("2"))
                {
                    follow.setText("Invitation sent");
                    follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                    follow.setVisibility(View.VISIBLE);
                    ll_bg.setVisibility(View.VISIBLE);
                }
                else if(people.getFriend_status().equalsIgnoreCase("3"))
                {
                    follow.setText("Accept");
                    follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                    follow.setVisibility(View.VISIBLE);
                    ll_bg.setVisibility(View.VISIBLE);
                }
                else
                {
                    follow.setVisibility(View.GONE);
                    ll_bg.setVisibility(View.GONE);

                }


                follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (NetworkDetector.isNetworkStatusAvialable(getActivity()))
                        {
                            if(people.getFriend_status().equalsIgnoreCase("0"))
                            {
                                sendRequest(people.getUser_id(),"1 ",position);
                            }
                            else if(people.getFriend_status().equalsIgnoreCase("2"))
                            {

                                new android.support.v7.app.AlertDialog.Builder(getActivity())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.app_name)
                                        .setMessage("Are you sure want to withdraw invitation ?")
                                        .setPositiveButton("withdraw", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                cancelreq(people.getUser_id(),"3",position);
                                            }

                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                            else if(people.getFriend_status().equalsIgnoreCase("3"))
                            {
                                Accept_Or_Reject(people.getUser_id(),"2",position,"2");
                            }



                        } else {
                            Toast.makeText(getActivity(), "Please check your Internet Connection",
                                    Toast.LENGTH_SHORT).show();
                        }





                    }
                });


            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(),OtherUserProfileActivity.class);
                    intent.putExtra("id",people.getUser_id());
                    intent.putExtra("name",people.getName());
                    startActivity(intent);
                }
            });

            return view;
        }
        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        /**
         * Custom Filter implementation for custom suggestions we provide.
         */
        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((MyFriendsList) resultValue).getUser_id();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (MyFriendsList people : tempItems) {
                        if (people.getUser_id().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<MyFriendsList> filterList = (ArrayList<MyFriendsList>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (MyFriendsList people : filterList) {
                        add(people);
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }


    private void sendRequest(String id, final String status,final int position) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",id);
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
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                customerAdapter.RefreshData(position,2);
                                Toast.makeText(getActivity(), "Request has been sent", Toast.LENGTH_SHORT).show();
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

    private void cancelreq(String user_id, String status, final int pos) {

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
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                customerAdapter.RefreshData(pos,0);
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



    private void Accept_Or_Reject(String user_id, String s, final int position, final String accept) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("follower_id",user_id);
            jsonObject.accumulate("status", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.request_response)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                customerAdapter.RefreshData(position,1);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }



}

package com.edison;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.MyFriendsList;
import com.edison.Object.TempIBobjt;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
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

public class MyConnections extends AppCompatActivity {

    TextView header,nodatasearch;
    FontManager fm;
    ImageView back;
    LinearLayout nodata_layout;
    SessionManager sm;
    ListView friendsList;
    ArrayList<MyFriendsList> myFrdObjectList;
    ACProgressFlower acProgressFlower;
    //    MyFrdListAdapter myFrdListAdapter;
    Button find_friends;
    RelativeLayout ll_search;

    ImageView searchicon,clear;
    AutoCompleteTextView edt_frds;
    AutocompleteCustomArrayAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_connections);
    }

    @Override
    protected void onResume() {
        super.onResume();
        INIT();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void INIT() {

        sm = new SessionManager(this);
        fm = new FontManager(this);
        myFrdObjectList = new ArrayList<>();

        searchicon = (ImageView) findViewById(R.id.searchicon);
        back = (ImageView) findViewById(R.id.back);
        clear = (ImageView) findViewById(R.id.clear);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        edt_frds = (AutoCompleteTextView) findViewById(R.id.edt_clg);
        ll_search = (RelativeLayout) findViewById(R.id.ll_search);
        header = (TextView) findViewById(R.id.header);
        nodatasearch = (TextView) findViewById(R.id.nodatasearch);
        find_friends = (Button) findViewById(R.id.find_friends);
        friendsList = (ListView) findViewById(R.id.friendsList);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int Screenwidth = displayMetrics.widthPixels;




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_search.setVisibility(View.VISIBLE);
                header.setVisibility(View.GONE);
                searchicon.setVisibility(View.GONE);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edt_frds.getText().toString().length()>0)
                {
                    edt_frds.getText().clear();
                }else
                {
                    ll_search.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    searchicon.setVisibility(View.VISIBLE);
                    hideKeyboard(MyConnections.this);
                }


            }
        });

        find_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyConnections.this,SearchActivity.class);
                startActivity(intent);

            }
        });
        nodata_layout.setVisibility(View.GONE);


        if (NetworkDetector.isNetworkStatusAvialable(MyConnections.this)) {

            getFrdList("","0");
            acProgressFlower = new ACProgressFlower.Builder(MyConnections.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("")
                    .fadeColor(Color.DKGRAY).build();
            acProgressFlower.setCancelable(false);
        } else {
            Toast.makeText(this, "Please Check your Internet Connections ", Toast.LENGTH_SHORT).show();
        }


        edt_frds.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s)
            {
                if (NetworkDetector.isNetworkStatusAvialable(MyConnections.this))
                {
//                    if(s.length()>0)
//                {
                    getFrdList(s.toString(),"1");
//                }

                }
                else
                {
                    Toast.makeText(MyConnections.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
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


    private void getFrdList(String s, final String s1) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("search_value", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.get_friends)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        acProgressFlower.dismiss();

                        try {


                            if (response.optString("success").equalsIgnoreCase("1")) {
                                MyFriendsList friendObject = null;
                                JSONArray data = response.optJSONArray("chat_users");

                                myFrdObjectList.clear();
                                for (int i = 0; i < data.length(); i++) {

                                    friendObject = new MyFriendsList();
                                    friendObject.setDesignation(data.optJSONObject(i).optString("designation"));
                                    friendObject.setName(data.optJSONObject(i).optString("name"));
                                    friendObject.setUser_id(data.optJSONObject(i).optString("user_id"));
                                    friendObject.setUser_is_live(data.optJSONObject(i).optString("user_is_live"));
                                    friendObject.setProfile_image(data.optJSONObject(i).optString("profile_image"));
                                    friendObject.setUser_company(data.optJSONObject(i).optString("user_company"));
                                    friendObject.setDialog_id(data.optJSONObject(i).optString("dialog_id"));

                                    myFrdObjectList.add(friendObject);
                                }

                                customerAdapter = new AutocompleteCustomArrayAdapter(MyConnections.this,
                                        R.layout.autolistdesign,R.id.companyname,myFrdObjectList);
                                friendsList.setAdapter(customerAdapter);
                                customerAdapter.notifyDataSetChanged();

//                                myFrdListAdapter = new MyFrdListAdapter(MyFriendsList.this, myFrdObjectList);
//                                friendsList.setAdapter(myFrdListAdapter);

                                nodata_layout.setVisibility(View.GONE);
                                nodatasearch.setVisibility(View.GONE);
                                friendsList.setVisibility(View.VISIBLE);
                            } else {

                                if(s1.equalsIgnoreCase("1"))
                                {
                                    nodatasearch.setVisibility(View.VISIBLE);
                                    nodata_layout.setVisibility(View.GONE);
                                }
                                else
                                {
                                    nodatasearch.setVisibility(View.GONE);
                                    nodata_layout.setVisibility(View.VISIBLE);
                                }
                                friendsList.setVisibility(View.GONE);
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


    public class AutocompleteCustomArrayAdapter extends ArrayAdapter<MyFriendsList> {

        final String TAG = "AutocompleteCustomArrayAdapter.java";
        ArrayList<MyFriendsList> tempItems, suggestions;
        Context mContext;
        int layoutResourceId,textViewResourceId;
        ArrayList<MyFriendsList> items;



        public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId,int textViewResourceId, ArrayList<MyFriendsList> data) {
            super(mContext, layoutResourceId,textViewResourceId, data);

            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.items = data;
            this.textViewResourceId = textViewResourceId;
            tempItems = new ArrayList<MyFriendsList>(items); // this makes the difference.
            suggestions = new ArrayList<MyFriendsList>();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.myfrdlistdesign, parent, false);
            }
            final MyFriendsList people = items.get(position);
            if (people != null) {

                CircleImageView request_img;
                final ProgressBar request_progress;
                LinearLayout btm_layout;
                BoldTextview request_name, onlinestatus, chat, voicecall, videocall;
                RelativeLayout rl_image;
                ImageView chat_image;
                NormalTextview request_designation;
                View dividerlength;


                onlinestatus = (BoldTextview) view.findViewById(R.id.onlinestatus);
                request_name = (BoldTextview) view.findViewById(R.id.request_name);
                request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
                chat = (BoldTextview) view.findViewById(R.id.chat);
                voicecall = (BoldTextview) view.findViewById(R.id.voicecall);
                videocall = (BoldTextview) view.findViewById(R.id.videocall);
                btm_layout = (LinearLayout) view.findViewById(R.id.btm_layout);
                request_img = (CircleImageView) view.findViewById(R.id.request_img);
                chat_image = (ImageView) view.findViewById(R.id.chat_image);
                request_progress = (ProgressBar) view.findViewById(R.id.request_progress);
                rl_image = (RelativeLayout) view.findViewById(R.id.rl_image);
                dividerlength = (View) view.findViewById(R.id.dividerlength);


//                fm.setChatIcon(chat);
//                fm.setCallicon(voicecall);
//                fm.setVideoicon(videocall);

                chat.setVisibility(View.GONE);
                voicecall.setVisibility(View.GONE);
                videocall.setVisibility(View.GONE);





                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                final DisplayMetrics displayMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int Screenwidth = displayMetrics.widthPixels;


                int new_height = Screenwidth / 10;
                new_height = (int) Math.round(new_height * 1.2);
                android.view.ViewGroup.LayoutParams layoutParams = rl_image.getLayoutParams();
                layoutParams.width = new_height;
                layoutParams.height = (int) new_height;
                rl_image.setLayoutParams(layoutParams);

                try {
                    final MyFriendsList suggestListObject = myFrdObjectList.get(position);
                    request_name.setText(suggestListObject.getName());
                    request_designation.setText(suggestListObject.getDesignation());
                    if (suggestListObject.getUser_is_live().equalsIgnoreCase("1")) {
                        onlinestatus.setVisibility(View.VISIBLE);
                        onlinestatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.online, 0, 0, 0);
                    } else {
                        onlinestatus.setVisibility(View.GONE);
                    }
                    request_progress.setVisibility(View.VISIBLE);

                    Picasso.with(getContext())
                            .load(suggestListObject.getProfile_image())
                            .placeholder(R.drawable.profile_img)
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

                    chat_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if(suggestListObject.getDialog_id().length()>0)
                            {
                                TempIBobjt tempIBobjt = new TempIBobjt();
                                tempIBobjt.setDialog_id(suggestListObject.getDialog_id());
                                tempIBobjt.setGroup_id(suggestListObject.getUser_id());
                                tempIBobjt.setGroup_image(suggestListObject.getProfile_image());
                                tempIBobjt.setGroup_name(suggestListObject.getName());
                                tempIBobjt.setQuickblox_id(suggestListObject.getQuickblox_id());
                                Intent intent = new Intent(MyConnections.this, OneToOneChat.class);
                                intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                                startActivity(intent);
                            }
                            else
                            {
                                JSONObject jsonObject = new JSONObject();

                                try {
                                    jsonObject.accumulate("sender_id",sm.getUserObject().getUser_id());
                                    jsonObject.accumulate("receiver_id",suggestListObject.getUser_id());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AndroidNetworking.post(Webservcie.dialog_create)
                                        .addJSONObjectBody(jsonObject)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {


                                                if(response.optString("success").equalsIgnoreCase("1"))
                                                {
                                                    TempIBobjt tempIBobjt = new TempIBobjt();
                                                    tempIBobjt.setDialog_id(response.optString("data"));
                                                    tempIBobjt.setGroup_id(suggestListObject.getUser_id());
                                                    tempIBobjt.setGroup_image(suggestListObject.getProfile_image());
                                                    tempIBobjt.setGroup_name(suggestListObject.getName());
                                                    tempIBobjt.setQuickblox_id(suggestListObject.getQuickblox_id());
                                                    Intent intent = new Intent(MyConnections.this, OneToOneChat.class);
                                                    intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                                                    startActivity(intent);
                                                }
                                                else
                                                {
                                                    Toast.makeText(MyConnections.this, "Try again later", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {

                                            }
                                        });
                            }




                        }

                    });


                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {




                            Intent intent = new Intent(MyConnections.this,OtherUserProfileActivity.class);
                            intent.putExtra("id",people.getUser_id());
                            intent.putExtra("name",people.getName());
                            startActivity(intent);
                        }
                    });


                } catch (Exception e) {

                }
            }



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

}

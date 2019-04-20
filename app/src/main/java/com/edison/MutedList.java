package com.edison;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Object.MyFriendsList;
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

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class MutedList extends AppCompatActivity {

    SessionManager sm;
    FontManager fm;
    TextView nodata;
    ListView mutedlist;
    ImageView backicon;
    BlocklistAdapter mutelistAdapter;
    ACProgressFlower acProgressFlower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muted_list);

    }
        @Override
        protected void onResume() {
            super.onResume();
            INIT();
        }

        private void INIT() {

            sm = new SessionManager(this);
            fm = new FontManager(this);
            backicon = (ImageView) findViewById(R.id.backicon);
            mutedlist = (ListView) findViewById(R.id.mutedlist);
            nodata = (TextView) findViewById(R.id.nodata);
            nodata.setVisibility(View.GONE);

            backicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });




            if (NetworkDetector.isNetworkStatusAvialable(MutedList.this)) {

                getFrdList("","0");
                acProgressFlower = new ACProgressFlower.Builder(MutedList.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(Color.WHITE)
                        .text("")
                        .fadeColor(Color.DKGRAY).build();
                acProgressFlower.setCancelable(false);
            } else {
                Toast.makeText(this, "Please Check your Internet Connections ", Toast.LENGTH_SHORT).show();
            }

        }

        private void getFrdList(String s, String s1) {


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            AndroidNetworking.post(Webservcie.mute_list)
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            acProgressFlower.dismiss();
                            try {


                                if (response.optString("success").equalsIgnoreCase("1")) {

                                    ArrayList<MyFriendsList> myFriendsLists = new ArrayList<>();

                                    MyFriendsList friendObject = null;
                                    JSONArray data = response.optJSONArray("mute_list");

                                    for (int i = 0; i < data.length(); i++) {

                                        friendObject = new MyFriendsList();
                                        friendObject.setDesignation(data.optJSONObject(i).optString("designation"));
                                        friendObject.setName(data.optJSONObject(i).optString("name"));
                                        friendObject.setUser_id(data.optJSONObject(i).optString("user_id"));
                                        friendObject.setProfile_image(data.optJSONObject(i).optString("profile_image"));
                                        myFriendsLists.add(friendObject);
                                    }


                                    mutelistAdapter = new BlocklistAdapter(MutedList.this, myFriendsLists);
                                    mutedlist.setAdapter(mutelistAdapter);

                                    nodata.setVisibility(View.GONE);
                                    mutedlist.setVisibility(View.VISIBLE);
                                } else {
                                    nodata.setVisibility(View.VISIBLE);
                                    mutedlist.setVisibility(View.GONE);
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


        public class BlocklistAdapter extends BaseAdapter {

            // Declare Variables
            Context context;
            LayoutInflater inflater;
            ArrayList<MyFriendsList> data;

            public BlocklistAdapter(Context context, ArrayList<MyFriendsList> arraylist) {
                this.context = context;
                this.inflater = LayoutInflater.from(context);
                data = arraylist;
            }

            public void refresh(ArrayList<MyFriendsList> items)
            {
                this.data = items;
                notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            public View getView(final int position, View convertView, ViewGroup parent) {
                // Declare Variables

                final ViewHolder viewHolder;
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.block_unblock_list_design, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.user_nameee = (TextView) convertView.findViewById(R.id.user_nameee1);
//                viewHolder.designation = (TextView) convertView.findViewById(R.id.designation);
                    viewHolder.user_imagee = (CircleImageView) convertView.findViewById(R.id.user_imageeee1);
                    viewHolder.mprogress = (ProgressBar) convertView.findViewById(R.id.mprogressw1);
                    viewHolder.unmutee = (TextView) convertView.findViewById(R.id.unblockk);
                    convertView.setTag(viewHolder);
                }
                else
                {
                    viewHolder = (ViewHolder) convertView.getTag();
                }


                final MyFriendsList result = data.get(position);
                viewHolder.unmutee.setText("UnMute");
                viewHolder.unmutee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (NetworkDetector.isNetworkStatusAvialable(MutedList.this))
                        {
                            new android.support.v7.app.AlertDialog.Builder(MutedList.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Are you sure want to UnMute "+ result.getName().toUpperCase()+"?")
                                    .setPositiveButton("UnMute", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (NetworkDetector.isNetworkStatusAvialable(MutedList.this)) {

                                                BlockUnblock(result.getUser_id());
                                                data.remove(position);
                                                refresh(data);
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Please check your Internet Connection",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();

                        }
                        else
                        {
                            Toast.makeText(MutedList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                viewHolder.user_nameee.setText(result.getName());
//            viewHolder.designation.setText(result.getDesignation());

                ///calculate new width and height
                WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
                final DisplayMetrics displayMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int width1 = displayMetrics.widthPixels;

                double width = Double.parseDouble(String.valueOf(width1));
                double height = width/10;
                height = height * 1.2;
                int new_height = (int) Math.round(height);
                android.view.ViewGroup.LayoutParams layoutParams = viewHolder.user_imagee.getLayoutParams();
                layoutParams.width = new_height;
                layoutParams.height = new_height;
                viewHolder.user_imagee.setLayoutParams(layoutParams);

                viewHolder.mprogress.setVisibility(View.VISIBLE);

                /////picasso
                Picasso.with(MutedList.this).load(result.getProfile_image())
                        .into(viewHolder.user_imagee, new Callback() {
                            @Override
                            public void onSuccess()
                            {
                                viewHolder.mprogress.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError() {
                            }
                        });




                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        Intent intent = new Intent(MutedList.this,OtherUserProfileActivity.class);
                        intent.putExtra("id",result.getUser_id());
                        intent.putExtra("name",result.getName());
                        startActivity(intent);
                    }
                });
                return convertView;

            }
        }

        static class ViewHolder {

            private TextView user_nameee,designation,unmutee;
            CircleImageView user_imagee;
            ProgressBar mprogress;
        }

        private void BlockUnblock(String id) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("follower_id", sm.getUserObject().getUser_id());
                jsonObject.accumulate("following_id",id);
                jsonObject.accumulate("status", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(BlockedList.this)
//                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
//                .themeColor(Color.WHITE)
//                .text("")
//                .fadeColor(Color.DKGRAY).build();
//        acProgressFlower.show();
//        acProgressFlower.setCancelable(false);

            AndroidNetworking.post(Webservcie.mute_unmute)
                    .addJSONObjectBody(jsonObject)
                    .setTag("login")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(final String response) {
//                        acProgressFlower.dismiss();

                            try {
                                JSONObject jsonObject1 = new JSONObject(response);
                                if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                                {
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
}

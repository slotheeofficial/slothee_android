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

public class MutualCnnectionList extends AppCompatActivity {

    SessionManager sm;
    FontManager fm;
    TextView nodata;
    ListView mutualconnectionlist;
    ImageView backicon;
    BlocklistAdapter mutelistAdapter;
    ACProgressFlower acProgressFlower;
    String otheruserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_cnnection_list);

        otheruserid = getIntent().getStringExtra("otheruserid");
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
        mutualconnectionlist = (ListView) findViewById(R.id.mutualconnectionlist);
        nodata = (TextView) findViewById(R.id.nodata);
        nodata.setVisibility(View.GONE);

        backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        if (NetworkDetector.isNetworkStatusAvialable(MutualCnnectionList.this)) {

            getFrdList("","0");
            acProgressFlower = new ACProgressFlower.Builder(MutualCnnectionList.this)
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
            jsonObject.accumulate("other_user_id",otheruserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.mutual_friends)
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
                                JSONArray data = response.optJSONArray("mutual_friends");

                                for (int i = 0; i < data.length(); i++) {

                                    friendObject = new MyFriendsList();
                                    friendObject.setDesignation(data.optJSONObject(i).optString("designation"));
                                    friendObject.setName(data.optJSONObject(i).optString("name"));
                                    friendObject.setUser_id(data.optJSONObject(i).optString("user_id"));
                                    friendObject.setProfile_image(data.optJSONObject(i).optString("profile_image"));
                                    myFriendsLists.add(friendObject);
                                }

                                mutelistAdapter = new BlocklistAdapter(MutualCnnectionList.this, myFriendsLists);
                                mutualconnectionlist.setAdapter(mutelistAdapter);

                                nodata.setVisibility(View.GONE);
                                mutualconnectionlist.setVisibility(View.VISIBLE);
                            } else {
                                nodata.setVisibility(View.VISIBLE);
                                mutualconnectionlist.setVisibility(View.GONE);
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



            viewHolder.user_nameee.setText(result.getName());
            viewHolder.unmutee.setVisibility(View.GONE);
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
            Picasso.with(MutualCnnectionList.this).load(result.getProfile_image())
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

                    Intent intent = new Intent(MutualCnnectionList.this,OtherUserProfileActivity.class);
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

}

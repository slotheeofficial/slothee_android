package com.edison;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.MyFriendsList;
import com.edison.Utils.FontManager;
import com.edison.Utils.GPSTracker;
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

import static android.view.View.VISIBLE;

public class OtherUserConnectionList extends AppCompatActivity {

    TextView header, nodatasearch;
    FontManager fm;
    ImageView back;
    LinearLayout nodata_layout;
    SessionManager sm;
    ListView near_by_frd_list;
    ArrayList<MyFriendsList> myFrdObjectList;
    ACProgressFlower acProgressFlower;
    OtherUserFrdList nearByfrdsAdapter;
    private GPSTracker gpsTracker;
    String otheruserid,otherusername;
    BoldTextview name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_connection_list);

        otherusername = getIntent().getStringExtra("otherusername");
        otheruserid = getIntent().getStringExtra("otheruserid");
        INIT();

    }

    private void INIT() {

        sm = new SessionManager(this);
        near_by_frd_list = (ListView) findViewById(R.id.near_by_frd_list);
        back = (ImageView) findViewById(R.id.back);
        name = (BoldTextview) findViewById(R.id.name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if (NetworkDetector.isNetworkStatusAvialable(OtherUserConnectionList.this)) {

            acProgressFlower = new ACProgressFlower.Builder(OtherUserConnectionList.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("")
                    .fadeColor(Color.DKGRAY).build();
            acProgressFlower.show();
            acProgressFlower.setCancelable(false);
            getFrdList();

        }
        else {
            Toast.makeText(OtherUserConnectionList.this,
                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
        name.setText(otherusername +" Connections");


    }

    private void getFrdList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", otheruserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.get_friends)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        acProgressFlower.cancel();
                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            JSONArray data = response.optJSONArray("chat_users");
                            if(data.length()>0)
                            {
                                MyFriendsList friendObject = null;
                                myFrdObjectList = new ArrayList<>();
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


                                nearByfrdsAdapter = new OtherUserFrdList(OtherUserConnectionList.this, myFrdObjectList);
                                near_by_frd_list.setAdapter(nearByfrdsAdapter);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.cancel();
                    }
                });

    }


    public class OtherUserFrdList extends BaseAdapter
    {
        ArrayList<MyFriendsList> myFrdObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public OtherUserFrdList(Context context,ArrayList<MyFriendsList> suggestListObjectArrayList)
        {
            this.context = context;
            this.myFrdObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return myFrdObjectArrayList.size();
        }
        public void RefreshData(int position,int diff)
        {
            if(diff==1)
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("1");
                notifyDataSetChanged();
            }
            else if(diff==2)
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("2");
                notifyDataSetChanged();
            }

            else
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("0");
                notifyDataSetChanged();
            }

        }

        @Override
        public Object getItem(int size)
        {
            return myFrdObjectArrayList.get(size);
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
                view = inflater.inflate(R.layout.myfrdlistdesign,viewGroup,false);
                viewHolder = new ViewHolder();

                viewHolder.onlinestatus = (BoldTextview) view.findViewById(R.id.onlinestatus);
                viewHolder.request_name = (BoldTextview) view.findViewById(R.id.request_name);
                viewHolder.request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
                viewHolder.request_img = (CircleImageView) view.findViewById(R.id.request_img);
                viewHolder.mprogress1 = (ProgressBar) view.findViewById(R.id.request_progress);
                viewHolder.rl_image = (RelativeLayout) view.findViewById(R.id.rl_image);
                viewHolder.chat_image = (ImageView) view.findViewById(R.id.chat_image);

            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }


            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;
            viewHolder.chat_image.setVisibility(View.GONE);

            int new_height = Screenwidth/10;
            new_height = (int) Math.round(new_height*1.5);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.request_img.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.request_img.setLayoutParams(layoutParams);

            final MyFriendsList suggestListObject = myFrdObjectArrayList.get(position);
            viewHolder.request_name.setText(suggestListObject.getName());
            viewHolder.request_designation.setText(suggestListObject.getDesignation());

            if(sm.getUserObject()!=null)
            {
                if(sm.getUserObject().getUser_id().equalsIgnoreCase(suggestListObject.getUser_id()))
                {
                    viewHolder.request_name.setTextColor(getResources().getColor(R.color.blue));
                }
                else
                {
                    viewHolder.request_name.setTextColor(getResources().getColor(R.color.black));
                }
            }

            viewHolder.mprogress1.setVisibility(VISIBLE);
            /////picasso
            Picasso.with(OtherUserConnectionList.this).load(suggestListObject.getProfile_image())
                    .placeholder(R.drawable.profile_img)
//                  .transform(new BlurTransformation(Global_feed_Activity.this, 25, 3))
                    .into(viewHolder.request_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.mprogress1.setVisibility(View.GONE);

                        }
                        @Override
                        public void onError() {
                            viewHolder.mprogress1.setVisibility(View.GONE);
                        }
                    });





            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(sm.getUserObject()!=null)
                    {
                        if(sm.getUserObject().getUser_id().equalsIgnoreCase(suggestListObject.getUser_id()))
                        {

                        }
                        else
                        {
                            Intent intent = new Intent(OtherUserConnectionList.this,OtherUserProfileActivity.class);
                            intent.putExtra("id",suggestListObject.getUser_id());
                            intent.putExtra("name",suggestListObject.getName());
                            startActivity(intent);
                        }
                    }


                }
            });

            view.setTag(viewHolder);
            return view;
        }

        class ViewHolder
        {
            CircleImageView request_img;
            ProgressBar mprogress1;
            LinearLayout btm_layout;
            BoldTextview request_name, onlinestatus, chat, voicecall, videocall;
            RelativeLayout rl_image;
            ImageView chat_image;
            NormalTextview request_designation;
        }



    }

}

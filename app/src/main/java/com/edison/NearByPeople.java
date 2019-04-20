package com.edison;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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

public class NearByPeople extends AppCompatActivity {

    TextView header, nodatasearch;
    FontManager fm;
    ImageView back;
    LinearLayout nodata_layout;
    SessionManager sm;
    ListView near_by_frd_list;
    ArrayList<MyFriendsList> myFrdObjectList;
    ACProgressFlower acProgressFlower;
    NearByfrdsAdapter nearByfrdsAdapter;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_people);



    }


    @Override
    protected void onResume() {
        INIT();
        super.onResume();
    }

    private void getLocation() {

        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            if (NetworkDetector.isNetworkStatusAvialable(NearByPeople.this)) {

                acProgressFlower = new ACProgressFlower.Builder(NearByPeople.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(Color.WHITE)
                        .text("")
                        .fadeColor(Color.DKGRAY).build();
                acProgressFlower.show();
                acProgressFlower.setCancelable(false);
                getNearByFriends(String.valueOf(latitude),String.valueOf(longitude));

            }
            else {
                Toast.makeText(NearByPeople.this,
                        "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }
        else
            {
            gpsTracker.showSettingsAlert();
//                turnGPSOn();
        }
    }
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    INIT();

                }
                break;

            default:
                break;
        }
    }

    private void INIT() {

        sm = new SessionManager(this);
        fm = new FontManager(this);

        near_by_frd_list = (ListView) findViewById(R.id.near_by_frd_list);
        back = (ImageView) findViewById(R.id.back);
        gpsTracker = new GPSTracker(NearByPeople.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        getLocation();


    }

    private void getNearByFriends(String s, String valueOf) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("latitude",s);
            jsonObject.accumulate("longitude",valueOf);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.near_by_people)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        acProgressFlower.cancel();
                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            JSONArray user = response.optJSONArray("user");
                            if(user.length()>0)
                            {
                                myFrdObjectList = new ArrayList<>();
                                MyFriendsList contact_friends_object;
                                for (int i=0;i<user.length();i++)
                                {
                                    contact_friends_object = new MyFriendsList();
                                    contact_friends_object.setName(user.optJSONObject(i).optString("name"));
                                    contact_friends_object.setUsername(user.optJSONObject(i).optString("username"));
                                    contact_friends_object.setUser_id(user.optJSONObject(i).optString("user_id"));
                                    contact_friends_object.setUser_company(user.optJSONObject(i).optString("company"));
                                    contact_friends_object.setProfile_image(user.optJSONObject(i).optString("profile_image"));
                                    contact_friends_object.setDesignation(user.optJSONObject(i).optString("designation"));
                                    contact_friends_object.setFriend_status(user.optJSONObject(i).optString("friend_status"));
                                    myFrdObjectList.add(contact_friends_object);
                                }

                                nearByfrdsAdapter = new NearByfrdsAdapter(NearByPeople.this, myFrdObjectList);
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

    public class NearByfrdsAdapter extends BaseAdapter
    {
        ArrayList<MyFriendsList> myFrdObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public NearByfrdsAdapter(Context context,ArrayList<MyFriendsList> suggestListObjectArrayList)
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



            viewHolder.mprogress1.setVisibility(VISIBLE);
            /////picasso
            Picasso.with(NearByPeople.this).load(suggestListObject.getProfile_image())
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

                    Intent intent = new Intent(NearByPeople.this,OtherUserProfileActivity.class);
                    intent.putExtra("id",suggestListObject.getUser_id());
                    intent.putExtra("name",suggestListObject.getName());
                    startActivity(intent);
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

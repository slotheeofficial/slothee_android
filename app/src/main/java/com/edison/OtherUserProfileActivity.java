package com.edison;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.File_path;
import com.edison.Object.MyPostObject;
import com.edison.Object.TempIBobjt;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class OtherUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager sm;
    FontManager fm;
    BoldTextview settings,username,auto_username;
    NormalTextview company,description;
    BoldTextview num_of_post,reject,connect,num_of_views,num_of_connection
            ,viewAllConection,conection_status;
    CircleImageView user_image;
    ProgressBar grpimageprogressbar;
    ExpandableHeightGridView postlist;
    Realm realm;
    MyPostListAdapter myPostListAdapter;
    LinearLayout ll_status,ll_manageconnection;
    LinearLayout ll_reject,ll_connect,ll_nodata,ll_otheruser_connectiton;
    String name,id,frd_Status,block,mute,profileimage;
    ImageView backicon;
    String dialog_id;
    ScrollView otheruserscroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        fm = new FontManager(this);
        sm = new SessionManager(this);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");



    }
    @Override
    protected void onResume() {

        INIT();
        super.onResume();
    }

    private void INIT() {

        sm = new SessionManager(OtherUserProfileActivity.this);
        fm = new FontManager(OtherUserProfileActivity.this);
        settings = (BoldTextview) findViewById(R.id.settings);
        num_of_connection = (BoldTextview) findViewById(R.id.num_of_connection);
        conection_status = (BoldTextview) findViewById(R.id.conection_status);
        viewAllConection = (BoldTextview) findViewById(R.id.viewAllConection);
        ll_otheruser_connectiton = (LinearLayout) findViewById(R.id.ll_otheruser_connectiton);
        num_of_views = (BoldTextview) findViewById(R.id.num_of_views);
        connect = (BoldTextview) findViewById(R.id.connect);
        reject = (BoldTextview) findViewById(R.id.reject);
        otheruserscroll = (ScrollView) findViewById(R.id.otheruserscroll);
        num_of_post = (BoldTextview) findViewById(R.id.num_of_post);
        username = (BoldTextview) findViewById(R.id.username);
        auto_username = (BoldTextview) findViewById(R.id.auto_username);
        description = (NormalTextview) findViewById(R.id.description);
        company = (NormalTextview) findViewById(R.id.company);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        grpimageprogressbar = (ProgressBar) findViewById(R.id.grpimageprogressbar);
        postlist = (ExpandableHeightGridView) findViewById(R.id.postlist);
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_reject = (LinearLayout) findViewById(R.id.ll_reject);
        ll_manageconnection = (LinearLayout) findViewById(R.id.ll_manageconnection);
        ll_connect = (LinearLayout) findViewById(R.id.ll_connect);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
        backicon = (ImageView) findViewById(R.id.backicon);
        realm = Realm.getDefaultInstance();
        ll_manageconnection.setVisibility(View.GONE);
        ll_connect.setVisibility(View.GONE);
        ll_nodata.setVisibility(View.GONE);
        ll_reject.setVisibility(View.GONE);
        settings.setVisibility(View.INVISIBLE);

        RealmResults<MyPostObject> homeObjectRealmResults = realm.where(MyPostObject.class).contains("post_user_fk",id).findAll();
        myPostListAdapter = new MyPostListAdapter(OtherUserProfileActivity.this, homeObjectRealmResults);
        postlist.setAdapter(myPostListAdapter);
        postlist.setExpanded(true);
        postlist.setFocusable(false);





        fm.setHorizontalIcon(settings);
        settings.setOnClickListener(this);
        user_image.setOnClickListener(this);
        reject.setOnClickListener(this);
        ll_reject.setOnClickListener(this);
        ll_connect.setOnClickListener(this);
        backicon.setOnClickListener(this);
        ll_otheruser_connectiton.setOnClickListener(this);
        viewAllConection.setOnClickListener(this);

        ///get home feed
        if(NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this))
        {
            getProfile();
        }
        else
        {
            Toast.makeText(OtherUserProfileActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }




    }

    private void getProfile() {

        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("other_user_id",id);
        }
        catch (Exception e)
        {

        }

        AndroidNetworking.post(Webservcie.get_other_profile)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


//                        acProgressFlower.dismiss();

//                        otheruserscroll.fullScroll(ScrollView.FOCUS_UP);
//                        otheruserscroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                otheruserscroll.fullScroll(View.FOCUS_UP);
//                            }
//                        });

                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                JSONObject user = jsonObject1.optJSONObject("user");

                                frd_Status = user.optString("friend_status");
                                block = user.optString("block");
                                mute = user.optString("mute");

                                conection_status.setText(jsonObject1.optInt("mutual_friends_count")+" Mutual Connection");
                                if(jsonObject1.optInt("mutual_friends_count")>0)
                                {
                                    if(jsonObject1.optInt("mutual_friends_count")==1)
                                    {
                                        viewAllConection.setText("VIEW");
                                    }
                                    else
                                    {
                                        viewAllConection.setText("VIEW ALL");
                                    }
                                    ll_manageconnection.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    ll_manageconnection.setVisibility(View.GONE);
                                }

                                //check whether user is blocked or not
                                if(user.optString("block").equalsIgnoreCase("1"))
                                {
                                    ll_connect.setVisibility(View.VISIBLE);
                                    ll_reject.setVisibility(View.GONE);
                                    settings.setVisibility(View.VISIBLE);
                                    connect.setText("UnBlock");
                                    ll_connect.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
                                    connect.setTextColor(getResources().getColor(R.color.white));
                                }
                                else
                                {
                                    if(user.optString("friend_status").equalsIgnoreCase("0"))
                                    {
                                        ll_connect.setVisibility(View.VISIBLE);
                                        ll_reject.setVisibility(View.GONE);
                                        settings.setVisibility(View.VISIBLE);
                                        connect.setText("Connect");
                                        ll_connect.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
                                        connect.setTextColor(getResources().getColor(R.color.white));
                                    }
                                    else if(user.optString("friend_status").equalsIgnoreCase("1"))
                                    {
                                        ll_connect.setVisibility(View.VISIBLE);
                                        settings.setVisibility(View.VISIBLE);
                                        ll_reject.setVisibility(View.GONE);
                                        connect.setText("Message");
                                        ll_connect.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
                                        connect.setTextColor(getResources().getColor(R.color.white));
                                    }
                                    else if(user.optString("friend_status").equalsIgnoreCase("2"))
                                    {
                                        ll_connect.setVisibility(View.VISIBLE);
                                        ll_reject.setVisibility(View.GONE);
                                        settings.setVisibility(View.INVISIBLE);
                                        connect.setText("Invitation sent");
                                        ll_connect.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                                        connect.setTextColor(getResources().getColor(R.color.appcolor));
                                    }
                                    else
                                    {
                                        ll_connect.setVisibility(View.VISIBLE);
                                        ll_reject.setVisibility(View.VISIBLE);
                                        settings.setVisibility(View.INVISIBLE);
                                        connect.setText("Accept");
                                        reject.setText("Reject");
                                        ll_connect.setBackground(getResources().getDrawable(R.drawable.btnbg_withblack));
                                        connect.setTextColor(getResources().getColor(R.color.white));
                                        ll_reject.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                                        reject.setTextColor(getResources().getColor(R.color.appcolor));
                                    }
                                }




                                username.setText(user.optString("name"));
                                dialog_id = (user.optString("dialog_id"));
                                profileimage = (user.optString("profile_image"));
                                auto_username.setText("@"+user.optString("username"));
                                num_of_post.setText(user.optString("post_count"));
                                num_of_connection.setText(user.optString("friends_count"));
                                num_of_views.setText(user.optString("view_count"));
                                description.setText(StringEscapeUtils.unescapeJava(user.optString("description")));
                                company.setText(user.optString("designation"));
                                grpimageprogressbar.setVisibility(View.VISIBLE);
                                Picasso.with(OtherUserProfileActivity.this)
                                        .load(user.optString("profile_image"))
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .placeholder(getResources().getDrawable(R.drawable.profile_img))
                                        .into(user_image, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                grpimageprogressbar.setVisibility(View.GONE);
                                            }
                                            @Override
                                            public void onError() {
                                                grpimageprogressbar.setVisibility(View.GONE);
                                            }
                                        });



                                if(user.optString("block").equalsIgnoreCase("1"))
                                {
                                    ll_nodata.setVisibility(View.GONE);
                                    postlist.setVisibility(View.GONE);
                                }
                                else
                                {
                                    ///getFrd list
                                    final JSONArray post = jsonObject1.optJSONArray("post");
                                    if(post.length()>0)
                                    {
                                        ll_nodata.setVisibility(View.GONE);
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                try {
                                                    realm.where(MyPostObject.class).contains("post_user_fk",id).findAll().clear();
                                                    realm.createOrUpdateAllFromJson(MyPostObject.class, post);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    realm.close();
                                                }
                                            }
                                        });

                                    }
                                    else
                                    {
                                        ll_nodata.setVisibility(View.VISIBLE);
                                    }

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

                final PopupMenu menu = new PopupMenu(OtherUserProfileActivity.this,view);
                if(block.equalsIgnoreCase("1"))
                {
                    menu.getMenu().add(Menu.NONE,1,1,"UnBlock");
                }
                else if(mute.equalsIgnoreCase("1")&&
                        frd_Status.equalsIgnoreCase("1"))
                {
                    menu.getMenu().add(Menu.NONE, 2, 3, "Disconnect");
                    menu.getMenu().add(Menu.NONE, 5, 2, "Block");
                    menu.getMenu().add(Menu.NONE, 3, 1, "UnMute");
                }
                else if(frd_Status.equalsIgnoreCase("0"))
                {
                    menu.getMenu().add(Menu.NONE, 5, 1, "Block");
                }
                else if(mute.equalsIgnoreCase("0")&&
                        block.equalsIgnoreCase("0")&&frd_Status.equalsIgnoreCase("1"))
                {
                    menu.getMenu().add(Menu.NONE, 2, 3, "Disconnect");
                    menu.getMenu().add(Menu.NONE, 5, 2, "Block");
                    menu.getMenu().add(Menu.NONE, 4, 1, "Mute");
                }


                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        int i = menuItem.getItemId();
                        if(i==1)
                        {
                            UnBlock_user("UnBlock","0");
//                            Toast.makeText(OtherUserProfileActivity.this, "Unblock", Toast.LENGTH_SHORT).show();
                        }
                        else if(i==2)
                        {
                            Unfollow_user();
                        }
                        else if(i==3)
                        {
                            UnMute_user("UnMute","0");
//                            Toast.makeText(OtherUserProfileActivity.this, "UnMute", Toast.LENGTH_SHORT).show();
                        }
                        else if(i==4)
                        {
                            UnMute_user("Mute","1");
//                            Toast.makeText(OtherUserProfileActivity.this, "Mute", Toast.LENGTH_SHORT).show();
                        }
                        else if(i==5)
                        {

                            UnBlock_user("Block","1");
//                            Toast.makeText(OtherUserProfileActivity.this, "block", Toast.LENGTH_SHORT).show();
                        }


                        return true;
                    }
                });

//                menu.inflate(R.menu.settings_others);
                menu.show();

                break;
           case R.id.backicon:

                finish();

                break;
           case R.id.viewAllConection:

               Intent intent = new Intent(OtherUserProfileActivity.this,MutualCnnectionList.class);
               intent.putExtra("otheruserid",id);
               startActivity(intent);

                break;
           case R.id.ll_otheruser_connectiton:

               Intent OtherUserConnectionList = new Intent(OtherUserProfileActivity.this,OtherUserConnectionList.class);
               OtherUserConnectionList.putExtra("otheruserid",id);
               OtherUserConnectionList.putExtra("otherusername",name);
               startActivity(OtherUserConnectionList);

                break;
           case R.id.ll_connect:


               if(block.equalsIgnoreCase("1"))
               {
                   UnBlock_user("UnBlock","0");
               }
               else
               {
                   //send req
                   if(frd_Status.equalsIgnoreCase("0"))
                   {
                       if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {
                           sendRequest(id,"1");
                       } else {
                           Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                       }

                   }
                   //already frd
                   else if(frd_Status.equalsIgnoreCase("1"))
                   {
                       NavigateToChat();
                   }
                   ///cancel req
                   else if(frd_Status.equalsIgnoreCase("2"))
                   {
                       new android.support.v7.app.AlertDialog.Builder(OtherUserProfileActivity.this)
                               .setIcon(android.R.drawable.ic_dialog_alert)
                               .setTitle(R.string.app_name)
                               .setMessage("Are you sure want to withdraw invitation ?")
                               .setPositiveButton("withdraw", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                       if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {
                                           sendRequest(id,"3");
                                       } else {
                                           Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                                       }

                                   }

                               })
                               .setNegativeButton("Cancel", null)
                               .show();
                   }
                   ///acc
                   else if(frd_Status.equalsIgnoreCase("3"))
                   {
                       if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {
                           Accept_Or_Reject(id,"2");

                       } else {
                           Toast.makeText(OtherUserProfileActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                       }
                   }
               }


                break;

            case R.id.ll_reject:

                ////rej
                if(frd_Status.equalsIgnoreCase("3"))
                {
                    if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {
                        Accept_Or_Reject(id,"3");

                    } else {
                        Toast.makeText(OtherUserProfileActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.user_image:

                Intent intent1 = new Intent(OtherUserProfileActivity.this, Full_ImageViewActivity.class);
                intent1.putExtra("url",profileimage);
                startActivity(intent1);
                break;

        }

    }

    private void NavigateToChat() {

            if(dialog_id.length()>0)
            {
                TempIBobjt tempIBobjt = new TempIBobjt();
                tempIBobjt.setDialog_id(dialog_id);
                tempIBobjt.setGroup_id(id);
                tempIBobjt.setGroup_image("");
                tempIBobjt.setGroup_name(name);
                tempIBobjt.setQuickblox_id("");
                Intent intent = new Intent(OtherUserProfileActivity.this, OneToOneChat.class);
                intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                startActivity(intent);
            }
            else
            {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.accumulate("sender_id",sm.getUserObject().getUser_id());
                    jsonObject.accumulate("receiver_id",id);
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
                                    tempIBobjt.setGroup_id(id);
                                    tempIBobjt.setGroup_image("");
                                    tempIBobjt.setGroup_name(name);
                                    tempIBobjt.setQuickblox_id("");
                                    Intent intent = new Intent(OtherUserProfileActivity.this, OneToOneChat.class);
                                    intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(OtherUserProfileActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }

    }

    public void Unfollow_user()
    {
        new android.support.v7.app.AlertDialog.Builder(OtherUserProfileActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to disconnect with "+name.toUpperCase()+" ?")
                .setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {
                            sendRequest(id,"2");
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void UnMute_user(String status, final String diff)
    {
        new android.support.v7.app.AlertDialog.Builder(OtherUserProfileActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to "+status +" "+ name.toUpperCase()+"?")
                .setPositiveButton(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {

                            MuteUnMute(diff);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void MuteUnMute(String diff) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("follower_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",id);
            jsonObject.accumulate("status", diff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(OtherUserProfileActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.mute_unmute)
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
                                INIT();
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


    public void UnBlock_user(String status, final String diff)
    {
        new android.support.v7.app.AlertDialog.Builder(OtherUserProfileActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure want to "+status +" "+ name.toUpperCase()+"?")
                .setPositiveButton(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (NetworkDetector.isNetworkStatusAvialable(OtherUserProfileActivity.this)) {

                            BlockUnblock(diff);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void BlockUnblock(String status) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("follower_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",id);
            jsonObject.accumulate("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(OtherUserProfileActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.block_unblock)
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
                                INIT();
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

    private void Accept_Or_Reject(String user_id, String s) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("follower_id",user_id);
            jsonObject.accumulate("status", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(OtherUserProfileActivity.this)
//                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
//                .themeColor(Color.WHITE)
//                .text("")
//                .fadeColor(Color.DKGRAY).build();
//        acProgressFlower.show();
//        acProgressFlower.setCancelable(false);


        AndroidNetworking.post(Webservcie.request_response)
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
                                INIT();
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


    private void sendRequest(String id, final String status) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",id);
            jsonObject.accumulate("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(OtherUserProfileActivity.this)
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
                                if(status.equalsIgnoreCase("1"))
                                {
                                    connect.setText("Invitation sent");
                                    frd_Status="2";
                                    ll_connect.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                                    connect.setTextColor(getResources().getColor(R.color.appcolor));
                                }
                                else
                                {
                                    INIT();
                                }
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
                        .placeholder(R.drawable.postplaceholder)
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

                    if(mute.equalsIgnoreCase("1"))
                    {
                        Toast.makeText(OtherUserProfileActivity.this, "Unmute your friend to view post.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        Intent intent = new Intent(getApplicationContext(),SingleFeedDetails.class);
                        intent.putExtra("post_pk",suggestListObject.getPost_pk());
                        intent.putExtra("dialog_id",dialog_id);
                        intent.putExtra("friend_status",frd_Status);
                        startActivity(intent);

                    }


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

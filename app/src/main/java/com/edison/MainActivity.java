package com.edison;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.MainFragment.CreateFeedFragment;
import com.edison.MainFragment.HomeFragment;
import com.edison.MainFragment.InboxFragment;
import com.edison.MainFragment.MyProfileFragment;
import com.edison.MainFragment.SearchFragment;
import com.edison.Object.UserObject;
import com.edison.Utils.GPSTracker;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    LinearLayout home0, search0, inbox0, createFeed0, myProfile0;
    SessionManager sm;
    private BroadcastReceiver mNetworkReceiver;
    TextView tv_check_connection, message_count, frd_req_count;
    ImageView search_image,search_image2, home_image,home_image2, create_feed_image, inbox_image,inbox_image2, myProfile_image,myProfile_image2;
    boolean doubleBackToExitPressedOnce = false;
    private GPSTracker gpsTracker;
    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private CardView cardBottomSheet;
    FrameLayout mainlayout;
    CallbackManager callbackManager;
    com.edison.Customfonts.Button tap_2_reg;
    ImageView googlelogin, linkedinlogin;
    BoldTextview redirect_2_login;

    public GoogleApiClient mGoogleApiClient;
    public final int RC_SIGN_IN = 007;
    String emailid, emailtoken, fbtoken, fstname, lastname, photo, login_type;
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    public static final String PACKAGE = "com.edison";

    ACProgressFlower acProgressFlower;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {
                if (sm.getUserObject() != null) {
                    getFriendReqList();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        INIT();
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                System.out.println(">>#$@34 " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }


    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//
//
//                    getLocation();
//
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    private void getLocation() {

        gpsTracker = new GPSTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {
                UpdateLatLong(String.valueOf(latitude), String.valueOf(longitude));
            }

        } else {
//            gpsTracker.showSettingsAlert();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    @Override
    public void onResume() {

        registerReceiver(myReceiver, new IntentFilter("FBR-IMAGE"));

        if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {
            if (sm.getUserObject() != null) {
                getFriendReqList();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    private void INIT() {

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        sm = new SessionManager(this);
        tv_check_connection = (TextView) findViewById(R.id.tv_check_connection);

        home_image = (ImageView) findViewById(R.id.home_image);
        home_image2 = (ImageView) findViewById(R.id.home_image2);
        search_image = (ImageView) findViewById(R.id.search_image);
        search_image2 = (ImageView) findViewById(R.id.search_image2);
        create_feed_image = (ImageView) findViewById(R.id.create_feed_image);
        inbox_image = (ImageView) findViewById(R.id.inbox_image);
        inbox_image2 = (ImageView) findViewById(R.id.inbox_image2);
        myProfile_image = (ImageView) findViewById(R.id.myProfile_image);
        myProfile_image2 = (ImageView) findViewById(R.id.myProfile_image2);
        frd_req_count = (TextView) findViewById(R.id.frd_req_count);
        message_count = (TextView) findViewById(R.id.message_count);

        frd_req_count.setVisibility(View.GONE);
        message_count.setVisibility(View.GONE);

        mainlayout = (FrameLayout) findViewById(R.id.mainlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainlayout.getForeground().setAlpha(0);
        }
        ///bottom sheet
        cardBottomSheet = (CardView) findViewById(R.id.card_home_details);
        tap_2_reg = (com.edison.Customfonts.Button) cardBottomSheet.findViewById(R.id.tap_2_reg);
        googlelogin = (ImageView) cardBottomSheet.findViewById(R.id.googlelogin);
        linkedinlogin = (ImageView) cardBottomSheet.findViewById(R.id.linkedinlogin);
        redirect_2_login = (BoldTextview) cardBottomSheet.findViewById(R.id.redirect_2_login);
        bottomSheetBehavior = BottomSheetBehavior.from(cardBottomSheet);
        cardBottomSheet.setVisibility(View.GONE);


        home0 = (LinearLayout) findViewById(R.id.home0);
        search0 = (LinearLayout) findViewById(R.id.search0);
        inbox0 = (LinearLayout) findViewById(R.id.inbox0);
        myProfile0 = (LinearLayout) findViewById(R.id.myProfile0);
        createFeed0 = (LinearLayout) findViewById(R.id.createFeed0);


        home0.setOnClickListener(this);
        search0.setOnClickListener(this);
        createFeed0.setOnClickListener(this);
        inbox0.setOnClickListener(this);
        myProfile0.setOnClickListener(this);
        redirect_2_login.setOnClickListener(this);
        tap_2_reg.setOnClickListener(this);
        googlelogin.setOnClickListener(this);
        linkedinlogin.setOnClickListener(this);


//        home_image.setColorFilter(getResources().getColor(R.color.appcolor));
//        search_image.setColorFilter(getResources().getColor(R.color.hash));
//        create_feed_image.setColorFilter(getResources().getColor(R.color.hash));
//        inbox_image.setColorFilter(getResources().getColor(R.color.hash));
//        myProfile_image.setColorFilter(getResources().getColor(R.color.hash));


            NavigateToHome();




        if (Build.VERSION.SDK_INT >= M) {
            if (MainActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    MainActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                android.Manifest.permission.RECORD_AUDIO,
//                                android.Manifest.permission.READ_PHONE_STATE,
//                                android.Manifest.permission.READ_CONTACTS,
//                                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.CAMERA},
//                        1);
            }
        } else {
            getLocation();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.home0:
                NavigateToHome();
                break;

            case R.id.redirect_2_login:
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                break;

            case R.id.googlelogin:

                if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                } else {
                    Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.linkedinlogin:

                if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {

//                    linkedinlogin();
                    facebookLogin();
                } else {
                    Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.tap_2_reg:

//                if (Build.VERSION.SDK_INT >= M) {
//                    if (MainActivity.this.checkSelfPermission(Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED&&
//                            MainActivity.this.checkSelfPermission(Manifest.permission.RECEIVE_SMS)
//                                    == PackageManager.PERMISSION_GRANTED ) {

                        Intent intenttap_2_reg = new Intent(MainActivity.this, SignUpManually.class);
                        startActivity(intenttap_2_reg);

//                    } else {
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{android.Manifest.permission.READ_SMS,android.Manifest.permission.RECEIVE_SMS},
//                                1);
//                    }
//                } else {
//
//                    Intent intenttap_2_reg = new Intent(MainActivity.this, SignUpManually.class);
//                    startActivity(intenttap_2_reg);
//                }



                break;

            case R.id.search0:
                if (sm.getUserObject() != null) {
                    NavigateToSearch();
                } else {
                    ShowAlert();
                }

                break;

            case R.id.createFeed0:
                if (sm.getUserObject() != null) {

                    if(sm.getUserObject().getName()!=null&&sm.getUserObject().getName().length()>0)
                    {
                        Intent intent1 = new Intent(MainActivity.this, CreateFeed.class);
                        startActivity(intent1);
                    }else {

                        Toast.makeText(MainActivity.this, "Please update your profile", Toast.LENGTH_SHORT).show();
                        Intent intenta = new Intent(MainActivity.this,UpdateProfile.class);
                        startActivity(intenta);

                    }


                } else {
                    ShowAlert();
                }
                break;


            case R.id.inbox0:
                if (sm.getUserObject() != null) {
                    NavigateToInbox();
                } else {
                    ShowAlert();
                }
                break;


            case R.id.myProfile0:
                if (sm.getUserObject() != null) {
                    NavigateToProfile();
                } else {
                    ShowAlert();
                }
                break;


        }

    }

    private void facebookLogin() {

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest data_request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject json_object,
                                    GraphResponse response) {
                                try {

                                    JSONObject object = new JSONObject(json_object.toString());
                                    JSONObject profile_pic_data = new JSONObject(object.get("picture").toString());
                                    JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

//                                    profile_pic_url.getString("url")

                                    emailid = object.optString("email");
                                    fstname = object.optString("name");
                                    lastname = object.optString(" ");
                                    fbtoken = object.optString("id");
                                    emailtoken="";
                                    login_type="";
                                    photo = object.optJSONObject("picture").optJSONObject("data").optString("url");

                                    if(NetworkDetector.isNetworkStatusAvialable(MainActivity.this))
                                    {
                                        Edison_login("2");

                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                Bundle permission_param = new Bundle();
                permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
                data_request.setParameters(permission_param);
                data_request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Sorry try again Later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

                System.out.println(">>> "+error);

                Toast.makeText(MainActivity.this, "Sorry try again Later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            System.out.println("??>>>>>SFSDF " + data);

            System.out.println(" >>>>>>>  varudhu varudhu ");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {

        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void handleSignInResult(GoogleSignInResult result) {

        System.out.println(">>>>>result>>>>>. " + result);


        if (result.isSuccess()) {


            emailid = result.getSignInAccount().getEmail();
            fstname = result.getSignInAccount().getGivenName();
            lastname = result.getSignInAccount().getFamilyName();
            emailtoken = result.getSignInAccount().getId();
            fbtoken = "";
            login_type = "";
            photo = String.valueOf(result.getSignInAccount().getPhotoUrl());

            if (NetworkDetector.isNetworkStatusAvialable(MainActivity.this)) {
                Edison_login("2");

            } else {
                Toast.makeText(this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            }


            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String id = acct.getId();
            String email = acct.getEmail();

            System.out.println(">>>>>>>>211112222222> " + "Name : " + personName + "\n" +
                    "Email : " + email + "\n" +
                    "G_Login ID : " + id);

        } else {
            System.out.println(">>> " + "ingaium varudhu but output varla");
        }
    }

    private void Edison_login(String s) {

        acProgressFlower = new ACProgressFlower.Builder(MainActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);
        String token = FirebaseInstanceId.getInstance().getToken();


        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.accumulate("fcm_id", token);
            jsonObject.accumulate("login_type", login_type);
            jsonObject.accumulate("google_id", emailtoken);
            jsonObject.accumulate("facebook_id", fbtoken);
            jsonObject.accumulate("email", emailid);
            jsonObject.accumulate("name", fstname + " " + lastname);
            jsonObject.accumulate("image_url", photo);
        } catch (Exception e) {

        }



        AndroidNetworking.post(Webservcie.login)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        acProgressFlower.dismiss();



                        try {


                            if (response.optString("success").equalsIgnoreCase("1")) {

                                JSONObject user = response.optJSONObject("user");
                                UserObject UO = new UserObject();
                                UO.setName(user.optString("name"));
                                UO.setUsername(user.optString("username"));
                                UO.setGender(user.optString("gender"));
                                UO.setEmail(user.optString("email"));
                                UO.setCountry_code(user.optString("country_code"));
                                UO.setPhoneno(user.optString("phoneno"));
                                UO.setDob(user.optString("dob"));
                                UO.setProfile_image(user.optString("profile_image"));
                                UO.setDesignation(user.optString("designation"));
                                UO.setCompany(user.optString("company"));
                                UO.setDescription(user.optString("description"));
                                UO.setInterest(user.optString("interest"));
                                UO.setUser_id(user.optString("user_id"));
                                sm.setUserObject(UO);


                                Toast.makeText(MainActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();

                                if (user.optInt("already") == 1)
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mainlayout.getForeground().setAlpha(0);
                                    }
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    cardBottomSheet.setVisibility(View.GONE);

//                                    Intent intent = new Intent(MainActivity.this, InterestActivity.class);
//                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(MainActivity.this, InterestActivity.class);
                                    startActivity(intent);
                                }


                            } else {
                                Toast.makeText(MainActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }

    public void ShowAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainlayout.getForeground().setAlpha(255);
        }
        cardBottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void NavigateToHome() {


//        Picasso.with(MainActivity.this).load(R.drawable.icons_home_selected).into(home_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_search_unselected).into(search_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_new_post_unselected).into(create_feed_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_messages_unselected).into(inbox_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_profile_unselected).into(myProfile_image);

        home_image.setVisibility(View.GONE);
        home_image2.setVisibility(View.VISIBLE);
        search_image.setVisibility(View.VISIBLE);
        search_image2.setVisibility(View.GONE);
        inbox_image.setVisibility(View.VISIBLE);
        inbox_image2.setVisibility(View.GONE);
        myProfile_image.setVisibility(View.VISIBLE);
        myProfile_image2.setVisibility(View.GONE);

//        home_image.setColorFilter(getResources().getColor(R.color.appcolor));
//        search_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        create_feed_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        inbox_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        myProfile_image.setColorFilter(getResources().getColor(R.color.dark_gray));


        Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "home");
        fragmentTransaction.commit();

    }

    private void NavigateToSearch() {

//        Picasso.with(MainActivity.this).load(R.drawable.icons_home_unselected).into(home_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_search_selected).into(search_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_new_post_unselected).into(create_feed_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_messages_unselected).into(inbox_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_profile_unselected).into(myProfile_image);

        home_image.setVisibility(View.VISIBLE);
        home_image2.setVisibility(View.GONE);
        search_image.setVisibility(View.GONE);
        search_image2.setVisibility(View.VISIBLE);
        inbox_image.setVisibility(View.VISIBLE);
        inbox_image2.setVisibility(View.GONE);
        myProfile_image.setVisibility(View.VISIBLE);
        myProfile_image2.setVisibility(View.GONE);

//        home_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        search_image.setColorFilter(getResources().getColor(R.color.appcolor));
//        create_feed_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        inbox_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        myProfile_image.setColorFilter(getResources().getColor(R.color.dark_gray));

        Fragment fragment = new SearchFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "home");
        fragmentTransaction.commit();

    }

    private void NavigateToCreateFeed() {


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        create_feed_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
        search_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.edttextcolor), android.graphics.PorterDuff.Mode.MULTIPLY);
        home_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.edttextcolor), android.graphics.PorterDuff.Mode.MULTIPLY);
        inbox_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.edttextcolor), android.graphics.PorterDuff.Mode.MULTIPLY);
        myProfile_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.edttextcolor), android.graphics.PorterDuff.Mode.MULTIPLY);


        Fragment fragment = new CreateFeedFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "home");
        fragmentTransaction.commit();

    }

    private void NavigateToInbox() {

//        Picasso.with(MainActivity.this).load(R.drawable.icons_home_unselected).into(home_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_search_unselected).into(search_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_new_post_unselected).into(create_feed_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_messages_selected).into(inbox_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_profile_unselected).into(myProfile_image);

        home_image.setVisibility(View.VISIBLE);
        home_image2.setVisibility(View.GONE);
        search_image.setVisibility(View.VISIBLE);
        search_image2.setVisibility(View.GONE);
        inbox_image.setVisibility(View.GONE);
        inbox_image2.setVisibility(View.VISIBLE);
        myProfile_image.setVisibility(View.VISIBLE);
        myProfile_image2.setVisibility(View.GONE);

//        home_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        search_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        create_feed_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        inbox_image.setColorFilter(getResources().getColor(R.color.appcolor));
//        myProfile_image.setColorFilter(getResources().getColor(R.color.dark_gray));


        Fragment fragment = new InboxFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "home");
        fragmentTransaction.commit();

    }

    private void NavigateToProfile() {

//        Picasso.with(MainActivity.this).load(R.drawable.icons_home_unselected).into(home_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_search_unselected).into(search_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_new_post_unselected).into(create_feed_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_messages_unselected).into(inbox_image);
//        Picasso.with(MainActivity.this).load(R.drawable.icons_profile_selected).into(myProfile_image);

        home_image.setVisibility(View.VISIBLE);
        home_image2.setVisibility(View.GONE);
        search_image.setVisibility(View.VISIBLE);
        search_image2.setVisibility(View.GONE);
        inbox_image.setVisibility(View.VISIBLE);
        inbox_image2.setVisibility(View.GONE);
        myProfile_image.setVisibility(View.GONE);
        myProfile_image2.setVisibility(View.VISIBLE);

//        home_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        search_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        create_feed_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        inbox_image.setColorFilter(getResources().getColor(R.color.dark_gray));
//        myProfile_image.setColorFilter(getResources().getColor(R.color.appcolor));

        Fragment fragment = new MyProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, "home");
        fragmentTransaction.commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == bottomSheetBehavior.STATE_EXPANDED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mainlayout.getForeground().setAlpha(0);
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            cardBottomSheet.setVisibility(View.GONE);
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click back again to exit app", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }


    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (isOnline(context))
                {
                    if (sm.getUserObject() != null) {
                        getFriendReqList();
                    }
                    tv_check_connection.setVisibility(View.GONE);
                }
                else
                {
                    tv_check_connection.setVisibility(View.VISIBLE);
                    tv_check_connection.setText("Could not Connect to internet");
                    tv_check_connection.setBackgroundColor(Color.RED);
                    tv_check_connection.setTextColor(Color.WHITE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private boolean isOnline(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                return (netInfo != null && netInfo.isConnected());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void getFriendReqList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.notification_count)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if (jsonObject1.optString("success").equalsIgnoreCase("1")) {


                                if (jsonObject1.optInt("invitation_count") > 0) {
                                    frd_req_count.setVisibility(View.VISIBLE);
                                    frd_req_count.setText(String.valueOf(jsonObject1.optString("invitation_count")));
                                } else {
                                    frd_req_count.setVisibility(View.GONE);
                                }

//                                if(jsonObject1.optInt("notification_count")>0)
//                                {
//                                    count2.setVisibility(View.VISIBLE);
//                                    count2.setText(String.valueOf(jsonObject1.optString("notification_count")));
//                                }
//                                else
//                                {
//                                    count2.setVisibility(View.GONE);
//                                }
//
//
                                if (jsonObject1.optInt("unread_count") > 0) {
                                    message_count.setVisibility(View.VISIBLE);
                                    message_count.setText(String.valueOf(jsonObject1.optString("unread_count")));
                                } else {
                                    message_count.setVisibility(View.GONE);
                                }


                            } else {
//                                count1.setVisibility(View.GONE);
                                message_count.setVisibility(View.GONE);
                                frd_req_count.setVisibility(View.GONE);
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

    public void UpdateLatLong(String latitude, String longitude) {
        if (sm.getUserObject() != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
                jsonObject.accumulate("latitude", latitude);
                jsonObject.accumulate("longitude", longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            AndroidNetworking.post(Webservcie.update_latitude_longitude)
                    .addJSONObjectBody(jsonObject)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {


                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });

        }


    }

}

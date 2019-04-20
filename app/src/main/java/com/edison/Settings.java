package com.edison;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;

public class Settings extends AppCompatActivity {

    SessionManager sm;
    LinearLayout account_settings,  logout,updateprofile,updateInterest,muted_list,blocked_list,invitefriends;
    TextView status_text;
    ImageView back;
    ImageView close1;
    FontManager fm;
    //ProgressDialog progressDialog;
    ACProgressFlower acProgressFlower;
    public GoogleApiClient mGoogleApiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Init();
        
        
    }

    private void Init() {

        fm = new FontManager(Settings.this);
        sm = new SessionManager(Settings.this);
//        progressDialog = new ProgressDialog(this);


        account_settings = (LinearLayout) findViewById(R.id.account_settings);
        close1 = (ImageView) findViewById(R.id.close1);
        updateprofile = (LinearLayout) findViewById(R.id.updateprofile);
        logout = (LinearLayout) findViewById(R.id.logout);
        blocked_list = (LinearLayout) findViewById(R.id.blocked_list);
        muted_list = (LinearLayout) findViewById(R.id.muted_list);
        updateInterest = (LinearLayout) findViewById(R.id.updateInterest);
        invitefriends = (LinearLayout) findViewById(R.id.invitefriends);
        status_text = (TextView) findViewById(R.id.status_text);


        account_settings.setOnClickListener(onclick);
        blocked_list.setOnClickListener(onclick);
        muted_list.setOnClickListener(onclick);
        updateInterest.setOnClickListener(onclick);
        updateprofile.setOnClickListener(onclick);
        logout.setOnClickListener(onclick);
        invitefriends.setOnClickListener(onclick);
        close1.setOnClickListener(onclick);

    }

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(Settings.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }
    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == account_settings.getId()) {

                Intent intent1 = new Intent(Settings.this, ResetPassword.class);
                startActivity(intent1);

            }
            else if (v.getId() == updateprofile.getId()) {


                Intent intent = new Intent(Settings.this, UpdateProfile.class);
                startActivity(intent);


            }
            else if (v.getId() == updateInterest.getId()) {


                Intent intent = new Intent(Settings.this, UpdateInterest.class);
                startActivity(intent);


            }
            else if (v.getId() == invitefriends.getId()) {


//                Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//
//                // Add data to the intent, the receiving app will decide
//                // what to do with it.
//                share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
//                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sloth");
//
//                startActivity(Intent.createChooser(share, "Share link!"));

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String shareMessage= "\nSlothee is a modern business networking platform aimed at connecting people which isn't data hungry and respects privacy.\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));


            }
            else if (v.getId() == close1.getId()) {

               finish();
            }
            else if (v.getId() == blocked_list.getId()) {

//                Toast.makeText(Settings.this, "blocked_list", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, BlockedList.class);
                startActivity(intent);

            }
            else if (v.getId() == muted_list.getId()) {

//                Toast.makeText(Settings.this, "muted_list", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, MutedList.class);
                startActivity(intent);

            }
            else if (v.getId() == logout.getId()) {




                new AlertDialog.Builder(Settings.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.app_name)
                        .setMessage("Are you sure want to logout from "+getResources().getString(R.string.app_name)+"?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                if(NetworkDetector.isNetworkStatusAvialable(Settings.this))
                                {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
                                        jsonObject.accumulate("offline_online","2");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    AndroidNetworking.post(Webservcie.offline_online)
                                            .addJSONObjectBody(jsonObject)
                                            .build().getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {



                                            if(response.optString("success").equalsIgnoreCase("1"))
                                            {

                                                //google
                                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                        new ResultCallback<Status>() {
                                                            @Override
                                                            public void onResult(Status status) {

                                                            }
                                                        });

                                                //linkedin
//                                                LISession session = LISessionManager.getInstance(getApplicationContext()).clearSession();
                                                //facebbok
//                                                LoginManager.getInstance().logOut();



                                                sm.clear_session();
                                                Intent intent = new Intent(Settings.this, SplashScreen.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                Settings.this.finish();

                                            }

                                        }

                                        @Override
                                        public void onError(ANError anError) {

                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(Settings.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                                }









                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    };

}

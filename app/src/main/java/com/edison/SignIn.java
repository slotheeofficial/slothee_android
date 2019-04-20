package com.edison;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.UserObject;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.realm.Realm;

import static android.os.Build.VERSION_CODES.M;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    ACProgressFlower acProgressFlower;
    BoldTextview signup;
    NormalTextview forgotpassword;
    LinearLayout txt_login;
    EditText edt_email,edt_password;
    SessionManager sm;
    FontManager fm;
    Realm realm;
    Button login;


    public GoogleApiClient mGoogleApiClient;
    public final int RC_SIGN_IN = 007;
    String emailid,emailtoken,fbtoken,fstname,lastname,photo,login_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        if (Build.VERSION.SDK_INT >= M) {
            if (
                    SignIn.this.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED&&
                            SignIn.this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    ) {

                INIT();


            }
            else
            {
                ActivityCompat.requestPermissions(SignIn.this,
                        new String[]{
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_PHONE_STATE

//                                ,
//                                Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_COARSE_LOCATION,
//                                Manifest.permission.CAMERA
                        },
                        1);
            }
        }
        else
        {
            INIT();
        }



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
    private void INIT()
    {


        this.realm = Realm.getDefaultInstance();
        sm = new SessionManager(this);
        fm = new FontManager(this);
        txt_login = (LinearLayout) findViewById(R.id.txt_login);
        login = (Button) findViewById(R.id.login);
        signup = (BoldTextview) findViewById(R.id.signup);
        forgotpassword = (NormalTextview) findViewById(R.id.forgotpassword);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_password = (EditText) findViewById(R.id.edt_password);

        ///onclick
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgotpassword.setOnClickListener(this);

        String token = FirebaseInstanceId.getInstance().getToken();


    }
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.signup :
                Intent intent = new Intent(SignIn.this, SignUpManually.class);
                startActivity(intent);
                break;

            case R.id.forgotpassword :

                final Dialog dialog = new Dialog(SignIn.this);
                dialog.setContentView(R.layout.enter_forgot_email);

                final EditText dialog_edit_email = (EditText) dialog.findViewById(R.id.dialog_edit_email);
                TextView dialog_done = (TextView) dialog.findViewById(R.id.dialog_done);
                TextView dialog_cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);

                dialog_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (dialog_edit_email.getText().toString().length() > 0)
                        {
                            if(validateUsername(dialog_edit_email.getText().toString().trim()))
                            {
                                dialog.cancel();
                                getPassWord(dialog_edit_email.getText().toString().trim());
                            }
                            else
                            {
                                dialog_edit_email.setError("Enter valid email.");
                            }
                        }
                        else
                        {
                            Toast.makeText(SignIn.this, "Please enter email", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                dialog.show();

                break;


            case R.id.login :

                if(validateEmail())
                {
                    if(validatePassword())
                    {
                        if(NetworkDetector.isNetworkStatusAvialable(SignIn.this))
                        {
                            Edison_login("1");
                            acProgressFlower = new ACProgressFlower.Builder(SignIn.this)
                                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                    .themeColor(Color.WHITE)
                                    .text("")
                                    .fadeColor(Color.DKGRAY).build();
                            acProgressFlower.show();
                            acProgressFlower.setCancelable(false);
                        }
                        else
                        {
                            Toast.makeText(this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


                break;

        }

    }

    private void Edison_login(String s) {

        String token = FirebaseInstanceId.getInstance().getToken();


        JSONObject jsonObject = null;

        try
        {
            jsonObject = new JSONObject();
            jsonObject.accumulate("username",edt_email.getText().toString());
            jsonObject.accumulate("password",edt_password.getText().toString());
            jsonObject.accumulate("fcm_id",token);
        }
        catch (Exception e)
        {

        }

        AndroidNetworking.post(Webservcie.login)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        acProgressFlower.dismiss();

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
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
                                Toast.makeText(SignIn.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                startActivity(intent);
                                finish();


                        }
                        else
                        {
                            Toast.makeText(SignIn.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();

                    }
                });




    }

    ///validate dob
    public boolean validatePassword()
    {
        if(edt_password.getText().toString().length()>0)
        {
            return true;
        }
        else
        {
            edt_password.setError("Please enter password");
            return false;
        }
    }
    //validateEmail
    public boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
        if (edt_email.getText().toString().trim().matches(emailPattern))
        {
            return true;
        }
        else
        {
            edt_email.setError("Enter valid email");
            return false;
        }

    }
    private void getPassWord(String trim) {

        acProgressFlower = new ACProgressFlower.Builder(SignIn.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("email",trim);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.forgot_password)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        acProgressFlower.dismiss();

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            Toast.makeText(SignIn.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SignIn.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });
    }

    private boolean validateUsername(String trim) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
        if (trim.matches(emailPattern))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}

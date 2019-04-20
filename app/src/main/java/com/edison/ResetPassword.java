package com.edison;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.Customfonts.EditText;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;

import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ResetPassword extends AppCompatActivity {

    SessionManager sm;
    EditText old_password,new_password,confirm_password;
    Button reset_password;
    ImageView back;
    ACProgressFlower acProgressFlower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        INIT();

    }

    private void INIT() {

        sm = new SessionManager(this);
        reset_password = (Button) findViewById(R.id.reset_password);
        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        back = (ImageView) findViewById(R.id.back);

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(old_password.getText().toString().length()>0)
                {
                    if(new_password.getText().toString().length()>7)
                    {
                        if(confirm_password.getText().toString().length()>0)
                        {
                            if(new_password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString()))
                            {
                                ChangePassword();
                            }
                            else
                            {
                                confirm_password.setError("Password does not match");
                            }
                        }
                        else
                        {
                            confirm_password.setError("Please enter Confirm password");
                        }
                    }
                    else
                    {
                        new_password.setError("Please length should be mini 8 characters");
                    }
                }
                else
                {
                    old_password.setError("Please enter old password");
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ChangePassword() {

        acProgressFlower = new ACProgressFlower.Builder(ResetPassword.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("old_password",old_password.getText().toString());
            jsonObject.accumulate("password",new_password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.change_password)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {



                        acProgressFlower.dismiss();
                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            finish();
                            Toast.makeText(ResetPassword.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ResetPassword.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });

    }
}

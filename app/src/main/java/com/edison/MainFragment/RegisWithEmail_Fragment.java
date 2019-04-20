package com.edison.MainFragment;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Firebase.FirebaseIDService;
import com.edison.InterestActivity;
import com.edison.MainActivity;
import com.edison.Object.UserObject;
import com.edison.R;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisWithEmail_Fragment extends Fragment implements View.OnClickListener {


    View view;
    SessionManager sm;
    MaterialEditText edt_name,edt_email,edt_password;
    ImageView tap_2_reg;
    ProgressBar progressbar_register;

    public RegisWithEmail_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_email_regis_, container, false);

        INIT();

        return view;
    }

    private void INIT() {

        sm = new SessionManager(getContext());
        edt_name = (MaterialEditText) view.findViewById(R.id.edt_name);
        edt_email = (MaterialEditText) view.findViewById(R.id.edt_email);
        edt_password = (MaterialEditText) view.findViewById(R.id.edt_password);
        progressbar_register = (ProgressBar) view.findViewById(R.id.progressbar_register);
        tap_2_reg = (ImageView) view.findViewById(R.id.tap_2_reg);
        progressbar_register.setVisibility(View.GONE);

        tap_2_reg.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.tap_2_reg:

                if(validateName()&&validateEmail()&&validatePassword())
                {

                    if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                    {
                        Register();
                        progressbar_register.setVisibility(View.VISIBLE);
                        tap_2_reg.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Check your Network connection", Toast.LENGTH_SHORT).show();
                    }


                }
                break;
        }

    }

    private void Register() {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("name",edt_name.getText().toString());
            jsonObject.accumulate("password",edt_password.getText().toString());
            jsonObject.accumulate("email",edt_email.getText().toString().trim());
            jsonObject.accumulate("fcm_id", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.register)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressbar_register.setVisibility(View.GONE);
                        tap_2_reg.setVisibility(View.VISIBLE);

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
                            Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), InterestActivity.class);
                            startActivity(intent);


                        }
                        else
                        {
                            Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        progressbar_register.setVisibility(View.GONE);
                        tap_2_reg.setVisibility(View.VISIBLE);

                    }
                });

    }

    ///validate edt_username
    public boolean validateName() {
        if (edt_name.getText().toString().length() > 0) {
            return true;
        } else {
            edt_name.setError("Please enter Name");
            return false;
        }
    }

    ///validate validatePassword
    public boolean validatePassword() {
        if (edt_password.getText().toString().length() > 7) {
            return true;
        } else {
            edt_password.setError("Please length should be mini 8 characters");
            return false;
        }
    }


    //validateEmail
    public boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
        if (edt_email.getText().toString().trim().matches(emailPattern)) {
            return true;
        } else {
            edt_email.setError("Enter valid email");
            return false;
        }

    }
}

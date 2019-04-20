package com.edison.MainFragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.InterestActivity;
import com.edison.MainActivity;
import com.edison.Object.UserObject;
import com.edison.R;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Build.VERSION_CODES.M;
import static com.edison.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisWithOTPFragment extends Fragment implements View.OnClickListener {

    View view;
    SessionManager sm;
    ImageView next;
    CountryCodePicker countryCodePicker;
    String country_iso="IN",country_code = "+91";
    LinearLayout ll_mobile_layout,ll_otp_layout;
    MaterialEditText edt_code;
    EditText mobile;
    BoldTextview resend_otp;
    ProgressBar resend_progressbar;
    ProgressBar progressbar_verifynumber;
    NormalTextview hint;
    boolean otpverfication = false;
    String otpID;
//    SmsVerifyCatcher smsVerifyCatcher;
    public RegisWithOTPFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mobile_number, container, false);

        INIT();

        return view;
    }

    private void INIT() {


        sm = new SessionManager(getContext());
        next =(ImageView) view.findViewById(R.id.next);
        countryCodePicker = (CountryCodePicker) view.findViewById(R.id.ccp);
        mobile = (EditText) view.findViewById(R.id.mobile);
        edt_code = (MaterialEditText) view.findViewById(R.id.edt_code);
        resend_otp = (BoldTextview) view.findViewById(R.id.resend_otp);
        hint = (NormalTextview) view.findViewById(R.id.hint);
        resend_progressbar = (ProgressBar) view.findViewById(R.id.resend_progressbar);
        progressbar_verifynumber = (ProgressBar) view.findViewById(R.id.progressbar_verifynumber);
        ll_mobile_layout = (LinearLayout) view.findViewById(R.id.ll_mobile_layout);
        ll_otp_layout = (LinearLayout) view.findViewById(R.id.ll_otp_layout);

//        TelephonyManager teleMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//        String localeCountry = teleMgr.getNetworkCountryIso();
//
//        if (localeCountry != null) {
//            Locale loc = new Locale("", localeCountry);
//            country_iso = String.valueOf(loc);
//            country_iso = country_iso.replace("_", "");
//        }
//        System.out.println("defaultctry>>>>> "+country_iso);
//        countryCodePicker.setCountryForNameCode(country_iso);

        ////country code change listener
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                country_iso = countryCodePicker.getSelectedCountryNameCode();
                country_code = countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        resend_progressbar.setVisibility(View.GONE);
        progressbar_verifynumber.setVisibility(View.GONE);
        ll_otp_layout.setVisibility(View.GONE);
        ll_mobile_layout.setVisibility(View.VISIBLE);
        next.setOnClickListener(this);
        resend_otp.setOnClickListener(this);


//        smsVerifyCatcher = new SmsVerifyCatcher(getActivity(), new OnSmsCatchListener<String>() {
//            @Override
//            public void onSmsCatch(String message) {
//
//                Pattern pattern = Pattern.compile("(\\d{4})");
//
////   \d is for a digit
////   {} is the number of digits here 4.
//
//                Matcher matcher = pattern.matcher(message);
//                String val = "";
//                if (matcher.find()) {
//                    val = matcher.group(0);  // 4 digit number
//                }
//
//                System.out.println("dasdasdasd "+message);
//                edt_code.setText(val);
//                if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
//                {
//                    VerifyOTp();
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                }
//                edt_code.setText(val);
//
//            }
//        });

    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        smsVerifyCatcher.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        smsVerifyCatcher.onStop();
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.next:

                if(isValidMobile11(mobile.getText().toString()))
                {

                    if(otpverfication)
                    {
                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                        {
                            VerifyOTp();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Alert_Message();
                    }


                }

                break;

            case R.id.resend_otp:

                if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                {
                    initiatePhoneVerification();
                    next.setVisibility(View.VISIBLE);
                    progressbar_verifynumber.setVisibility(View.GONE);
                    resend_progressbar.setVisibility(View.VISIBLE);
                    resend_otp.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }


                break;

        }

    }

    private void VerifyOTp() {

        next.setVisibility(View.GONE);
        progressbar_verifynumber.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("otp",edt_code.getText().toString());
            jsonObject.accumulate("phoneno",mobile.getText().toString());
            jsonObject.accumulate("country_code",country_iso);
            jsonObject.accumulate("fcm_id", FirebaseInstanceId.getInstance().getToken());
            jsonObject.accumulate("id", otpID);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        AndroidNetworking.post(Webservcie.check_otp)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


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

                            if(user.optInt("already")==1)
                            {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(getActivity(), InterestActivity.class);
                                startActivity(intent);
                            }



                        }
                        else
                        {
                            next.setVisibility(View.VISIBLE);
                            progressbar_verifynumber.setVisibility(View.GONE);

                            Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        next.setVisibility(View.VISIBLE);
                        progressbar_verifynumber.setVisibility(View.GONE);

                    }
                });





    }

    private void Alert_Message() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);
        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.app_name)+" will be authenticating the phone number you provided. Use the below options to proceed.");
        // Setting Icon to Dialog
        alertDialog.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                        {
                            initiatePhoneVerification();
                            next.setVisibility(View.GONE);
                            progressbar_verifynumber.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.dismiss();
                    }
                });
        // Showing Alert Message
        alertDialog.show();

    }

    private void initiatePhoneVerification() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("phoneno",country_code+mobile.getText().toString());
            jsonObject.accumulate("country_code",country_iso);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.send_otp)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(response.optString("success").equalsIgnoreCase("1"))
                        {

                            otpID = response.optString("id");
                            ll_mobile_layout.setVisibility(View.GONE);
                            ll_otp_layout.setVisibility(View.VISIBLE);
                            resend_progressbar.setVisibility(View.GONE);
                            resend_otp.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                            progressbar_verifynumber.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "OTP has been sent to your mobile number.", Toast.LENGTH_SHORT).show();
                            otpverfication = true;

                        }
                        else
                        {

                            ll_mobile_layout.setVisibility(View.VISIBLE);
                            otpverfication = false;
                            ll_otp_layout.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            progressbar_verifynumber.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        next.setVisibility(View.VISIBLE);
                        progressbar_verifynumber.setVisibility(View.GONE);

                    }
                });








    }

    private boolean isValidMobile11(String phone) {
        boolean check=false;
        String usNumberStr = phone;                                       //number to validate
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(usNumberStr, country_iso);            //with default country
            boolean isValid = phoneUtil.isValidNumber(usNumberProto);      //returns true
            if(isValid)
            {
                check=true;
            }
            else
            {
                mobile.setError("Please enter Valid mobile Number");
                check=false;
            }
            String usNumber = phoneUtil.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164); //+12025550100
        }catch (com.google.i18n.phonenumbers.NumberParseException e) {
            e.printStackTrace();
        }
        return check;
    }


}

package com.edison;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.Customfonts.EditText;
import com.edison.Object.UserObject;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static android.os.Build.VERSION_CODES.M;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    BoldTextview selected_dob;
    SessionManager sm;
    FontManager fm;
    EditText edt_fstname, edt_username, email,edt_mobile, txt_designantion,txt_description,txt_company;
    ImageView profile_img,back,changeprofilephoto;
    Button updateprofile;
    int flag = 0;
    String filepath="";
    private int year, month, day;
    ACProgressFlower acProgressFlower;
    private Calendar calendar;
    LinearLayout ll_par_num_lay,ll_industry,ll_functionalarea,ll_desgnation;
    int age = 0;
    //    private Spinner spinner,functional_area,industry;
    private Spinner spinner;
    String selecteditem="0";
    private static final String[] paths = {"Select","Male","Female"};
    CountryCodePicker countryCodePicker;
    String country_iso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        fm = new FontManager(this);
        sm = new SessionManager(this);

        if(NetworkDetector.isNetworkStatusAvialable(UpdateProfile.this))
        {

            INIT();

        }
        else
        {

            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void INIT() {

        edt_fstname = (EditText) findViewById(R.id.edt_fstname);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile);
        edt_username = (EditText) findViewById(R.id.edt_username);
        txt_designantion = (EditText) findViewById(R.id.txt_designantion);
        txt_description = (EditText) findViewById(R.id.txt_description);
        txt_company = (EditText) findViewById(R.id.txt_company);
        updateprofile = (Button) findViewById(R.id.updateprofile);
        selected_dob = (BoldTextview) findViewById(R.id.selected_dob);
        email = (EditText) findViewById(R.id.email);
        back = (ImageView) findViewById(R.id.back);
        profile_img = (ImageView) findViewById(R.id.profile_img);
        changeprofilephoto = (ImageView) findViewById(R.id.changeprofilephoto);
        spinner = (Spinner)findViewById(R.id.spinner);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateProfile.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        calendar = Calendar.getInstance();

//        calendar.add(Calendar.YEAR,-18);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String localeCountry = teleMgr.getNetworkCountryIso();


        if (localeCountry != null) {
            Locale loc = new Locale("", localeCountry);
            country_iso = String.valueOf(loc);
            country_iso = country_iso.replace("_", "");
        }
        countryCodePicker.setCountryForNameCode(country_iso);

        ////country code change listener
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                country_iso = countryCodePicker.getSelectedCountryNameCode();
            }
        });

        if(sm.getUserObject()!=null)
        {
            email.setText(sm.getUserObject().getEmail());
            edt_fstname.setText(sm.getUserObject().getName());
            edt_username.setText(sm.getUserObject().getUsername());
            edt_mobile.setText(sm.getUserObject().getPhoneno());
            txt_designantion.setText(sm.getUserObject().getDesignation());
            txt_description.setText(StringEscapeUtils.unescapeJava(sm.getUserObject().getDescription()));
            txt_company.setText(sm.getUserObject().getCompany());

//            if(sm.getUserObject().getGender().equalsIgnoreCase("Male"))
            if(sm.getUserObject().getGender().equalsIgnoreCase("1"))
            {
                spinner.setSelection(1);
            }
//            else if(sm.getUserObject().getGender().equalsIgnoreCase("Female"))
            else if(sm.getUserObject().getGender().equalsIgnoreCase("2"))
            {
                spinner.setSelection(2);
            }
            else
            {
                spinner.setSelection(0);
            }
            if(sm.getUserObject().getDob().length()>5)
            {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

                Date date1 = null;
                try {
                    date1 = inputFormat.parse(sm.getUserObject().getDob());
//                    getAge(date1.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String date = outputFormat.format(date1);

                selected_dob.setText(date);

            }

            if(sm.getUserObject().getCountry_code().length()>0)
            {
                countryCodePicker.setCountryForNameCode(sm.getUserObject().getCountry_code());
            }
            else
            {

            }


            Picasso.with(UpdateProfile.this).load(sm.getUserObject().getProfile_image())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(getResources().getDrawable(R.drawable.profile_img))
                    .into(profile_img);

        }






        back.setOnClickListener(this);
        updateprofile.setOnClickListener(this);
        selected_dob.setOnClickListener(this);
        changeprofilephoto.setOnClickListener(this);
        profile_img.setOnClickListener(this);


    }
    //validateEmail
    public boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
        if (email.getText().toString().trim().matches(emailPattern))
        {
            return true;
        }
        else
        {
            email.setError("Enter valid email");
            return false;
        }

    }
    private void Update() {

        String date = "";
        if(selected_dob.getText().toString().trim().length()>0){
            DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date1 = null;
            try {
                date1 = inputFormat.parse(selected_dob.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date = outputFormat.format(date1);
        }




        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.accumulate("name",edt_fstname.getText().toString());
            jsonObject.accumulate("username",edt_username.getText().toString());
            jsonObject.accumulate("email",email.getText().toString());
            jsonObject.accumulate("phoneno",edt_mobile.getText().toString());
            jsonObject.accumulate("country_code",country_iso);
            jsonObject.accumulate("dob",date);
            jsonObject.accumulate("gender",selecteditem);
            jsonObject.accumulate("description",StringEscapeUtils.escapeJava(txt_description.getText().toString()));
            jsonObject.accumulate("designation",txt_designantion.getText().toString());
            jsonObject.accumulate("company_name",txt_company.getText().toString());
            jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
        }
        catch (Exception e)
        {

        }
        AndroidNetworking.post(Webservcie.update_profile)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        acProgressFlower.dismiss();

                        try {

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
                                Toast.makeText(UpdateProfile.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            else
                            {
                                Toast.makeText(UpdateProfile.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.back:
                onBackPressed();
                break;

            case R.id.profile_img:
                Intent intent = new Intent(UpdateProfile.this, Full_ImageViewActivity.class);
                intent.putExtra("url",sm.getUserObject().getProfile_image());
                startActivity(intent);
                break;

            case R.id.changeprofilephoto:
                if (Build.VERSION.SDK_INT >= M) {
                    if (UpdateProfile.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            UpdateProfile.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            UpdateProfile.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        selectImage();

                    } else {
                        ActivityCompat.requestPermissions(UpdateProfile.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                1);
                    }
                } else {
                    selectImage();
                }
                break;

            case R.id.updateprofile:

                if(validateEmail()&&validateName()
                        && validateLastName()
                        && isValidMobile(edt_mobile.getText().toString())
                        && validateCompany()
                        && validateDesignation()

                        )
                {
                    if(NetworkDetector.isNetworkStatusAvialable(UpdateProfile.this))
                    {

                        Update();
                        acProgressFlower = new ACProgressFlower.Builder(UpdateProfile.this)
                                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                .themeColor(Color.WHITE)
                                .text("")
                                .fadeColor(Color.DKGRAY).build();
                        acProgressFlower.show();
                        acProgressFlower.setCancelable(false);
                    }
                    else
                    {
                        Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
                    }
                }


                break;

            case R.id.selected_dob:

                final Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -13);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                long starttimem_millisec = 0;
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                Date cc = c.getTime();
                starttimem_millisec = cc.getTime();



                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfile.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                selected_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year ;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date date = sdf.parse(dateString);
//                                    getAge(date.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, year, month, day);
                datePickerDialog.show();

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
//                datePickerDialog.getDatePicker().setMinDate(starttimem_millisec);

                break;



        }

    }


    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UpdateProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File filesDir = UpdateProfile.this.getFilesDir();
                    File imageFile = new File(filesDir, "captured2" + ".jpg");
                    filepath = imageFile.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(UpdateProfile.this,
                            getApplicationContext().getPackageName() + ".com.sloth.provider", imageFile.getAbsoluteFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Bitmap bitmap = (BitmapFactory.decodeFile(filepath));

                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }

                profile_img.setImageBitmap(rotatedBitmap);

                File filesDir = UpdateProfile.this.getFilesDir();
                File imageFile = new File(filesDir, "from_camera" + ".jpg");

                FileOutputStream outFile;

                filepath = imageFile.getAbsolutePath();

                try {
                    outFile = new FileOutputStream(imageFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);

                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new ImageCompressionAsyncTask(UpdateProfile.this).execute();



            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath1 = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath1, null, null, null);

                if (c != null) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath1[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(picturePath);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap rotatedBitmap = null;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }
                    profile_img.setImageBitmap(rotatedBitmap);

                    File filesDir = UpdateProfile.this.getFilesDir();
                    File imageFile = new File(filesDir, "fro_crop4" + ".jpg");

                    FileOutputStream outFile;

                    filepath = imageFile.getAbsolutePath();

                    try {
                        outFile = new FileOutputStream(imageFile);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);

                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    new ImageCompressionAsyncTask(UpdateProfile.this).execute();



                } else {
                    Toast.makeText(this, "Could not load this image", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            File filesDir = UpdateProfile.this.getFilesDir();
            File imageFile = new File(filesDir, "fro_crop5" + ".jpg");
            String filePathaa = SiliCompressor.with(mContext).compress(filepath,imageFile.getAbsoluteFile());
            return filePathaa;

        }

        @Override
        protected void onPostExecute(String s) {

            File imageFile = new File(s);

            filepath = imageFile.getPath();

            if(NetworkDetector.isNetworkStatusAvialable(UpdateProfile.this))
            {

                Upload_Image(filepath);

            }
            else
            {
                Toast.makeText(UpdateProfile.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void Upload_Image(String filepath) {
        acProgressFlower = new ACProgressFlower.Builder(UpdateProfile.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);
        AndroidNetworking.upload(Webservcie.update_profile_image)
                .addMultipartFile("profile_image",new File(filepath))
                .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject1) {


                        acProgressFlower.dismiss();

                        try {

                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {

                                JSONObject user = jsonObject1.optJSONObject("user");
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
                                Toast.makeText(UpdateProfile.this, jsonObject1.optString("message"), Toast.LENGTH_SHORT).show();
                                finish();



                            }

                            else
                            {
                                Toast.makeText(UpdateProfile.this, jsonObject1.optString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });
    }


    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    ///validate edt_username
    public boolean validateName() {
        if (edt_fstname.getText().toString().length() > 0) {
            return true;
        } else {
            edt_fstname.setError("Please enter Name");
            return false;
        }
    }

    ///validate edt_username
    public boolean validateLastName() {
        if (edt_username.getText().toString().length() > 0) {
            return true;
        } else {
            edt_username.setError("Please enter User Name");
            return false;
        }
    }

    ///validate edt_username
    public boolean validateDesignation() {
        if (txt_designantion.getText().toString().length() > 0) {
            return true;
        } else {
            txt_designantion.setError("Please enter your designation");
            return false;
        }
    }
    ///validate edt_username
    public boolean validateCompany() {
        if (txt_company.getText().toString().length() > 0) {
            return true;
        } else {
            txt_company.setError("Please enter your Company name");
            return false;
        }
    }


    ///validate dob
    public boolean validateDOB()
    {
        if(selected_dob.getText().toString().length()>0)
        {
            return true;
        }
        else
        {
            Toast.makeText(this, "Please select your DOB", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (phone.trim().length() > 0) {

            String usNumberStr = phone;                                       //number to validate
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber usNumberProto = phoneUtil.parse(usNumberStr, country_iso);            //with default country
                boolean isValid = phoneUtil.isValidNumber(usNumberProto);      //returns true
                if (isValid) {

                    check = true;
                } else {

                    edt_mobile.setError("Please enter Valid mobile Number");
                    check = false;
                }
                String usNumber = phoneUtil.format(usNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164); //+12025550100
            } catch (com.google.i18n.phonenumbers.NumberParseException e) {
                e.printStackTrace();
            }
        } else

        {
            check = true;
        }


        return check;

//        boolean check = false;
//        if (!Pattern.matches("[a-zA-Z]+", phone)) {
//            if (phone.length() < 6 || phone.length() > 13) {
//                // if(phone.length() != 10) {
//                check = false;
//                edt_mobile.setError("Please enter Valid Number");
//            } else {
//                check = true;
//            }
//        } else {
//            check = false;
//        }
//        return check;
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                selecteditem="0";
                break;
            case 1:
                selecteditem="1";
                break;
            case 2:
                selecteditem="2";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        selecteditem="0";
    }


}

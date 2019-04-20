package com.edison.MainFragment;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.MainActivity;
import com.edison.Object.FeedObject;
import com.edison.R;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Utils;
import com.edison.Utils.Webservcie;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.WINDOW_SERVICE;
import static android.os.Build.VERSION_CODES.M;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFeedFragment extends Fragment implements View.OnClickListener {

    View view;
    SessionManager sm;
    ImageView userimagea,choosevideo,chooseimage,reselect_media,close;
    Button upload_feed;
    String filepath="";
    ImageView feed_image;
    EditText feed_description;
    BoldTextview txtPercentage;
    ACProgressFlower acProgressFlower;
    private static final int SELECT_VIDEO = 300;
    int type = 1;
    String title,description,oldlocation,feedid;
    ProgressBar progressBar;
    RelativeLayout btm_layout;
    LinearLayout ll_progressbar,ll_data;
    boolean pressed = false;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int RESULT_LOAD_IMAGE1 = 255;
    String postid,content,diff;
    ImageLoader imageLoader;
    RecyclerView imagelist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_feed, container, false);

        Init();

        return view;
    }

    private void Init() {


        sm = new SessionManager(getActivity());
        btm_layout = (RelativeLayout) view.findViewById(R.id.btm_layout);
        userimagea = (ImageView) view.findViewById(R.id.userimagea);
        chooseimage = (ImageView) view.findViewById(R.id.chooseimage);
        choosevideo = (ImageView) view.findViewById(R.id.choosevideo);
        close = (ImageView) view.findViewById(R.id.close);
        ll_data = (LinearLayout) view.findViewById(R.id.ll_data);
        ll_progressbar = (LinearLayout) view.findViewById(R.id.ll_progressbar);
        feed_image = (ImageView) view.findViewById(R.id.feed_image);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        upload_feed = (Button) view.findViewById(R.id.upload_feed);
        txtPercentage = (BoldTextview) view.findViewById(R.id.txtPercentage);
        feed_description = (EditText) view.findViewById(R.id.feed_description);
        imagelist = (RecyclerView) view.findViewById(R.id.imagelist);

        feed_image.setVisibility(View.VISIBLE);
        chooseimage.setVisibility(View.VISIBLE);
        choosevideo.setVisibility(View.VISIBLE);

        upload_feed.setOnClickListener(this);
        choosevideo.setOnClickListener(this);
        chooseimage.setOnClickListener(this);
        close.setOnClickListener(this);

        WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int width1 = displayMetrics.widthPixels;

        double width = Double.parseDouble(String.valueOf(width1));
        double height = width/10;
        height = height * 3;
        int new_height = (int) Math.round(height);
        android.view.ViewGroup.LayoutParams layoutParams = feed_image.getLayoutParams();

        if (Build.VERSION.SDK_INT >= M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1);


            }
        }
        else
        {
        }


        Picasso.with(getActivity()).load(sm.getUserObject().getProfile_image()).into(userimagea);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }

            case 55: {

                return;
            }

        }
    }

    private void selectImage() {

        final CharSequence[] options =
                {"Capture Image",
                        "Choose Image from Gallery",
                        "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Capture Image")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File filesDir = getActivity().getFilesDir();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "captured2" + timeStamp + ".jpg";
                    File imageFile = new File(filesDir, imageFileName);
                    type = 2;
                    filepath = imageFile.getAbsolutePath();

                    Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".com.sloth.provider", imageFile.getAbsoluteFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose Image from Gallery")) {
//                    layout.setVisibility(View.VISIBLE);
                    type = 2;
                    feed_image.setVisibility(View.VISIBLE);
//                    reselect_video.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
//

                    dialog.cancel();

                }
            }
        });
        builder.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.upload_feed:


                if(filepath.length()>0 || (feed_description.getText().toString().trim().length()>0))
                {


                    if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
                    {
                       if(type==2)
                        {
//                            new ImageCompressionAsyncTask(this).execute();
                        }
                        else
                        {
                            Upload_feed();
                        }

                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }


                }else
                {
                    Toast.makeText(getActivity(), "Enter something to post", Toast.LENGTH_SHORT).show();
                }

                break;



            case R.id.choosevideo:
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                3);
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
                    }
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
                }



                break;
            case R.id.chooseimage:
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                55);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Working on it, You can check with CONTENT feed.....Thanks", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Working on it, You can check with CONTENT feed.....Thanks", Toast.LENGTH_SHORT).show();
                }



                break;

            case R.id.close:

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                break;



        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == Activity.RESULT_OK) {

            String imageEncoded;
            List<String> imagesEncodedList;
            imagesEncodedList = new ArrayList<String>();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            if(data.getData()!=null){

                Uri mImageUri=data.getData();

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(mImageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded  = cursor.getString(columnIndex);
                cursor.close();

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        imagesEncodedList.add(imageEncoded);
                        cursor.close();

                    }
                    Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                }
            }

            String[] all_path = data.getStringArrayExtra("all_path");

            System.out.println(">>>DSf>>>@#@#@# ");
            for (String string : imagesEncodedList)
            {
                System.out.println("$#@#@$#@$#@$ "+string);
            }

        }
    }


    private void Upload_feed() {

        hideKeyboard(getActivity());
        acProgressFlower = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.cancel();
        acProgressFlower.setCancelable(false);
        progressBar.setVisibility(View.VISIBLE);
        ll_progressbar.setVisibility(View.VISIBLE);
        pressed = true;
        ll_data.setVisibility(View.GONE);
        btm_layout.setVisibility(View.GONE);

        String path = "";

        if(type==1)
        {


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
                jsonObject.accumulate("message",StringEscapeUtils.escapeJava(feed_description.getText().toString()));
                jsonObject.accumulate("type","1");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            AndroidNetworking.upload(Webservcie.post1)
                    .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                    .addMultipartParameter("message", StringEscapeUtils.escapeJava(feed_description.getText().toString()))
                    .addMultipartParameter("type", "1")
//                    .addMultipartFile("preview_image",new File(path))
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            // below code will be executed in the executor provided
                            // do anything with response
                            progressBar.setVisibility(View.GONE);
                            acProgressFlower.dismiss();
                            System.out.println(">>>response.... "+response);
                            ll_progressbar.setVisibility(View.GONE);
                            ll_data.setVisibility(View.VISIBLE);
                            btm_layout.setVisibility(View.VISIBLE);
                            if(response.optString("success").equalsIgnoreCase("1"))
                            {

                                Realm realm = Realm.getDefaultInstance();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        try {
                                            realm.createOrUpdateAllFromJson(FeedObject.class, response.optJSONArray("posts"));
                                            RealmResults<FeedObject> homeObjectRealmResults =  realm.where(FeedObject.class).findAll();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            realm.close();
                                        }
                                    }
                                });

                                Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error

                            System.out.println(">@#@#$@$#@ "+error);

                            acProgressFlower.dismiss();
                        }
                    });
        }

        else if(type==2)
        {
            ArrayList<File> fileArrayList = new ArrayList<>();

            AndroidNetworking.upload(Webservcie.post1)
                    .addMultipartFileList("file_path",fileArrayList)
                    .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                    .addMultipartParameter("message",StringEscapeUtils.escapeJava(feed_description.getText().toString()))
                    .addMultipartParameter("type", "2")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            long divisor = totalBytes / 100;
                            int progressAmount = (int)(bytesUploaded / divisor);
                            progressBar.setVisibility(View.VISIBLE);

                            System.out.println(">>>>progressAmount>>>>> "+progressAmount);

                            // updating progress bar value
                            progressBar.setProgress(progressAmount);

                            // updating percentage value
                            txtPercentage.setText(String.valueOf(progressAmount) + "%");
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            // below code will be executed in the executor provided
                            // do anything with response
                            acProgressFlower.dismiss();
                            System.out.println(">>>.... "+response);

                            if(response.optString("success").equalsIgnoreCase("1"))
                            {
                                Realm realm = Realm.getDefaultInstance();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        try {
                                            realm.createOrUpdateAllFromJson(FeedObject.class, response.optJSONArray("posts"));
                                            RealmResults<FeedObject> homeObjectRealmResults =  realm.where(FeedObject.class).findAll();
                                            System.out.println(">>after> "+homeObjectRealmResults.size());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            realm.close();
                                        }
                                    }
                                });
                                Toast.makeText(getActivity(), response.optString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            acProgressFlower.dismiss();
                        }
                    });
        }
        else
        {

        }


    }
}

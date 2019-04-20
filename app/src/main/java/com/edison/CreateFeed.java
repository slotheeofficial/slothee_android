package com.edison;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.Button;
import com.edison.Object.CustomGallery;
import com.edison.Object.FeedObject;
import com.edison.Object.SelectedImage;
import com.edison.Utils.Action;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
import com.googlecode.mp4parser.srt.SrtParser;
import com.iceteck.silicompressorr.SiliCompressor;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.os.Build.VERSION_CODES.M;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class CreateFeed extends AppCompatActivity implements View.OnClickListener {

    SessionManager sm;
    ImageView userimagea,choosevideo,chooseimage,reselect_media,close;
    Button upload_feed;
    String filepath="";
    ImageView feed_image;
    EditText feed_description;
    BoldTextview txtPercentage,textcount;
    ACProgressFlower acProgressFlower;
    private static final int SELECT_VIDEO = 300;
    int type = 1;
    String title,description,oldlocation,feedid;
    ProgressBar progressBar;
    LinearLayout ll_progressbar,ll_data;
    boolean pressed = false;
    private static final int RESULT_LOAD_IMAGE1 = 255;
    String postid,content,diff;
//    RecyclerView imagelist;
    GridView imagelist;
    GridAdapter gridAdapter;

    Handler handler;
    ImageListRecyclerAdapter imageListRecyclerAdapter;
    //GalleryAdapter adapter;
    ImageLoader imageLoader;
    private HashMap<String, CustomGallery> imagesUri;
    ArrayList<CustomGallery> dataT;
    ArrayList<String> selected_ImageFile;
    ArrayList<String> compressedFilePth;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_feed);

        Init();

    }

    private void Init() {



        realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

               realm.where(SelectedImage.class).findAll().clear();

            }
        });

        sm = new SessionManager(CreateFeed.this);
        dataT = new ArrayList<CustomGallery>();
        selected_ImageFile = new ArrayList<>();
        compressedFilePth = new ArrayList<>();
        userimagea = (ImageView) findViewById(R.id.userimagea);
        chooseimage = (ImageView) findViewById(R.id.chooseimage);
        choosevideo = (ImageView) findViewById(R.id.choosevideo);
        close = (ImageView) findViewById(R.id.close);
        ll_data = (LinearLayout) findViewById(R.id.ll_data);
        ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar);
        feed_image = (ImageView) findViewById(R.id.feed_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        upload_feed = (Button) findViewById(R.id.upload_feed);
        txtPercentage = (BoldTextview) findViewById(R.id.txtPercentage);
        feed_description = (EditText) findViewById(R.id.feed_description);
        textcount = (BoldTextview) findViewById(R.id.textcount);
//      imagelist = (RecyclerView) findViewById(R.id.imagelist);
        imagelist = (GridView) findViewById(R.id.imagelist);


        TextWatcher mTextEditorWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                textcount.setText(String.valueOf(s.length())+"/200");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        feed_description.addTextChangedListener(mTextEditorWatcher);


        feed_image.setVisibility(View.VISIBLE);
        chooseimage.setVisibility(View.VISIBLE);
        choosevideo.setVisibility(View.VISIBLE);

        upload_feed.setOnClickListener(this);
        choosevideo.setOnClickListener(this);
        chooseimage.setOnClickListener(this);
        close.setOnClickListener(this);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int width1 = displayMetrics.widthPixels;

        double width = Double.parseDouble(String.valueOf(width1));
        double height = width/10;
        height = height * 3;
        int new_height = (int) Math.round(height);
        android.view.ViewGroup.LayoutParams layoutParams = feed_image.getLayoutParams();

        if (Build.VERSION.SDK_INT >= M) {
            if (CreateFeed.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    CreateFeed.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    CreateFeed.this.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            }
            else
            {
                ActivityCompat.requestPermissions(CreateFeed.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1);


            }
        }
        else
        {
        }


        Picasso.with(CreateFeed.this).load(sm.getUserObject().getProfile_image()).into(userimagea);


        handler = new Handler();
//        imagelist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
//        imageListRecyclerAdapter = new ImageListRecyclerAdapter(getApplicationContext());
//        imageListRecyclerAdapter.setMultiplePick(false);
//        imagelist.setAdapter(imageListRecyclerAdapter);


        RealmResults<SelectedImage> selectedImageRealmResults = realm.where(SelectedImage.class).findAll();
        gridAdapter = new GridAdapter(CreateFeed.this, selectedImageRealmResults);
        imagelist.setAdapter(gridAdapter);

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

                RealmResults<SelectedImage> selectedImageRealmResults = realm.where(SelectedImage.class).findAll();
                if(selectedImageRealmResults.size()>0 || (feed_description.getText().toString().trim().length()>0))
                {


                    if(NetworkDetector.isNetworkStatusAvialable(CreateFeed.this))
                    {

                        if(selectedImageRealmResults.size()>0)
                        {
                            type = 2;

                            for(int i=0;i<selectedImageRealmResults.size();i++)
                            {
                                new ImageCompressionAsyncTask(CreateFeed.this).execute(selectedImageRealmResults.get(i).getOriginalImagePath());
                            }



                        }
                        else
                        {
                            Upload_feed();
                        }

                    }
                    else
                    {
                        Toast.makeText(CreateFeed.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                    }


                }else
                {
                    Toast.makeText(CreateFeed.this, "Enter something to post", Toast.LENGTH_SHORT).show();
                }

                break;



            case R.id.choosevideo:
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ContextCompat.checkSelfPermission(CreateFeed.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateFeed.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                3);
                    }
                    else
                    {



                        Intent intent = new Intent(CreateFeed.this,CustomPhotoGalleryActivity.class);

                        startActivityForResult(intent,RESULT_LOAD_IMAGE1);

//                        Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
//                        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
//                        startActivityForResult(intent, RESULT_LOAD_IMAGE1);
                    }
                }
                else
                {
//                    Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
//                    intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
//                    startActivityForResult(intent, RESULT_LOAD_IMAGE1);

                    Intent intent = new Intent(CreateFeed.this,CustomPhotoGalleryActivity.class);

                    startActivityForResult(intent,RESULT_LOAD_IMAGE1);

                }



                break;
            case R.id.chooseimage:
                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (ContextCompat.checkSelfPermission(CreateFeed.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateFeed.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                55);
                    }
                    else
                    {
                        ChooseCameraImage();
                    }
                }
                else
                {
                    ChooseCameraImage();
                }



                break;

            case R.id.close:

                Intent intent = new Intent(CreateFeed.this, MainActivity.class);
                startActivity(intent);

                break;



        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == Activity.RESULT_OK) {

//            String[] all_path = data.getStringArrayExtra("all_path");
//
            RealmResults<SelectedImage> selectedImageRealmResults = realm.where(SelectedImage.class).findAll();
//            for (SelectedImage s : selectedImageRealmResults) {
//
//                selected_ImageFile.add(s.getOriginalImagePath());
//            }

            gridAdapter.notifyDataSetChanged();




//
//            for (String string : all_path) {
//
//                System.out.println(">>DSFSDf "+string);
//
//                CustomGallery item = new CustomGallery();
//                item.sdcardPath = string;
//
//                dataT.add(item);
//                selected_ImageFile.add(string);
//            }
//
//            imageListRecyclerAdapter.addAll(dataT);
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 102) {
                File f = new File(CreateFeed.this.getFilesDir().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("captured2.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(filepath,
                            bitmapOptions);
//                    storeCameraPhotoInSDCard(bitmap);
                    ExifInterface ei = new ExifInterface(filepath);
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


                    File filesDir = CreateFeed.this.getFilesDir();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "from_camera" + timeStamp + ".jpg";
                    final File imageFile = new File(filesDir, imageFileName);
                    FileOutputStream outFile;

                    filepath = imageFile.getAbsolutePath();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            SelectedImage selectedImage = new SelectedImage();
                            selectedImage.setOriginalImagePath(imageFile.getAbsolutePath());
                            selectedImage.setCompressedImagePath(imageFile.getAbsolutePath());
                            realm.copyToRealmOrUpdate(selectedImage);

                        }
                    });

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

//                    new ImageCompressionAsyncTask(CreateFeed.this).execute(imageFile.getAbsolutePath());
                    gridAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void ChooseCameraImage() {

        RealmResults<SelectedImage> selectedImageRealmResults = realm.where(SelectedImage.class).findAll();
        if(selectedImageRealmResults.size()<6)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File filesDir = CreateFeed.this.getFilesDir();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            String imageFileName = "captured2" + timeStamp + ".jpg";
            File imageFile = new File(filesDir, imageFileName);
            filepath = imageFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(CreateFeed.this,
                    CreateFeed.this.getPackageName() + ".com.sloth.provider",
                    imageFile.getAbsoluteFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 102);
        }
        else
        {
            Toast.makeText(this, "You can upload only 6 images", Toast.LENGTH_SHORT).show();
        }


    }


    class ImageCompressionAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            File filesDir = CreateFeed.this.getFilesDir();
            File imageFile = new File(getFilesDir()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");

//          File imageFile = new File(filesDir, "fro_crop5" + ".jpg");
            String filePathaa = SiliCompressor.with(mContext).compress(params[0],imageFile.getAbsoluteFile());
            return filePathaa;

        }

        @Override
        protected void onPostExecute(String s) {

            System.out.println(">$@#4 "+s);
//          compressedFilePth.add(new File(s));
            compressedFilePth.add(s);
//          File imageFile = new File(s);
//          filepath = imageFile.getPath();

            RealmResults<SelectedImage> selectedImageRealmResults = realm.where(SelectedImage.class).findAll();
            if(selectedImageRealmResults.size()==compressedFilePth.size())
            {
                Upload_feed();
            }

        }
    }

    private void Upload_feed() {

        hideKeyboard(CreateFeed.this);
        acProgressFlower = new ACProgressFlower.Builder(CreateFeed.this)
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

        String path = "";


        if(type==1)
        {


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("user_id",sm.getUserObject().getUser_id());
                jsonObject.accumulate("message", StringEscapeUtils.escapeJava(feed_description.getText().toString()));
                jsonObject.accumulate("type","1");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            AndroidNetworking.upload(Webservcie.post1)
//                    .addMultipartFile("file_path", new File(filepath))
                    .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                    .addMultipartParameter("message", StringEscapeUtils.escapeJava(feed_description.getText().toString()))
                    .addMultipartParameter("type", "1")
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
                            ll_progressbar.setVisibility(View.GONE);
                            ll_data.setVisibility(View.VISIBLE);
                            if(response.optString("success").equalsIgnoreCase("1"))
                            {

                                Toast.makeText(CreateFeed.this, response.optString("message"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CreateFeed.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error


                            acProgressFlower.dismiss();
                        }
                    });
        }

        else if(type==2)
        {


            AndroidNetworking.upload(Webservcie.post1)

                    .addMultipartFileList("file_path[]",sendLIst())
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


                            // updating progress bar value
                            progressBar.setProgress(progressAmount);

                            // updating percentage value
                            txtPercentage.setText(String.valueOf(progressAmount) + "%");
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            acProgressFlower.dismiss();

                            if(response.optString("success").equalsIgnoreCase("1"))
                            {
//
                                Toast.makeText(CreateFeed.this, response.optString("message"), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CreateFeed.this,MainActivity.class);
                                startActivity(intent);
                                finish();

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

    private List<File> sendLIst() {

        ArrayList<File> fileArrayList = new ArrayList<>();

        for (String f : compressedFilePth) {

            fileArrayList.add(new File(f));


        }


        return fileArrayList;

    }


    public class GridAdapter extends BaseAdapter
    {
        RealmResults<SelectedImage> selectedImageRealmResults;
        Context context;
        LayoutInflater inflater;

        public GridAdapter(Context context,RealmResults<SelectedImage> selectedImageRealmResults)
        {
            this.context = context;
            this.selectedImageRealmResults = selectedImageRealmResults;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount()
        {
            return selectedImageRealmResults.size();
        }

        @Override
        public Object getItem(int i)
        {
            return selectedImageRealmResults.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        public View getView(int i, View convertView, ViewGroup viewGroup) {

            final ViewHolder viewHolder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.gridview, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imagev);
                viewHolder.deleteimage = (ImageView) convertView.findViewById(R.id.deleteimage);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            final SelectedImage result = selectedImageRealmResults.get(i);

            ///calculate new width and height
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int width1 = displayMetrics.widthPixels;

            double width = Double.parseDouble(String.valueOf(width1));
            double height = width/3;
            int new_height = (int) Math.round(height);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.imageView.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = new_height;
            viewHolder.imageView.setLayoutParams(layoutParams);

            viewHolder.deleteimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final SelectedImage selectedImage = realm.where(SelectedImage.class)
                            .contains("originalImagePath",result.getOriginalImagePath().trim()).findFirst();
                    if(selectedImage!=null)
                    {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                selectedImage.removeFromRealm();
                                notifyDataSetChanged();
                            }
                        });
                    }

                }
            });

            Picasso.with(CreateFeed.this).load("file://" +result.getOriginalImagePath().trim())
                    .into(viewHolder.imageView);

            return convertView;
        }


    }
     static class ViewHolder
    {
        private  ImageView deleteimage,imageView;
    }

}

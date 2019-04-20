package com.edison;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.EventBus.EventMessage;
import com.edison.Object.O2OChat;
import com.edison.Object.TempIBobjt;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.os.Build.VERSION_CODES.M;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class OneToOneChat extends AppCompatActivity implements View.OnClickListener {

    Realm realm;
    FontManager fm;
    SessionManager sm;
    ImageView backtoinbox, chatSendButton, chooseimage, chooseGallery;
    EditText edt_msg;
    TextView  receiver_name;
    RealmResults<O2OChat> qbChatMessageRealmRealmResults;
    String filepath = "";
    BoldTextview settings;

    private ListView chat_list;
    //    StickyListHeadersListView stickyList;
    RelativeLayout ll_text;
    TempIBobjt iBoxObj;
    MyAdapter myAdapter;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int SELECT_VIDEO = 300;
    private static final int ATTACH_FILE = 400;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    RelativeLayout btm_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);

        iBoxObj = new Gson().fromJson(getIntent().getStringExtra("qbchatdialog"),TempIBobjt.class);
        INIT();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventFromService(EventMessage event){

        if(event.getNotification().equalsIgnoreCase("chat121"))
        {
            try {
                myAdapter.notifyDataSetChanged();
            }
            catch (Exception e)
            {

            }
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume()
    {
        myAdapter.notifyDataSetChanged();
        super.onResume();
    }


    private void INIT() {


        this.realm = Realm.getDefaultInstance();
        fm = new FontManager(this);
        sm = new SessionManager(this);

        backtoinbox = (ImageView) findViewById(R.id.backtoinbox);
        chat_list = (ListView) findViewById(R.id.chat_list);
        settings = (BoldTextview) findViewById(R.id.settings);
        edt_msg = (EditText) findViewById(R.id.edt_msg);
        receiver_name = (TextView) findViewById(R.id.receiver_name);
        chatSendButton = (ImageView) findViewById(R.id.chatSendButton);
        chooseimage = (ImageView) findViewById(R.id.chooseimage);
        chooseGallery = (ImageView) findViewById(R.id.chooseGallery);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ll_text = (RelativeLayout) findViewById(R.id.ll_text);
        btm_layout = (RelativeLayout) findViewById(R.id.btm_layout);


        receiver_name.setText(iBoxObj.getGroup_name());


        fm.setVericalIcon(settings);

        qbChatMessageRealmRealmResults = realm.where(O2OChat.class).
                contains("dialog_id", iBoxObj.getDialog_id()).findAll();

        myAdapter = new MyAdapter(OneToOneChat.this,qbChatMessageRealmRealmResults);
        chat_list.setAdapter(myAdapter);


        if (NetworkDetector.isNetworkStatusAvialable(OneToOneChat.this)) {
            getMessage();
        } else {
            Toast.makeText(OneToOneChat.this, "Please Check your Internet Connections ", Toast.LENGTH_SHORT).show();
        }

        chatSendButton.setOnClickListener(this);
        chooseimage.setOnClickListener(this);
        backtoinbox.setOnClickListener(this);
        chooseGallery.setOnClickListener(this);
        receiver_name.setOnClickListener(this);
        settings.setOnClickListener(this);




    }

    public void getMessage() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("sender_id",sm.getUserObject().getUser_id());
            jsonObject.accumulate("receiver_id",iBoxObj.getGroup_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.ChatConversation)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {




                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {

                                        realm.createAllFromJson(O2OChat.class, response.optJSONArray("data"));
//                                        realm.createOrUpdateAllFromJson(O2OChat.class, response.optJSONArray("data"));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        realm.close();
                                    }


                                    myAdapter.notifyDataSetChanged();

                                }
                            });



                        }



                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(">>>>.. " + anError);
                    }
                });



    }

    public void UpdateReadStatus() {

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(OneToOneChat.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.cancel();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("sender_id", iBoxObj.getGroup_id());
            jsonObject.accumulate("receiver_id", sm.getUserObject().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.clear_chat)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        acProgressFlower.dismiss();
                        try {

                            if(response.optString("success").equalsIgnoreCase("1"))
                            {
                                finish();
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
            case R.id.chatSendButton:

                if (edt_msg.getText().toString().trim().length() > 0) {

                    if (NetworkDetector.isNetworkStatusAvialable(OneToOneChat.this))
                    {
                        String uniquefield = String.valueOf(new java.util.Date().getTime());
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String newDateStr = format1.format(new java.util.Date().getTime());

                        realm.beginTransaction();
                        O2OChat chatMessage = new O2OChat();
                        chatMessage.setMessage(edt_msg.getText().toString());
                        chatMessage.setDialog_id(iBoxObj.getDialog_id());
                        chatMessage.setAndriod_unique_id(uniquefield);
                        chatMessage.setChat_message_id(uniquefield);
                        chatMessage.setMessge_type("1");
                        chatMessage.setCreated_at(newDateStr);
                        chatMessage.setSender_id(sm.getUserObject().getUser_id());
                        realm.copyToRealmOrUpdate(chatMessage);
                        realm.commitTransaction();
                        myAdapter.notifyDataSetChanged();

                        String message = StringEscapeUtils.escapeJava(edt_msg.getText().toString());
                        sendMessageToReceiver(message,uniquefield,"1", chatMessage);
                        edt_msg.setText("");
                    }
                    else
                    {
                        Toast.makeText(OneToOneChat.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }



                } else {
                    Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.backtoinbox:
                finish();
                break;
            case R.id.settings:

                PopupMenu popupMenu = new PopupMenu(OneToOneChat.this,view);
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem menuItem) {

                       switch (menuItem.getItemId())
                       {
                           case R.id.clearchat:

                               new android.support.v7.app.AlertDialog.Builder(OneToOneChat.this)
                                       .setIcon(android.R.drawable.ic_dialog_alert)
                                       .setTitle(R.string.app_name)
                                       .setMessage("Are you sure want to delete chat ?")
                                       .setPositiveButton("Delete chat", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {

                                               if(NetworkDetector.isNetworkStatusAvialable(OneToOneChat.this))
                                               {
                                                   Realm realm = Realm.getDefaultInstance();
                                                   realm.executeTransaction(new Realm.Transaction() {
                                                       @Override
                                                       public void execute(Realm realm) {
                                                           try {

                                                               realm.where(O2OChat.class).contains("dialog_id", iBoxObj.getDialog_id()).findAll().clear();

                                                           }
                                                           catch (Exception e) {
                                                               e.printStackTrace();
                                                           } finally {
                                                               realm.close();
                                                           }


                                                           myAdapter.notifyDataSetChanged();

                                                           UpdateReadStatus();


                                                       }
                                                   });

                                               }


                                           }

                                       })
                                       .setNegativeButton("Cancel", null)
                                       .show();

                               break;
                       }

                       return false;
                   }
               });
               popupMenu.inflate(R.menu.clearchat);
                popupMenu.show();
                break;
            case R.id.receiver_name:

                Intent intent2 = new Intent(OneToOneChat.this,OtherUserProfileActivity.class);
                intent2.putExtra("id",String.valueOf(iBoxObj.getGroup_id()));
                intent2.putExtra("name",iBoxObj.getGroup_name());
                startActivity(intent2);

                break;

            case R.id.chooseimage:

                if (Build.VERSION.SDK_INT >= M) {
                    if (OneToOneChat.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            OneToOneChat.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            OneToOneChat.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        ChooseCameraImage();


                    } else {
                        ActivityCompat.requestPermissions(OneToOneChat.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                2);
                    }
                } else {
                    ChooseCameraImage();
                }

                break;


                case R.id.chooseGallery:

                if (Build.VERSION.SDK_INT >= M) {
                    if (OneToOneChat.this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            OneToOneChat.this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            OneToOneChat.this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        chooseGalleryImage();


                    } else {
                        ActivityCompat.requestPermissions(OneToOneChat.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                1);
                    }
                } else {
                    chooseGalleryImage();
                }

                break;

        }


    }

    private void chooseGalleryImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, 2);

    }

    private void ChooseCameraImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File filesDir = OneToOneChat.this.getFilesDir();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());
        String imageFileName = "captured2" + timeStamp + ".jpg";
        File imageFile = new File(filesDir, imageFileName);
        filepath = imageFile.getAbsolutePath();
        Uri photoURI = FileProvider.getUriForFile(OneToOneChat.this,
                OneToOneChat.this.getPackageName() + ".com.sloth.provider",
                imageFile.getAbsoluteFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(OneToOneChat.this.getFilesDir().toString());
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


                    File filesDir = OneToOneChat.this.getFilesDir();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                    String timeStamp = dateFormat.format(new Date());
                    String imageFileName = "from_camera" + timeStamp + ".jpg";
                    File imageFile = new File(filesDir, imageFileName);
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

                    new ImageCompressionAsyncTask(OneToOneChat.this).execute();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



            else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath1 = {MediaStore.Images.Media.DATA};
                Cursor c = OneToOneChat.this.getContentResolver().query(selectedImage, filePath1, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath1[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(picturePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        Bitmap rotatedBitmap = null;
                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(thumbnail, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(thumbnail, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(thumbnail, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = thumbnail;
                        }


                        File filesDir = OneToOneChat.this.getFilesDir();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String timeStamp = dateFormat.format(new Date());
                        String imageFileName = "from_gallery" + timeStamp + ".jpg";
                        File imageFile = new File(filesDir, imageFileName);

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
                        new ImageCompressionAsyncTask(OneToOneChat.this).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(OneToOneChat.this, "Could not load this image", Toast.LENGTH_SHORT).show();
                }
            } else {

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

            File filesDir = OneToOneChat.this.getFilesDir();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            String imageFileName = "from_camera" + timeStamp + ".jpg";
            File imageFile = new File(filesDir, imageFileName);
//            File storageDir = new File(Environment.getExternalStorageDirectory().toString(), "NovaStar Documents");
            String filePathaa = SiliCompressor.with(mContext).compress(filepath,imageFile.getAbsoluteFile());
            return filePathaa;

        }

        @Override
        protected void onPostExecute(String s) {

            File imageFile = new File(s);

            filepath = imageFile.getPath();

            if(NetworkDetector.isNetworkStatusAvialable(OneToOneChat.this))
            {

                sendImage("2");

            }
            else
            {
                Toast.makeText(OneToOneChat.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void sendImage(String type) {


        String uniquefield = String.valueOf(new java.util.Date().getTime());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDateStr = format1.format(new java.util.Date().getTime());

        realm.beginTransaction();
        O2OChat o2OChat = new O2OChat();
        o2OChat.setMessge_type(type);
        o2OChat.setCreated_at(newDateStr);
        o2OChat.setChat_message_id(uniquefield);
        o2OChat.setAndriod_unique_id(uniquefield);
        o2OChat.setDialog_id(iBoxObj.getDialog_id());
        o2OChat.setSender_id(sm.getUserObject().getUser_id());
        o2OChat.setPercent(0);
        o2OChat.setUploading(false);
        o2OChat.setMessage(filepath);
        realm.copyToRealmOrUpdate(o2OChat);
        realm.commitTransaction();
        myAdapter.notifyDataSetChanged();
        sendMessageToReceiver(filepath,uniquefield,type,o2OChat);

    }



    private void sendMessageToReceiver(String message, String uniquefield, String type, final O2OChat o2OChat) {


        if(type.equalsIgnoreCase("2"))
        {
            AndroidNetworking.upload(Webservcie.postChatMessage)
                    .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                    .addMultipartParameter("receiver_id",iBoxObj.getGroup_id())
                    .addMultipartParameter("unique_id",uniquefield)
                    .addMultipartParameter("type",type)
                    .addMultipartParameter("message","")
                    .addMultipartFile("file_path", new File(message))
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long message) {

                            long divisor = message / 100;
                            int progressAmount = (int)(bytesUploaded / divisor);
                            realm.beginTransaction();
                            o2OChat.setPercent(progressAmount);
                            o2OChat.setUploading(true);
                            realm.copyToRealmOrUpdate(o2OChat);
                            realm.commitTransaction();
                            myAdapter.notifyDataSetChanged();

                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {


                            if(response.optString("success").equalsIgnoreCase("1"))
                            {

                                O2OChat one2onechat = realm.where(O2OChat.class).
                                        equalTo("chat_message_id", response.optJSONObject("data")
                                                .optString("andriod_unique_id")).findFirst();
                                if(one2onechat!=null)
                                {
                                    realm.beginTransaction();
                                    one2onechat.setSender_id(response.optJSONObject("data").optString("sender_id"));
                                    one2onechat.setSender_user_name(response.optJSONObject("data").optString("sender_user_name"));
                                    one2onechat.setReceiver_id(response.optJSONObject("data").optString("receiver_id"));
                                    one2onechat.setReceiver_user_name(response.optJSONObject("data").optString("receiver_user_name"));
                                    one2onechat.setDialog_id(response.optJSONObject("data").optString("dialog_id"));
                                    one2onechat.setChat_message_id(response.optJSONObject("data").optString("chat_message_id"));
                                    one2onechat.setMessage(response.optJSONObject("data").optString("message"));
                                    one2onechat.setMessge_type(response.optJSONObject("data").optString("messge_type"));
                                    one2onechat.setAndriod_unique_id(response.optJSONObject("data").optString("andriod_unique_id"));
                                    one2onechat.setCreated_at(response.optJSONObject("data").optString("created_at"));
                                    one2onechat.setUploading(false);
                                    realm.copyToRealmOrUpdate(one2onechat);
                                    realm.commitTransaction();
                                    myAdapter.notifyDataSetChanged();



                                }

                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
        else
        {
            AndroidNetworking.upload(Webservcie.postChatMessage)
                    .addMultipartParameter("user_id",sm.getUserObject().getUser_id())
                    .addMultipartParameter("receiver_id",iBoxObj.getGroup_id())
                    .addMultipartParameter("unique_id",uniquefield)
                    .addMultipartParameter("type",type)
                    .addMultipartParameter("message",message)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {


                            if(response.optString("success").equalsIgnoreCase("1"))
                            {

                                O2OChat one2onechat = realm.where(O2OChat.class).
                                        equalTo("chat_message_id", response.optJSONObject("data")
                                                .optString("andriod_unique_id")).findFirst();
                                if(one2onechat!=null)
                                {
                                    realm.beginTransaction();
                                    one2onechat.setSender_id(response.optJSONObject("data").optString("sender_id"));
                                    one2onechat.setSender_user_name(response.optJSONObject("data").optString("sender_user_name"));
                                    one2onechat.setReceiver_id(response.optJSONObject("data").optString("receiver_id"));
                                    one2onechat.setReceiver_user_name(response.optJSONObject("data").optString("receiver_user_name"));
                                    one2onechat.setDialog_id(response.optJSONObject("data").optString("dialog_id"));
                                    one2onechat.setChat_message_id(response.optJSONObject("data").optString("chat_message_id"));
                                    one2onechat.setMessage(response.optJSONObject("data").optString("message"));
                                    one2onechat.setMessge_type(response.optJSONObject("data").optString("messge_type"));
                                    one2onechat.setAndriod_unique_id(response.optJSONObject("data").optString("andriod_unique_id"));
                                    one2onechat.setCreated_at(response.optJSONObject("data").optString("created_at"));
                                    one2onechat.setUploading(false);
                                    realm.copyToRealmOrUpdate(one2onechat);
                                    realm.commitTransaction();

//                                    chatMessageListAdapter.notifyDataSetChanged();


                                }

                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            System.out.println("&^$%^anError#> "+anError);
                        }
                    });
        }



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
                    chooseGalleryImage();
                }
                return;
            }

            case 2: {


                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    ChooseCameraImage();
                }
                return;
            }
            case 3: {

                if (grantResults.length == 0 || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    Intent intent = new Intent();
                    intent.setType("video/mp4");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
                }
                return;
            }
        }
    }




    public class MyAdapter extends BaseAdapter {

        Context context;
        RealmResults<O2OChat> chatUsersArrayList;
        private int lastIndex = 0;
        private LayoutInflater inflater;

        public MyAdapter(Context context,RealmResults<O2OChat> chatUsersArrayList) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.chatUsersArrayList = chatUsersArrayList;
        }

        @Override
        public int getCount() {
            return chatUsersArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return chatUsersArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.chat_my_design, parent, false);

                viewHolder.incoming_layout_bubble = (FrameLayout) view.findViewById(R.id.incoming_layout_bubble);
                viewHolder.outgoing_layout_bubble = (FrameLayout) view.findViewById(R.id.outgoing_layout_bubble);
                viewHolder.sentimage_mprogressbar = (ProgressBar) view.findViewById(R.id.sentimage_mprogressbar);
                viewHolder.sending_image_mprogressbar = (ProgressBar) view.findViewById(R.id.sending_image_mprogressbar);
                viewHolder.received_progressbar = (ProgressBar) view.findViewById(R.id.received_progressbar);
                viewHolder.sentImage = (ImageView) view.findViewById(R.id.sentImage);
                viewHolder.received_image = (ImageView) view.findViewById(R.id.received_image);
                viewHolder.sent_msg = (NormalTextview) view.findViewById(R.id.sent_msg);
                viewHolder.received_msg = (NormalTextview) view.findViewById(R.id.received_msg);
                viewHolder.received_date = (NormalTextview) view.findViewById(R.id.received_date);
                viewHolder.sent_timing = (TextView) view.findViewById(R.id.sent_timing);
                viewHolder.received_timing = (TextView) view.findViewById(R.id.received_timing);
                viewHolder.mysentTextProgress = (TextView) view.findViewById(R.id.mysentTextProgress);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            final O2OChat chatobject = chatUsersArrayList.get(position);

            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;


            int new_height = (int) (Screenwidth / 2.3);
            int new_height1 = Screenwidth/10;
            new_height1 = new_height1*8;
            new_height = (int) Math.round(new_height*1.5);
            ViewGroup.LayoutParams layoutParams = viewHolder.sentImage.getLayoutParams();
            ViewGroup.LayoutParams layoutParams1 = viewHolder.received_image.getLayoutParams();
            layoutParams.width = new_height1;
            layoutParams1.width = new_height1;
            layoutParams1.height = (int) new_height;
            layoutParams.height = (int) new_height;
            viewHolder.sentImage.setLayoutParams(layoutParams);
            viewHolder.received_image.setLayoutParams(layoutParams1);


            String crtdate = chatUsersArrayList.get(position).getCreated_at();
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");

            Date date2 = null;
            String tempStartDate = "";
            try {
                date2 = format2.parse(crtdate);
                tempStartDate = dateFormat2.format(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date date3 = null;
            String tempPreviousDate = "";
            if(position>0){
                String crtdate11 = chatUsersArrayList.get(position-1).getCreated_at();
                try {
                    date3 = format2.parse(crtdate11);
                    tempPreviousDate = dateFormat2.format(date3);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(tempStartDate.equalsIgnoreCase(tempPreviousDate))
            {
                viewHolder.received_date.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.received_date.setVisibility(View.VISIBLE);
                viewHolder.received_date.setText(tempStartDate);
            }



            if (chatobject.getSender_id().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                viewHolder.incoming_layout_bubble.setVisibility(View.GONE);
                viewHolder.outgoing_layout_bubble.setVisibility(View.VISIBLE);
                viewHolder.sending_image_mprogressbar.setVisibility(View.GONE);
                viewHolder.mysentTextProgress.setVisibility(View.GONE);

                String dtStart = chatobject.getCreated_at();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
                SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat hrs_timeformat = new SimpleDateFormat("HH:mm:ss");

                try {
                    Date date = format.parse(dtStart);
                    String dateTime = timeformat.format(date);
                    viewHolder.sent_timing.setText(dateTime);
                } catch (final ParseException e) {
                    e.printStackTrace();
                }


                if(chatobject.getMessge_type().equalsIgnoreCase("2"))
                {
                    viewHolder.sent_msg.setVisibility(View.GONE);
                    //below method used to show uploading progressbar
                    if(chatobject.isUploading())
                    {
                        viewHolder.mysentTextProgress.setText(String.valueOf(chatobject.getPercent()));
                        viewHolder.sending_image_mprogressbar.setVisibility(View.VISIBLE);
                        viewHolder.sending_image_mprogressbar.setProgress(chatobject.getPercent());
                        viewHolder.mysentTextProgress.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viewHolder.sending_image_mprogressbar.setVisibility(View.GONE);
                        viewHolder.mysentTextProgress.setVisibility(View.GONE);
                    }

                    viewHolder.sentImage.setVisibility(View.VISIBLE);
                    viewHolder.sentimage_mprogressbar.setVisibility(View.VISIBLE);

                    if (chatobject.getMessage().contains("/data/user/0/com.edggi/files/"))
                    {
                        File file = new File(chatobject.getMessage());
                        Picasso.with(getApplicationContext())
                                .load(file)
                                .into(viewHolder.sentImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        viewHolder.sentimage_mprogressbar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        viewHolder.sentimage_mprogressbar.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else
                    {
                        Picasso.with(getApplicationContext())
                                .load(chatobject.getMessage())
//                                .resize(400,400)
                                .into(viewHolder.sentImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        viewHolder.sentimage_mprogressbar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        viewHolder.sentimage_mprogressbar.setVisibility(View.GONE);
                                    }
                                });

                    }

                }
                else
                {


                    viewHolder.sent_msg.setText(StringEscapeUtils.unescapeJava(chatobject.getMessage()));
                    viewHolder.sent_msg.setVisibility(View.VISIBLE);
                    viewHolder.sentImage.setVisibility(View.GONE);
                    viewHolder.sentimage_mprogressbar.setVisibility(View.GONE);
                    viewHolder.mysentTextProgress.setVisibility(View.GONE);

                }

            } else {



                viewHolder.incoming_layout_bubble.setVisibility(View.VISIBLE);
                viewHolder.outgoing_layout_bubble.setVisibility(View.GONE);
                String dtStart = chatobject.getCreated_at();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");

                try {
                    Date date = format.parse(dtStart);
                    String dateTime = timeformat.format(date);
                    viewHolder.received_timing.setText(dateTime);
                } catch (final ParseException e) {
                    e.printStackTrace();
                }
//
                if(chatobject.getMessge_type().equalsIgnoreCase("2"))
                {
                    viewHolder.received_msg.setVisibility(View.GONE);
                    viewHolder.received_image.setVisibility(View.VISIBLE);
                    viewHolder.received_progressbar.setVisibility(View.VISIBLE);

                    Picasso.with(getApplicationContext())
                            .load(chatobject.getMessage())
//                            .resize(400,400)
                            .into(viewHolder.received_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.received_progressbar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    viewHolder.received_progressbar.setVisibility(View.GONE);
                                }
                            });

                }

                else {
                    viewHolder.received_msg.setText(StringEscapeUtils.unescapeJava(chatobject.getMessage()));
                    viewHolder.received_msg.setVisibility(View.VISIBLE);
                    viewHolder.received_image.setVisibility(View.GONE);
                    viewHolder.received_progressbar.setVisibility(View.GONE);
                }

            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (chatobject.getMessge_type().equalsIgnoreCase("2"))
                    {
                        Intent intent = new Intent(OneToOneChat.this, Full_ImageViewActivity.class);
                        intent.putExtra("url",chatobject.getMessage());
                        startActivity(intent);
                    }

                }
            });
            view.setTag(viewHolder);

            return view;
        }

        class ViewHolder {
            TextView   sent_timing, received_timing,mysentTextProgress;
            ImageView sentImage, received_image;
            ProgressBar sentimage_mprogressbar, received_progressbar, sending_image_mprogressbar;
            FrameLayout incoming_layout_bubble, outgoing_layout_bubble;
            NormalTextview sent_msg, received_msg,received_date;
        }

    }




}

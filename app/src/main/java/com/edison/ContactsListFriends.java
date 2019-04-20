package com.edison;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.Contacts;
import com.edison.Object.MyFriendsList;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;

public class ContactsListFriends extends AppCompatActivity {

    SessionManager sm;
    FontManager fm;
    ListView frd_list;
    ImageView back;

    int plus_sign_pos = 0;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ArrayList<String> selectUsers;
    ArrayList<String> jsonlist;
    ArrayList<Contacts> contactsArrayList;
    Cursor phones, email;
    MyContactsFrdAdapter myContactsFrdAdapter;

    ACProgressFlower acProgressFlower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list_friends);


        INIT();

    }

    private void INIT()
    {

        sm = new SessionManager(this);
        fm = new FontManager(this);
        frd_list = (ListView) findViewById(R.id.frd_list);
        back = (ImageView) findViewById(R.id.back);
        selectUsers = new ArrayList<>();
        jsonlist = new ArrayList<>();
        contactsArrayList = new ArrayList<>();
        if (NetworkDetector.isNetworkStatusAvialable(ContactsListFriends.this)) {

            showContacts();

        }
        else {
            Toast.makeText(ContactsListFriends.this,
                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void showContacts() {

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            new LoadContact().execute();


        }

    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ContactsListFriends.this, "No contacts in your contact list.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                    selectUsers.add(phoneNumber);
                    Contacts contacts = new Contacts();
                    contacts.setName(name);
                    contacts.setNumber(phoneNumber);
                    contactsArrayList.add(contacts);

                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            for (Contacts user : contactsArrayList) {

                String without_zero = user.getNumber();

                if (without_zero != null) {
                    if (hasCountryCode(without_zero)) {
                        // +52 for MEX +526441122345, 13-10 = 3, so we need to remove 3 characters
                        try {
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber numberProto = null;
                            numberProto = phoneUtil.parse(without_zero, "");
                            int countryCode = numberProto.getCountryCode();
                            long nationalNumber = numberProto.getNationalNumber();

                            String filterNum = String.valueOf(nationalNumber);
                            filterNum = filterNum.replaceAll("[^A-Za-z0-9]", "");

                            jsonlist.add(filterNum);
                            user.setNumber(filterNum);

                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        String filterNum = without_zero.trim();

                        filterNum = filterNum.replaceAll("[^A-Za-z0-9]", "");

                        jsonlist.add(filterNum);
                        user.setNumber(filterNum);
                    }
                }

            }


            HashSet<Contacts> hashSet = new HashSet<Contacts>();
            hashSet.addAll(contactsArrayList);
            contactsArrayList.clear();
            contactsArrayList.addAll(hashSet);

            System.out.println(">>wqe>> "+jsonlist);
            System.out.println(">qwe>>> "+jsonlist.size());


            if (NetworkDetector.isNetworkStatusAvialable(ContactsListFriends.this))
            {

                Load_Contact_Service();

            }
            else
            {
                Toast.makeText(ContactsListFriends.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private boolean hasCountryCode(String number) {

        return number.charAt(plus_sign_pos) == '+'; // Didn't String had contains() method?...
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();

            } else {
                Toast.makeText(ContactsListFriends.this, "Until you grant the permission, we cannot display the contacts suggesion list ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Load_Contact_Service() {

        acProgressFlower = new ACProgressFlower.Builder(ContactsListFriends.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("invite_friends", Send_contact());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.contact_list_update)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        acProgressFlower.dismiss();
                        if(response.optString("success").equalsIgnoreCase("1"))
                        {
                            JSONArray data = response.optJSONArray("user");
                            if(data.length()>0)
                            {
                                ArrayList<MyFriendsList> contact_friends_objectArrayList = new ArrayList<>();
                                MyFriendsList contact_friends_object;
                                for (int i = 0; i < data.length(); i++) {

                                    contact_friends_object = new MyFriendsList();
                                    contact_friends_object.setDesignation(data.optJSONObject(i).optString("designation"));
                                    contact_friends_object.setName(data.optJSONObject(i).optString("name"));
                                    contact_friends_object.setUser_id(data.optJSONObject(i).optString("user_id"));
                                    contact_friends_object.setUser_is_live(data.optJSONObject(i).optString("user_is_live"));
                                    contact_friends_object.setProfile_image(data.optJSONObject(i).optString("profile_image"));
                                    contact_friends_object.setUser_company(data.optJSONObject(i).optString("user_company"));
                                    contact_friends_object.setDialog_id(data.optJSONObject(i).optString("dialog_id"));
                                    contact_friends_object.setFriend_status(data.optJSONObject(i).optString("friend_status"));
                                    contact_friends_objectArrayList.add(contact_friends_object);
                                }

                                myContactsFrdAdapter = new MyContactsFrdAdapter(ContactsListFriends.this, contact_friends_objectArrayList);
                                frd_list.setAdapter(myContactsFrdAdapter);

                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();
                    }
                });
    }

    public class MyContactsFrdAdapter extends BaseAdapter
    {
        ArrayList<MyFriendsList> myFrdObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public MyContactsFrdAdapter(Context context,ArrayList<MyFriendsList> suggestListObjectArrayList)
        {
            this.context = context;
            this.myFrdObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return myFrdObjectArrayList.size();
        }
        public void RefreshData(int position,int diff)
        {
            if(diff==1)
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("1");
                notifyDataSetChanged();
            }
            else if(diff==2)
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("2");
                notifyDataSetChanged();
            }
            else
            {
                this.myFrdObjectArrayList.get(position).setFriend_status("0");
                notifyDataSetChanged();
            }



        }

        @Override
        public Object getItem(int size)
        {
            return myFrdObjectArrayList.get(size);
        }
        @Override
        public long getItemId(int size)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup)
        {

            View view = convertview;
            final ViewHolder viewHolder;
            if(view==null)
            {
                view = inflater.inflate(R.layout.suggestlistdesginnew,viewGroup,false);
                viewHolder = new ViewHolder();

                viewHolder.request_designation = (NormalTextview) view.findViewById(R.id.request_designation);
                viewHolder.ttl_connection = (NormalTextview) view.findViewById(R.id.ttl_connection);
                viewHolder.ttl_views = (NormalTextview) view.findViewById(R.id.ttl_views);
                viewHolder.ttl_post = (NormalTextview) view.findViewById(R.id.ttl_post);
                viewHolder.request_name = (BoldTextview) view.findViewById(R.id.request_name);
                viewHolder.follow = (BoldTextview) view.findViewById(R.id.follow);
                viewHolder.ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
                viewHolder.ll_coutn = (LinearLayout) view.findViewById(R.id.ll_coutn);
                viewHolder.request_img = (CircleImageView) view.findViewById(R.id.request_img);
                viewHolder.mprogress1 = (ProgressBar) view.findViewById(R.id.request_progress);
            }
            else
            {
                viewHolder = (ViewHolder) view.getTag();
            }


            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;


            int new_height = Screenwidth/10;
            new_height = (int) Math.round(new_height*1.5);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.request_img.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.request_img.setLayoutParams(layoutParams);
            viewHolder.ll_coutn.setVisibility(View.GONE);

            final MyFriendsList suggestListObject = myFrdObjectArrayList.get(position);
            viewHolder.request_name.setText(suggestListObject.getName());
            viewHolder.request_designation.setText(suggestListObject.getDesignation());



            viewHolder.mprogress1.setVisibility(VISIBLE);
            /////picasso
            Picasso.with(ContactsListFriends.this).load(suggestListObject.getProfile_image())
                    .placeholder(R.drawable.profile_img)
//                  .transform(new BlurTransformation(Global_feed_Activity.this, 25, 3))
                    .into(viewHolder.request_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.mprogress1.setVisibility(View.GONE);

                        }
                        @Override
                        public void onError() {
                            viewHolder.mprogress1.setVisibility(View.GONE);
                        }
                    });




            if(suggestListObject.getFriend_status().equalsIgnoreCase("0"))
            {
                viewHolder.follow.setText("Connect");
                viewHolder.follow.setVisibility(View.VISIBLE);
                viewHolder.ll_bg.setVisibility(View.VISIBLE);
                viewHolder.follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
            }
            else if(suggestListObject.getFriend_status().equalsIgnoreCase("2"))
            {
                viewHolder.follow.setText("Invitation sent");
                viewHolder.follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                viewHolder.follow.setVisibility(View.VISIBLE);
                viewHolder.ll_bg.setVisibility(View.VISIBLE);
            }
            else if(suggestListObject.getFriend_status().equalsIgnoreCase("3"))
            {
                viewHolder.follow.setText("Accept");
                viewHolder.follow.setBackground(getResources().getDrawable(R.drawable.nearbyuser_btn));
                viewHolder.follow.setVisibility(View.VISIBLE);
                viewHolder.ll_bg.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.follow.setVisibility(View.GONE);
                viewHolder.ll_bg.setVisibility(View.GONE);

            }




            viewHolder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (NetworkDetector.isNetworkStatusAvialable(ContactsListFriends.this))
                    {
                        if(suggestListObject.getFriend_status().equalsIgnoreCase("0"))
                        {
                            sendRequest(suggestListObject.getUser_id(),"1 ",position);
                        }
                        else if(suggestListObject.getFriend_status().equalsIgnoreCase("2"))
                        {

                            new android.support.v7.app.AlertDialog.Builder(ContactsListFriends.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Are you sure want to withdraw invitation ?")
                                    .setPositiveButton("withdraw", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            cancelreq(suggestListObject.getUser_id(),"3",position);
                                        }

                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                        else if(suggestListObject.getFriend_status().equalsIgnoreCase("3"))
                        {
                            Accept_Or_Reject(suggestListObject.getUser_id(),"2",position,"2");
                        }



                    } else {
                        Toast.makeText(ContactsListFriends.this, "Please check your Internet Connection",
                                Toast.LENGTH_SHORT).show();
                    }





                }
            });




            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ContactsListFriends.this,OtherUserProfileActivity.class);
                    intent.putExtra("id",suggestListObject.getUser_id());
                    intent.putExtra("name",suggestListObject.getName());
                    startActivity(intent);
                }
            });

            view.setTag(viewHolder);
            return view;
        }

        class ViewHolder
        {
            CircleImageView request_img;
            ProgressBar mprogress1;
            BoldTextview follow,request_name;
            LinearLayout ll_bg,ll_coutn;
            NormalTextview request_designation,ttl_post,ttl_views,ttl_connection;
        }



    }
    private void cancelreq(String user_id, String status, final int pos) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",user_id);
            jsonObject.accumulate("type", status);
            jsonObject.accumulate("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(ContactsListFriends.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.request_send)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                myContactsFrdAdapter.RefreshData(pos,0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();

                    }
                });


    }



    private void sendRequest(String user_id, String s, final int position) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("following_id",user_id);
            jsonObject.accumulate("type", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ACProgressFlower acProgressFlower = new ACProgressFlower.Builder(ContactsListFriends.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.request_send)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                myContactsFrdAdapter.RefreshData(position,2);
                                Toast.makeText(ContactsListFriends.this, "Request has been sent", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();

                    }
                });


    }


    private void Accept_Or_Reject(String user_id, String s, final int position, final String accept) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("follower_id",user_id);
            jsonObject.accumulate("status", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        acProgressFlower = new ACProgressFlower.Builder(ContactsListFriends.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.show();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.request_response)
                .addJSONObjectBody(jsonObject)
                .setTag("login")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(final String response) {
                        acProgressFlower.dismiss();


                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            if(jsonObject1.optString("success").equalsIgnoreCase("1"))
                            {
                                if(accept.equalsIgnoreCase("2"))
                                {
                                    myContactsFrdAdapter.RefreshData(position,1);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        acProgressFlower.dismiss();

                    }
                });


    }
    private Object Send_contact() {

        JSONArray ret = new JSONArray();

        try {



            for (Contacts userr : contactsArrayList) {


                JSONObject JO = new JSONObject();
                JO.accumulate("contact_no", userr.getNumber());
                JO.accumulate("name", userr.getName());
                ret.put((JO));
            }

        } catch (Exception e) {

        }

        System.out.println(">>> "+ret);


        return ret;

    }


}

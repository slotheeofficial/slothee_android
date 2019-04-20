package com.edison;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.Object.RequestListObj;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class ManageInvitation extends AppCompatActivity {

    ImageView backicon1;
    FontManager fm;
    SessionManager sm;
    ListView req_receiced_list;
    ACProgressFlower acProgressFlower;
    BoldTextview nodata;
    SuggestListAdapter suggestListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_invitation);
        
        INIT();
        
    }

    private void INIT() {

        fm = new FontManager(ManageInvitation.this);
        sm = new SessionManager(ManageInvitation.this);
        req_receiced_list = (ListView) findViewById(R.id.req_receiced_list);
        nodata = (BoldTextview) findViewById(R.id.nodata);


        backicon1 = (ImageView) findViewById(R.id.backicon1);
        backicon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (NetworkDetector.isNetworkStatusAvialable(ManageInvitation.this)) {
            getFriendReqList();
        } else {
            Toast.makeText(ManageInvitation.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }


    private void getFriendReqList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        acProgressFlower = new ACProgressFlower.Builder(ManageInvitation.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower.dismiss();
        acProgressFlower.setCancelable(false);

        AndroidNetworking.post(Webservcie.request_list)
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
                                final JSONArray user = jsonObject1.optJSONArray("user");

                                if(user.length()>0)
                                {
                                    ArrayList<RequestListObj> suggestListArrayList = new ArrayList<>();
                                    RequestListObj suggestList = null;
                                    for (int i=0; i<user.length();i++)
                                    {
                                        suggestList = new RequestListObj();
                                        suggestList.setName(user.optJSONObject(i).optString("name"));
                                        suggestList.setUser_id(user.optJSONObject(i).optString("user_id"));
                                        suggestList.setProfile_image(user.optJSONObject(i).optString("profile_image"));
                                        suggestList.setDesignation(user.optJSONObject(i).optString("designation"));

                                        suggestListArrayList.add(suggestList);
                                    }

                                    suggestListAdapter = new SuggestListAdapter(ManageInvitation.this,suggestListArrayList);
                                    req_receiced_list.setAdapter(suggestListAdapter);
                                    nodata.setVisibility(View.GONE);
                                    req_receiced_list.setVisibility(View.VISIBLE);
                                }


                            }
                            else
                            {
                                nodata.setVisibility(View.VISIBLE);
                                req_receiced_list.setVisibility(View.GONE);
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

    public class SuggestListAdapter extends BaseAdapter
    {
        ArrayList<RequestListObj> suggestListObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public SuggestListAdapter(Context context,ArrayList<RequestListObj> suggestListObjectArrayList)
        {
            this.context = context;
            this.suggestListObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            return suggestListObjectArrayList.size();
        }
        public void RefreshData(int pos)
        {
            suggestListObjectArrayList.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int size)
        {
            return suggestListObjectArrayList.get(size);
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
                view = inflater.inflate(R.layout.list_view_row,viewGroup,false);
                viewHolder = new ViewHolder();
                viewHolder.accept = (NormalTextview) view.findViewById(R.id.accept);
                viewHolder.reject = (NormalTextview) view.findViewById(R.id.reject);
                viewHolder.request_name = (NormalTextview) view.findViewById(R.id.username);
                viewHolder.user_imagee = (CircleImageView) view.findViewById(R.id.user_imagee);
                viewHolder.mprogress = (ProgressBar) view.findViewById(R.id.mprogress);
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
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.user_imagee.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.user_imagee.setLayoutParams(layoutParams);

            final RequestListObj suggestListObject = suggestListObjectArrayList.get(position);

            String sourceString = "<b> <font color='black'>" + suggestListObject.getName().toUpperCase()+"</font></b> " + "sent you a request ";
            viewHolder.request_name.setText(Html.fromHtml(sourceString));

            viewHolder.mprogress.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(suggestListObject.getProfile_image())
                    .placeholder(R.drawable.profile_img)
                    .into(viewHolder.user_imagee, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.mprogress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            viewHolder.mprogress.setVisibility(View.GONE);
                        }
                    });

            viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (NetworkDetector.isNetworkStatusAvialable(ManageInvitation.this)) {
                        Accept_Or_Reject(suggestListObject.getUser_id(),"2",position);

                    } else {
                        Toast.makeText(ManageInvitation.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (NetworkDetector.isNetworkStatusAvialable(ManageInvitation.this)) {
                        Accept_Or_Reject(suggestListObject.getUser_id(),"3",position);

                    } else {
                        Toast.makeText(ManageInvitation.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ManageInvitation.this, OtherUserProfileActivity.class);
                    intent.putExtra("id",suggestListObject.getUser_id());
                    intent.putExtra("name",suggestListObject.getName());
                    startActivity(intent);
                }
            });
//            viewHolder.user_imagee.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(ManageInvitation.this, ManageInvitation.class);
//                    intent.putExtra("id",suggestListObject.getUser_id());
//                    intent.putExtra("name",suggestListObject.getName());
//                    startActivity(intent);
//                }
//            });


            view.setTag(viewHolder);
            return view;
        }

        class ViewHolder
        {
            CircleImageView user_imagee;
            ProgressBar mprogress;
            NormalTextview request_name,accept,reject;
        }



    }

    private void Accept_Or_Reject(String user_id, String s, final int position) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("follower_id",user_id);
            jsonObject.accumulate("status", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        acProgressFlower = new ACProgressFlower.Builder(ManageInvitation.this)
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
                                suggestListAdapter.RefreshData(position);
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
}

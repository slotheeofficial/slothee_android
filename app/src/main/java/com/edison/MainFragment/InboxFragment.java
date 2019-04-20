package com.edison.MainFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.edison.Customfonts.BoldTextview;
import com.edison.Customfonts.NormalTextview;
import com.edison.MainActivity;
import com.edison.MyConnections;
import com.edison.Object.IBoxObj;
import com.edison.Object.TempIBobjt;
import com.edison.OneToOneChat;
import com.edison.R;
import com.edison.Utils.FontManager;
import com.edison.Utils.NetworkDetector;
import com.edison.Utils.SessionManager;
import com.edison.Utils.Webservcie;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.WINDOW_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    View view;
    InboxListAdapter inboxlistadapter;
    FontManager fm;
    ACProgressFlower acProgressFlower;
    Realm realm;
    SessionManager sm;
    ListView inboxlist;
    LinearLayout ll_nodata;
    FloatingActionButton create_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inbox, container, false);

        Init();

        return view;
    }

    @Override
    public void onResume() {
        Init();
        super.onResume();
    }


    private void Init() {

        realm = Realm.getDefaultInstance();
        sm = new SessionManager(getContext());
        fm = new FontManager(getContext());
        inboxlist = (ListView) view.findViewById(R.id.suggestlist);
        ll_nodata = (LinearLayout) view.findViewById(R.id.ll_nodata);
        create_message = (FloatingActionButton) view.findViewById(R.id.create_message);

        RealmResults<IBoxObj> iBoxObjRealmResults = realm.where(IBoxObj.class).findAll();
        inboxlistadapter = new InboxListAdapter(getActivity(), iBoxObjRealmResults);
        inboxlist.setAdapter(inboxlistadapter);

        if(NetworkDetector.isNetworkStatusAvialable(getActivity()))
        {
            getInboxList();
        }
        else
        {
            Toast.makeText(getActivity(), "Check your Network connection", Toast.LENGTH_SHORT).show();
        }

        create_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MyConnections.class);
                startActivity(intent);

            }
        });

    }

    private void getInboxList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", sm.getUserObject().getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Webservcie.chat_inbox)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {


                        final JSONArray items = response.optJSONArray("data");

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    realm.clear(IBoxObj.class);
                                    realm.createOrUpdateAllFromJson(IBoxObj.class, items);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    realm.close();
                                }

                                RealmResults<IBoxObj> iBoxObjRealmResults = realm.where(IBoxObj.class).findAll();
                                if (iBoxObjRealmResults.size() > 0) {
                                    ll_nodata.setVisibility(View.GONE);
                                    inboxlist.setVisibility(View.VISIBLE);
                                    inboxlistadapter.notifyDataSetChanged();
                                } else {
                                    ll_nodata.setVisibility(View.VISIBLE);
                                    inboxlist.setVisibility(View.GONE);
                                }

                            }
                        });

                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });

    }

    public class InboxListAdapter extends BaseAdapter {
        RealmResults<IBoxObj> myFrdObjectArrayList;
        Context context;
        LayoutInflater inflater;

        public InboxListAdapter(Context context, RealmResults<IBoxObj> suggestListObjectArrayList) {
            this.context = context;
            this.myFrdObjectArrayList = suggestListObjectArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myFrdObjectArrayList.size();
        }

        public void RefreshData(int pos) {
            myFrdObjectArrayList.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int size) {
            return myFrdObjectArrayList.get(size);
        }

        @Override
        public long getItemId(int size) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {

            View view = convertview;
            final ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.inboxlist_design, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.onlinestatus = (BoldTextview) view.findViewById(R.id.onlinestatus);
                viewHolder.request_name = (BoldTextview) view.findViewById(R.id.request_name);
                viewHolder.request_time = (BoldTextview) view.findViewById(R.id.request_time);
                viewHolder.last_msg_username = (NormalTextview) view.findViewById(R.id.last_msg_username);
                viewHolder.last_msg_imageicon = (NormalTextview) view.findViewById(R.id.last_msg_imageicon);
                viewHolder.unread_msg = (TextView) view.findViewById(R.id.unread_msg);
                viewHolder.request_img = (CircleImageView) view.findViewById(R.id.request_img);
                viewHolder.request_progress = (ProgressBar) view.findViewById(R.id.request_progress);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int Screenwidth = displayMetrics.widthPixels;


            int new_height = Screenwidth / 10;
            new_height = (int) Math.round(new_height * 1.6);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.request_img.getLayoutParams();
            layoutParams.width = new_height;
            layoutParams.height = (int) new_height;
            viewHolder.request_img.setLayoutParams(layoutParams);

            final IBoxObj inbox = myFrdObjectArrayList.get(position);
            viewHolder.request_name.setText(StringEscapeUtils.unescapeJava(inbox.getGroup_name()));

//            System.out.println(">W#EWE#$ " + inbox.getUnread_count());

            if (inbox.getUnread_count() > 0) {
                viewHolder.unread_msg.setVisibility(View.VISIBLE);
                viewHolder.unread_msg.setText(String.valueOf(inbox.getUnread_count()));
            } else {
                viewHolder.unread_msg.setVisibility(View.GONE);
            }

            if (inbox.getLast_message() != null && inbox.getLast_message().length() > 0) {

                if (inbox.getLast_messge_type().equalsIgnoreCase("1")) {
                    viewHolder.last_msg_imageicon.setVisibility(View.GONE);
                    if (inbox.getLast_message_user_id().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                        viewHolder.last_msg_username.setText("You : " + StringEscapeUtils.unescapeJava(inbox.getLast_message()));

                    } else {
                        viewHolder.last_msg_username.setText(inbox.getLast_message_user_name() +
                                " : " + StringEscapeUtils.unescapeJava(inbox.getLast_message()));
                    }
                } else if (inbox.getLast_messge_type().equalsIgnoreCase("2")) {

                    viewHolder.last_msg_imageicon.setVisibility(View.VISIBLE);
                    fm.setImage_Icon(viewHolder.last_msg_imageicon);
                    if (inbox.getLast_message_user_id().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                        viewHolder.last_msg_username.setText("You ");
                    } else {
                        viewHolder.last_msg_username.setText(inbox.getLast_message_user_name() +
                                " ");
                    }
                } else if (inbox.getLast_messge_type().equalsIgnoreCase("4")) {
                    viewHolder.last_msg_imageicon.setVisibility(View.VISIBLE);
                    fm.setVideoicon(viewHolder.last_msg_imageicon);
                    if (inbox.getLast_message_user_id().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                        viewHolder.last_msg_username.setText("You ");

                    } else {
                        viewHolder.last_msg_username.setText(inbox.getLast_message_user_name() +
                                " ");
                    }
                } else if (inbox.getLast_messge_type().equalsIgnoreCase("3")) {

                    viewHolder.last_msg_imageicon.setVisibility(View.VISIBLE);
                    fm.setAudioicon(viewHolder.last_msg_imageicon);
                    if (inbox.getLast_message_user_id().equalsIgnoreCase(sm.getUserObject().getUser_id())) {
                        viewHolder.last_msg_username.setText("You ");
                    } else {
                        viewHolder.last_msg_username.setText(inbox.getLast_message_user_name() +
                                " ");
                    }
                } else {
                    viewHolder.last_msg_imageicon.setVisibility(View.GONE);
                    viewHolder.last_msg_username.setVisibility(View.GONE);
                }

            } else {
                viewHolder.last_msg_username.setText("");
            }

            viewHolder.request_progress.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(inbox.getGroup_image())
                    .placeholder(R.drawable.profile_img)
                    .into(viewHolder.request_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.request_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            viewHolder.request_progress.setVisibility(View.GONE);
                        }
                    });

            viewHolder.request_progress.setVisibility(View.GONE);




            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (inbox.getUnread_count() > 0) {
                        if (NetworkDetector.isNetworkStatusAvialable(getActivity())) {
                            UpdateReadStatus("", inbox.getGroup_id(), "1");
                        } else {
                            Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                    TempIBobjt tempIBobjt = new TempIBobjt();
                    tempIBobjt.setDialog_id(inbox.getDialog_id());
                    tempIBobjt.setGroup_id(inbox.getGroup_id());
                    tempIBobjt.setGroup_image(inbox.getGroup_image());
                    tempIBobjt.setGroup_name(inbox.getGroup_name());
                    tempIBobjt.setQuickblox_id(inbox.getQuickblox_id());

                    Intent intent = new Intent(getActivity(), OneToOneChat.class);
                    intent.putExtra("qbchatdialog", new Gson().toJson(tempIBobjt));
                    startActivity(intent);

                }
            });

            view.setTag(viewHolder);
            return view;
        }

        class ViewHolder {
            CircleImageView request_img;
            ProgressBar request_progress;
            TextView unread_msg;
            NormalTextview last_msg_username, last_msg_imageicon;
            BoldTextview request_name, request_time, onlinestatus;
        }


    }


    public void UpdateReadStatus(String message_id, String receiver_id, String type) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("message_id", message_id);
            jsonObject.accumulate("sender_id", sm.getUserObject().getUser_id());
            jsonObject.accumulate("receiver_id", receiver_id);
            jsonObject.accumulate("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AndroidNetworking.post(Webservcie.update_read_unread_status)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            if(response.optString("success").equalsIgnoreCase("1"))
                            {
                                MainActivity activity = (MainActivity) getActivity();
                                activity.getFriendReqList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


}

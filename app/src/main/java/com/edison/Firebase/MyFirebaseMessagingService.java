package com.edison.Firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.edison.EventBus.EventMessage;
import com.edison.MainActivity;
import com.edison.Object.O2OChat;
import com.edison.Object.TempIBobjt;
import com.edison.OneToOneChat;
import com.edison.OtherUserProfileActivity;
import com.edison.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * Created by Vengat G on 1/2/2019.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        JSONObject jsonObject=new JSONObject(remoteMessage.getData());

        System.out.println(">>+ "+jsonObject);


        Intent myIntent = new Intent("FBR-IMAGE");
        this.sendBroadcast(myIntent);



        if(jsonObject.optString("type").equalsIgnoreCase("1"))
        {
            sendNotification(StringEscapeUtils.unescapeJava(jsonObject.optString("message")),jsonObject);
            if(jsonObject.optString("chat_type").equalsIgnoreCase("1"))
            {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                O2OChat o2OChat = new O2OChat();
                o2OChat.setSender_id(jsonObject.optString("sender_id"));
                o2OChat.setMessge_type(jsonObject.optString("messge_type"));
                o2OChat.setAndriod_unique_id(jsonObject.optString("andriod_unique_id"));
                o2OChat.setSender_user_name(jsonObject.optString("sender_user_name"));
                o2OChat.setMessage(jsonObject.optString("message"));
                o2OChat.setChat_message_id(jsonObject.optString("chat_message_id"));
                o2OChat.setDialog_id(jsonObject.optString("dialog_id"));
                o2OChat.setCreated_at(jsonObject.optString("created_at"));
                realm.copyToRealmOrUpdate(o2OChat);
                realm.commitTransaction();

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new EventMessage("chat121"));
            }



        }
        else
        {
            sendNotification(jsonObject.optString("body"),jsonObject);
        }
    }


    @SuppressLint("WrongConstant")
    private void sendNotification(String messageBody, JSONObject jsonObject) {

        String CHANNEL_ID = String.valueOf(R.string.notification_channel_id);
        CharSequence name = "Test";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        Intent intent = new Intent();
        if(jsonObject.optString("type").equalsIgnoreCase("1"))
        {
            TempIBobjt tempIBobjt = new TempIBobjt();
            tempIBobjt.setDialog_id(jsonObject.optString("dialog_id"));
            tempIBobjt.setGroup_id(jsonObject.optString("sender_id"));
            tempIBobjt.setGroup_image("");
            tempIBobjt.setGroup_name(jsonObject.optString("sender_user_name"));
            tempIBobjt.setQuickblox_id("");

            intent = new Intent(this, OneToOneChat.class);
            intent.putExtra("qbchatdialog",new Gson().toJson(tempIBobjt));

        }
        else
        {
            intent = new Intent(this, MainActivity.class);
        }




        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("New Message Received")
                .setContentText(jsonObject.optString("sender_user_name")+" : "+StringEscapeUtils.unescapeJava(jsonObject.optString("message")))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, notificationBuilder.build());



    }
}

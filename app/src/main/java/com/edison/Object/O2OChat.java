package com.edison.Object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vengat G on 1/10/2019.
 */

public class O2OChat extends RealmObject {

    @PrimaryKey
    private String chat_message_id;

    private String sender_id, sender_user_name,
            receiver_id, receiver_user_name,
            dialog_id, message,
            messge_type, andriod_unique_id,
            created_at, preview_image;

    private int percent;
    private String crt_datee;
    private String previous_datee;

    private boolean isUploading = false;


    public String getChat_message_id() {
        return chat_message_id;
    }

    public void setChat_message_id(String chat_message_id) {
        this.chat_message_id = chat_message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_user_name() {
        return sender_user_name;
    }

    public void setSender_user_name(String sender_user_name) {
        this.sender_user_name = sender_user_name;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_user_name() {
        return receiver_user_name;
    }

    public void setReceiver_user_name(String receiver_user_name) {
        this.receiver_user_name = receiver_user_name;
    }

    public String getDialog_id() {
        return dialog_id;
    }

    public void setDialog_id(String dialog_id) {
        this.dialog_id = dialog_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessge_type() {
        return messge_type;
    }

    public void setMessge_type(String messge_type) {
        this.messge_type = messge_type;
    }

    public String getAndriod_unique_id() {
        return andriod_unique_id;
    }

    public void setAndriod_unique_id(String andriod_unique_id) {
        this.andriod_unique_id = andriod_unique_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPreview_image() {
        return preview_image;
    }

    public void setPreview_image(String preview_image) {
        this.preview_image = preview_image;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getCrt_datee() {
        return crt_datee;
    }

    public void setCrt_datee(String crt_datee) {
        this.crt_datee = crt_datee;
    }

    public String getPrevious_datee() {
        return previous_datee;
    }

    public void setPrevious_datee(String previous_datee) {
        this.previous_datee = previous_datee;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }
}

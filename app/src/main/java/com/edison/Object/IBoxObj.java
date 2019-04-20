package com.edison.Object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vengat G on 1/4/2019.
 */

public class IBoxObj extends RealmObject {

    @PrimaryKey
    private String dialog_id;

    private String type,group_name,group_id,group_image,last_message_id,last_message,last_messge_type,
            last_message_user_id,last_message_user_name,andriod_unique_id,created_at,quickblox_id;

    private int unread_count;

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    public String getDialog_id() {
        return dialog_id;
    }

    public void setDialog_id(String dialog_id) {
        this.dialog_id = dialog_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_messge_type() {
        return last_messge_type;
    }

    public void setLast_messge_type(String last_messge_type) {
        this.last_messge_type = last_messge_type;
    }

    public String getLast_message_user_id() {
        return last_message_user_id;
    }

    public void setLast_message_user_id(String last_message_user_id) {
        this.last_message_user_id = last_message_user_id;
    }

    public String getLast_message_user_name() {
        return last_message_user_name;
    }

    public void setLast_message_user_name(String last_message_user_name) {
        this.last_message_user_name = last_message_user_name;
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

    public String getQuickblox_id() {
        return quickblox_id;
    }

    public void setQuickblox_id(String quickblox_id) {
        this.quickblox_id = quickblox_id;
    }


}

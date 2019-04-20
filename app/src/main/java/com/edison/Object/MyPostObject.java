package com.edison.Object;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vengat G on 1/9/2019.
 */

public class MyPostObject extends RealmObject{

    @PrimaryKey
    private String post_pk;

    private int friend_status;
    private String views,post_user_fk,message,type,date,post_user,post_userimage,post_designation,link;
    private RealmList<File_path> file_path;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public int getFriend_status() {
        return friend_status;
    }

    public void setFriend_status(int friend_status) {
        this.friend_status = friend_status;
    }

    public String getPost_pk() {
        return post_pk;
    }

    public void setPost_pk(String post_pk) {
        this.post_pk = post_pk;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getPost_user_fk() {
        return post_user_fk;
    }

    public void setPost_user_fk(String post_user_fk) {
        this.post_user_fk = post_user_fk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<File_path> getFile_path() {
        return file_path;
    }

    public void setFile_path(RealmList<File_path> file_path) {
        this.file_path = file_path;
    }


    public String getPost_user() {
        return post_user;
    }

    public void setPost_user(String post_user) {
        this.post_user = post_user;
    }

    public String getPost_userimage() {
        return post_userimage;
    }

    public void setPost_userimage(String post_userimage) {
        this.post_userimage = post_userimage;
    }

    public String getPost_designation() {
        return post_designation;
    }

    public void setPost_designation(String post_designation) {
        this.post_designation = post_designation;
    }
}

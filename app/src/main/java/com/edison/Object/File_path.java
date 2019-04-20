package com.edison.Object;

import org.json.JSONArray;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Vengat G on 1/4/2019.
 */

public class File_path extends RealmObject{

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}

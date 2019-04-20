package com.edison.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.edison.Object.UserObject;
import com.google.gson.Gson;

/**
 * Created by Vengat G on 1/2/2019.
 */

public class SessionManager {

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context)
    {
        this.context = context;
        this.preferences = context.getSharedPreferences("Edison",0);
        this.editor = preferences.edit();
    }


    public UserObject getUserObject()
    {
        String data = preferences.getString("uo","");
        UserObject userObject = new Gson().fromJson(data,UserObject.class);
        return userObject;
    }

    public void setUserObject(UserObject userObject)
    {
        String data = new Gson().toJson(userObject);
        editor.putString("uo",data);
        editor.commit();
    }


    public void clear_session() {
        editor.clear();
        editor.commit();
    }

}

package com.bambazu.fireup.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by blackxcorpio on 8/14/15.
 */
public class LocalDataManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Context context;

    private final String PREF_NAME = "FireUp_Prefs";
    private final int MODE_PRIVATE = 0;

    private final String userKey = "__USER__";
    private final String passKey = "__PASSWD__";
    private final String emailKey = "__EMAIL__";
    private final String statusKey = "__STATUS__";
    private final String skipKey = "__SKIPPED__";

    public LocalDataManager(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    public void saveLocalData(String user, String pass, String email, boolean isLogged, boolean isSkipped){
        editor = prefs.edit();

        editor.putString(userKey, user);
        editor.putString(passKey, pass);
        editor.putString(emailKey, email);
        editor.putBoolean(statusKey, isLogged);
        editor.putBoolean(skipKey, isSkipped);

        editor.commit();
    }

    public Object getLocalData(String key){
        Object obj = prefs.getString(key, null);
        return obj;
    }

    public boolean isLogged(){
        return prefs.getBoolean(statusKey, false);
    }

    public boolean isSkipped(){
        return prefs.getBoolean(skipKey, false);
    }

    public void deleteLocalData(String key){
        editor = prefs.edit();

        if(key != null){
            editor.remove(key);
        }
        else{
            editor.clear();
        }

        editor.commit();
    }
}

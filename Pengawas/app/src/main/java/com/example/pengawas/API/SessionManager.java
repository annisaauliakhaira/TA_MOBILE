package com.example.pengawas.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

public class SessionManager {
    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String NIP = "nip";

    public SessionManager(Context context){
        this._context=context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    //Make Session
    public void createLoginSession (String token, String name, String nip){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(TOKEN, "Bearer "+token);
        editor.putString(NAME, name);
        editor.putString(NIP, nip);
        editor.commit();
    }

    //Save Key and Value (save Sesion)
    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(TOKEN, sharedPreferences.getString(TOKEN, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(NIP, sharedPreferences.getString(NIP, null));
        return user;
    }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }
}

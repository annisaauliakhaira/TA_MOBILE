package com.example.mahasiswa.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.mahasiswa.LoginActivity;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String IS_LOGIN = "IsLogin";
    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String NIM = "nim";

    public SessionManager(Context context){
        this._context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    //Make session
    public void createLoginSession(String token, String name, String nim){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(TOKEN, "Bearer "+token);
        editor.putString(NAME, name);
        editor.putString(NIM, nim);
        editor.commit();
    }

    //Save key and value (save Session)
    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(TOKEN, sharedPreferences.getString(TOKEN, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(NIM, sharedPreferences.getString(NIM, null));
        return user;
    }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }

    public boolean chechToken(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public void isLogin(){
        String token = sharedPreferences.getString(TOKEN, null);
        if (token.equals("")){
            final HashMap<String, String> aboutt_ = new HashMap<>();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseBody> isLoginCall = apiInterface.isLogin(token);
            isLoginCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()!=200){
                        Intent intent = new Intent(_context, LoginActivity.class);
                        logoutSession();
                        _context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("isLogin", t.getMessage());
                }
            });
        }
    }
}

package com.example.ta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_username, et_password;
    Button bt_login;
    String Username, Password;
    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                Username = et_username.getText().toString();
                Password = et_password.getText().toString();
                login(Username, Password);

                break;
        }
    }

    private void login(String username, String password) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> loginCall = apiInterface.loginResponse(username, password);
        loginCall.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONObject jsonData = jsonRESULTS.getJSONObject("data");
                        Log.e("jsonData", jsonData.toString());
                        if (jsonData.length()!=0) {
                            String token = jsonData.getString("token");
                            String name = jsonData.getString("name");
                            String nip = jsonData.getString("nip");

                            sessionManager = new SessionManager(LoginActivity.this);
                            sessionManager.createLoginSession(token, name, nip);

                            Toast.makeText(LoginActivity.this, name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Gagal Login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error data", t.getMessage());
            }
        });
    }
}
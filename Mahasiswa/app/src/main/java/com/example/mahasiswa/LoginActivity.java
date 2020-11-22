package com.example.mahasiswa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mahasiswa.API.ApiClient;
import com.example.mahasiswa.API.ApiInterface;
import com.example.mahasiswa.API.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText et_username, et_password;
    Button bt_login;
    String Username, Password;
    ApiInterface apiInterface;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(LoginActivity.this);
        if (sessionManager.chechToken()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        et_username =findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.bt_login:
                    Username = et_username.getText().toString();
                    Password = et_password.getText().toString();
                    login(Username, Password);
                    loadingDialog.startLoadingDialog();
                    break;

                }
            }
        });
    }


    private void login(String username, String password) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> loginCall = apiInterface.loginResponse(username,password);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONObject jsonData = jsonRESULTS.getJSONObject("data");
                        Log.e("jsonData", jsonData.toString());
                        if (jsonData.length()!=0){
                            String token = jsonData.getString("token");
                            String name = jsonData.getString("name");
                            String nim = jsonData.getString("nim");

                            sessionManager =new SessionManager(LoginActivity.this);
                            sessionManager.createLoginSession(token, name, nim);

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
                }else{
                    Toast.makeText(LoginActivity.this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error Data", t.getMessage());
            }
        });
    }
}
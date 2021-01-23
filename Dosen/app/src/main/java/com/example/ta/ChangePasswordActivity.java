package com.example.ta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText et_oldPassword, et_newPassword, et_confirmPassword;
    Button bt_changePass, bt_cancelChange;
    String oldPassword, newPassword, confirmPassword;
    ApiInterface apiInterface;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        et_oldPassword = (EditText) findViewById(R.id.et_oldPass);
        et_newPassword = (EditText) findViewById(R.id.et_newPass);
        et_confirmPassword = (EditText) findViewById(R.id.et_confirmPass);
        bt_changePass = (Button) findViewById(R.id.bt_changePass);
        bt_cancelChange = (Button) findViewById(R.id.bt_cancelChange);

        bt_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.bt_changePass:
                        oldPassword = et_oldPassword.getText().toString();
                        newPassword = et_newPassword.getText().toString();
                        confirmPassword = et_confirmPassword.getText().toString();
                        changePass(token, oldPassword, newPassword, confirmPassword);
                        break;
                }
            }
        });

        bt_cancelChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void changePass(String token, String oldPassword, String newPassword, String confirmPassword){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> changePassCall = apiInterface.changePassword(token, oldPassword, newPassword, confirmPassword);
        changePassCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    et_oldPassword.setText("");
                    et_newPassword.setText("");
                    et_confirmPassword.setText("");
                    Intent intent =  new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(ChangePasswordActivity.this, "Gagal Change Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error data", t.getMessage());
            }
        });
    }

    private void validatiionPassword(){

    }
}
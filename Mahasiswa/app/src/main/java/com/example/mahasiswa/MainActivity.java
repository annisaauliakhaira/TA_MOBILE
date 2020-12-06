package com.example.mahasiswa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.mahasiswa.API.ApiClient;
import com.example.mahasiswa.API.ApiInterface;
import com.example.mahasiswa.API.SessionManager;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    ApiInterface apiInterface;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer =findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void logout(){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);
        Call<ResponseBody> logoutCall = apiInterface.logout(token);
        logoutCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        boolean isSuccess = jsonRESULTS.getBoolean("success");
                        if (isSuccess){
                            sessionManager.logoutSession();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Gagal Logout", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error data", t.getMessage());
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_about:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_examschedule:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExamscheduleFragment()).commit();
                break;
            case R.id.nav_history:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavView(){
        NavigationView navigationView =findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_Name = headerView.findViewById(R.id.tv_nameMhs);
        TextView nav_Nim = headerView.findViewById(R.id.tv_nimMhs);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String>User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> headerCall = apiInterface.getDetail(token);
        headerCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONObject headers =jsonRESULTS.getJSONObject("data");
                        nav_Name.setText(headers.getJSONObject("student").getString("name"));
                        nav_Nim.setText("NIM. "+headers.getJSONObject("student").getString("nim"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error Data View Model", t.getMessage());
            }
        });
    }
}
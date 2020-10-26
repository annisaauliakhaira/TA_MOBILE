package com.example.ta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void logout(){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);
        Call<ResponseBody> logoutCall = apiInterface.logout(token);
        logoutCall.enqueue(new Callback<ResponseBody>(){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        boolean isSuccess = jsonRESULTS.getBoolean("success");
                        if (isSuccess) {
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
}
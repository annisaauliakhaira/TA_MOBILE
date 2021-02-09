package com.example.ta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.NewseventAdapter;
import com.example.ta.ViewModel.NewseventViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsEventActivity extends AppCompatActivity {
    private static final int WRITE_PERMISSION = 1001;
    private RecyclerView rv_newsEvent;
    SessionManager sessionManager;
    EditText et_newsEvent;
    Button bt_save;
    String NewsEvent, id, token, newsevent_id = null;
    ApiInterface apiInterface;
    private NewseventViewModel newseventViewModel;
    private String[] action = {"Edit", "Delete"};
    private FloatingActionButton fab_download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_event);
        rv_newsEvent = findViewById(R.id.rv_news_event);
        fab_download = findViewById(R.id.fab_download);
        rv_newsEvent.setLayoutManager(new LinearLayoutManager(this));
        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        NewseventAdapter iAdapter = new NewseventAdapter(new NewseventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsEventActivity.this);
                builder.setTitle("Choose Action")
                        .setItems(action, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i==0){
                                    try {
                                        String data = item.getString("news_event");
                                        newsevent_id = item.getString("id");
                                        et_newsEvent.setText(data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else if(i==1){
                                    try {
                                        deleteNews(token, item.getString("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                builder.show();
            }
        });

        iAdapter.notifyDataSetChanged();
        rv_newsEvent.setAdapter(iAdapter);


        if (sessionManager.getUserDetail().equals("")){
            Intent intent = new Intent(NewsEventActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            try {
                id = getIntent().getStringExtra("data");
                newseventViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(NewseventViewModel.class);
                newseventViewModel.setNewsevent(token, id);
                newseventViewModel.getNewsEvent().observe(this, new Observer<JSONArray>() {
                    @Override
                    public void onChanged(JSONArray jsonArray) {
                        iAdapter.setData(jsonArray);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        et_newsEvent = findViewById(R.id.et_news);
        bt_save = findViewById(R.id.bt_saveNews);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.bt_saveNews:
                        NewsEvent = et_newsEvent.getText().toString();
                        if (newsevent_id==null){
                            Log.e("simpan", "A");
                            saveNews(token, NewsEvent);
                        }else {
                            Log.e("update", "B");
                            updateNews(token, NewsEvent);
                        }
                        break;
                }
            }
        });

        fab_download.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadBeritaAcara();
            }
        });
    }

    private void saveNews(String token, String news_event){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> saveCall = apiInterface.saveNewsData(news_event, token, id);
        saveCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(NewsEventActivity.this, "Save Data Success", Toast.LENGTH_SHORT).show();
                    et_newsEvent.setText("");
                    newseventViewModel.setNewsevent(token, id);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NewsEventActivity.this, "Failed to Save Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNews(String token, String news_event){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> updateCall =apiInterface.getUpdateNews(news_event, token, newsevent_id);
        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(NewsEventActivity.this, "Update Data Success", Toast.LENGTH_SHORT).show();
                et_newsEvent.setText("");
                newsevent_id = null;
                newseventViewModel.setNewsevent(token, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NewsEventActivity.this, "Failed Update Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteNews(String token, String newsevent_id){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> deleteCall = apiInterface.getDeleteNews(token, newsevent_id);
        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(NewsEventActivity.this, "Delete Data is Success", Toast.LENGTH_SHORT).show();
                newseventViewModel.setNewsevent(token, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void downloadBeritaAcara(){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> saveCall = apiInterface.downloadBeritaAcara(token, id);
        saveCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        String hasil = jsonRESULTS.getString("data");
                        Toast.makeText(NewsEventActivity.this, hasil, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(response.code()==409){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.errorBody().string());
                        String hasil = jsonRESULTS.getString("data");
                        Toast.makeText(NewsEventActivity.this, hasil, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NewsEventActivity.this, "Failed to Download Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
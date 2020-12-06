package com.example.pengawas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pengawas.API.ApiClient;
import com.example.pengawas.API.ApiInterface;
import com.example.pengawas.API.SessionManager;
import com.example.pengawas.Adapter.NewseventAdapter;
import com.example.pengawas.ViewModel.NewseventViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsEventActivity extends AppCompatActivity {
    private RecyclerView rv_newsEvent;
    SessionManager sessionManager;
    EditText et_newsEvent;
    Button bt_save;
    String NewsEvent, id, newsevent_id = null;
    ApiInterface apiInterface;
    private NewseventViewModel newseventViewModel;
    private String[] action = {"Edit", "Delete"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_event);
        rv_newsEvent = findViewById(R.id.rv_news_event);
        rv_newsEvent.setLayoutManager(new LinearLayoutManager(this));
        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        NewseventAdapter iAdapter = new NewseventAdapter(new NewseventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsEventActivity.this);
                builder.setTitle("Choose Action")
                        .setItems(action, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                if (i==0){
                                    try {
                                        String data = item.getString("news_event");
                                        newsevent_id = item.getString("id");
                                        et_newsEvent.setText(data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                else if (i==1){
                                    Toast.makeText(NewsEventActivity.this, "Delete", Toast.LENGTH_SHORT).show();
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
                newseventViewModel.setNews(token, id);
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
                            saveNews(token, NewsEvent);
                        }else {
                            updateNews(token, NewsEvent);
                        }
                        break;
                }
            }
        });
    }

    private void saveNews(String token, String news_event) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> SaveCall = apiInterface.getNewsData(news_event, token, id);
        SaveCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(NewsEventActivity.this, "Save Data Success", Toast.LENGTH_SHORT).show();
                    et_newsEvent.setText("");
                    newseventViewModel.setNews(token, id);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updateNews(String token, String news_event){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> updateCall = apiInterface.getUpdateNews(news_event, token, newsevent_id);
        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(NewsEventActivity.this, "Update Data Success", Toast.LENGTH_SHORT).show();
                et_newsEvent.setText("");
                newsevent_id = null;
                newseventViewModel.setNews(token, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void deleteNews(String token, String newsevent_id){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> deleteCall = apiInterface.getDeleteNews(token, newsevent_id);
        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(NewsEventActivity.this, "Delete Data Success", Toast.LENGTH_SHORT).show();
                newseventViewModel.setNews(token, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
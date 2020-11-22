package com.example.pengawas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.pengawas.API.SessionManager;
import com.example.pengawas.Adapter.NewseventAdapter;
import com.example.pengawas.ViewModel.NewseventViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewsEventActivity extends AppCompatActivity {
    private RecyclerView rv_newsEvent;
    SessionManager sessionManager;
    private NewseventViewModel newseventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_event);
        rv_newsEvent = findViewById(R.id.rv_news_event);
        rv_newsEvent.setLayoutManager(new LinearLayoutManager(this));

        NewseventAdapter iAdapter = new NewseventAdapter(new NewseventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {

            }
        });

        iAdapter.notifyDataSetChanged();
        rv_newsEvent.setAdapter(iAdapter);
        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        if (sessionManager.getUserDetail().equals("")){
            Intent intent = new Intent(NewsEventActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            try {
                String id = getIntent().getStringExtra("data");
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
    }
}
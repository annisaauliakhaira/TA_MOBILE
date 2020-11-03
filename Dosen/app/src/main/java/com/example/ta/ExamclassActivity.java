package com.example.ta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamclassAdapter;
import com.example.ta.ViewModel.ClassstudentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ExamclassActivity extends AppCompatActivity {
    ImageView iv_scan;
    private RecyclerView rv_detailclass;
    SessionManager sessionManager;
    private TextView tv_className, tv_classCode, tv_lecturerName, tv_dateDetail, tv_detailTime, tv_roomDetail;

    private ClassstudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examclass);
        rv_detailclass = findViewById(R.id.rv_student);
        rv_detailclass.setLayoutManager(new LinearLayoutManager(this));
        tv_className = findViewById(R.id.tv_className);
        tv_classCode = findViewById(R.id.tv_classCode);
        Toast.makeText(this, tv_classCode.getText().toString(), Toast.LENGTH_SHORT).show();
        tv_lecturerName = findViewById(R.id.tv_lectuerName);
        tv_dateDetail = findViewById(R.id.tv_dateDetail);
        tv_detailTime = findViewById(R.id.tv_timeDetail);
        tv_roomDetail = findViewById(R.id.tv_roomDetail);
        ExamclassAdapter iAdapter = new ExamclassAdapter(new ExamclassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {

            }
        });

        iAdapter.notifyDataSetChanged();
        rv_detailclass.setAdapter(iAdapter);

        sessionManager = new SessionManager(this);
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        if (sessionManager.getUserDetail().equals("")){
            Intent intent = new Intent(ExamclassActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            try {
                Intent intent = getIntent();
                JSONObject classDetail = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
                String id = classDetail.getString("id");
                tv_className.setText(classDetail.getJSONObject("class").getString("name"));
                tv_classCode.setText(classDetail.getJSONObject("class").getString("id"));
                tv_lecturerName.setText("");
                tv_dateDetail.setText(classDetail.getString("date"));
                tv_detailTime.setText(classDetail.getString("start_hour")+" - " + classDetail.getString("ending_hour"));
                tv_roomDetail.setText(classDetail.getString("room"));

                studentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ClassstudentViewModel.class);
                studentViewModel.setStudentclass(token, id);
                studentViewModel.getStudentClass().observe(this, new Observer<JSONArray>() {
                    @Override
                    public void onChanged(JSONArray jsonArray) {
                        iAdapter.setData(jsonArray);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        iv_scan = findViewById(R.id.iv_scan);
        Intent intent = getIntent();
    }

}
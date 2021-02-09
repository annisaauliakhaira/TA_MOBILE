package com.example.ta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamHistoryAdapter;
import com.example.ta.ViewModel.ExamHistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

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

public class ExamHistoryActivity extends AppCompatActivity {
    private static final int WRITE_PERMISSION = 1001;
    private RecyclerView rv_presenceDetail;
    SessionManager sessionManager;
    private TextView tv_courseHistory, tv_examtype, tv_class_name_history, tv_attendanceTime, tv_timeHistory, tv_dateHistory, tv_roomDetail, tv_presenceStatus,
    tv_studentHistory, tv_presenceHistory, tv_permitHistory, tv_absenceHistory;
    private FloatingActionButton fab_download;
    String id, token, verified_at="";
    private LoadingDialog loadingDialog;
    private ExamHistoryViewModel examHistoryViewModel;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_history);

        tv_courseHistory = findViewById(R.id.tv_courseHistory);
        tv_examtype = findViewById(R.id.tv_examtype);
        tv_class_name_history = findViewById(R.id.tv_class_name_history);
        tv_dateHistory = findViewById(R.id.tv_dateHistory);
        tv_roomDetail = findViewById(R.id.tv_roomHistory);
        tv_timeHistory = findViewById(R.id.tv_timeHistory);
        tv_studentHistory = findViewById(R.id.tv_studentHistory);
        tv_presenceHistory = findViewById(R.id.tv_presenceHistory);
        tv_permitHistory = findViewById(R.id.tv_permitHistory);
        tv_absenceHistory = findViewById(R.id.tv_absenceHistory);
        tv_presenceStatus = findViewById(R.id.tv_presenceStatus);
        tv_attendanceTime = findViewById(R.id.tv_attendanceTime);
        rv_presenceDetail = findViewById(R.id.rv_presenceDetail);
        fab_download = findViewById(R.id.fab_download);

        loadingDialog = new LoadingDialog(this);
        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.loadHistoryDetail);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        ExamHistoryAdapter iAdapter = new ExamHistoryAdapter();
        rv_presenceDetail.setLayoutManager(new LinearLayoutManager(this));
        iAdapter.notifyDataSetChanged();
        rv_presenceDetail.setAdapter(iAdapter);
        loadingDialog.startLoadingDialog();

        fab_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDaftarHadir();
            }
        });

        try {
            Intent intent = getIntent();
            JSONObject historyDetail = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
            id = historyDetail.getString("exam_id");
            tv_courseHistory.setText(historyDetail.getString("courses_name"));
            tv_examtype.setText(historyDetail.getString("examtype"));
            tv_class_name_history.setText(historyDetail.getString("class_name"));

            if (historyDetail.getString("waktu_masuk").equals("null")){
                tv_attendanceTime.setText("-");
            }
            else {
                tv_attendanceTime.setText(historyDetail.getString("waktu_masuk"));
            }

            tv_dateHistory.setText(historyDetail.getString("date"));
            tv_roomDetail.setText(historyDetail.getString("room"));
            tv_timeHistory.setText(historyDetail.getString("start_hour")+" - "+historyDetail.getString("ending_hour"));
            String Status = historyDetail.getString("status");
            String keterangan = "Status Not Found";
            if (Status.equals("0")){
                keterangan = ": "+ "Absence";
            }else if (Status.equals("1")){
                keterangan = ": "+ "Presence";
            }else if (Status.equals("2")){
                keterangan = ": "+ "Permit";
            }
            tv_presenceStatus.setText(keterangan);
            tv_studentHistory.setText("Student : "+historyDetail.getString("total"));
            tv_presenceHistory.setText("Presence : "+historyDetail.get("hadir"));
            tv_absenceHistory.setText("Absence : "+historyDetail.getString("tidak_hadir"));
            tv_permitHistory.setText("Permit : "+historyDetail.getString("izin"));
            verified_at = historyDetail.getString("verified_at");

            examHistoryViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ExamHistoryViewModel.class);
            examHistoryViewModel.setStudentPresence(token, id);
            examHistoryViewModel.getStudentPresence().observe(this, new Observer<JSONArray>() {
                @Override
                public void onChanged(JSONArray jsonArray) {
                    iAdapter.setData(jsonArray);
                    loadingDialog.dismissDialog();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    examHistoryViewModel.setStudentPresence(token, id);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void downloadDaftarHadir(){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> saveCall = apiInterface.downloadDaftarHadir(token, id);
        saveCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        String hasil = jsonRESULTS.getString("data");
                        Toast.makeText(ExamHistoryActivity.this, hasil, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(response.code()==409){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.errorBody().string());
                        String hasil = jsonRESULTS.getString("data");
                        Toast.makeText(ExamHistoryActivity.this, hasil, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExamHistoryActivity.this, "Failed to Download Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
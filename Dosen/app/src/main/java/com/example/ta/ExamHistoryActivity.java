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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ExamHistoryActivity extends AppCompatActivity {
    private static final int WRITE_PERMISSION = 1001;
    private RecyclerView rv_presenceDetail;
    SessionManager sessionManager;
    private TextView tv_courseHistory, tv_examtype, tv_class_name_history, tv_attendanceTime, tv_timeHistory, tv_dateHistory, tv_roomDetail, tv_presenceStatus,
    tv_studentHistory, tv_presenceHistory, tv_permitHistory, tv_absenceHistory;
    private FloatingActionButton fab_download;
    String id, token, url;
    ApiInterface apiInterface;
    private LoadingDialog loadingDialog;
    private ExamHistoryViewModel examHistoryViewModel;

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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(ExamHistoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        downloadFile(id,url);
                    }else{
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
                    }
                }else{
                    downloadFile(id,url);
                }
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

            url = ApiClient.BASE_URL+"printDaftarHadir/"+historyDetail.getString("exam_id");

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

    private void downloadFile(String fileName, String url){
        Uri downloadUri = Uri.parse(url);
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        try {
            if(manager != null){
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle(fileName+".pdf")
                        .setDescription("Download File")
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName+".pdf")
                        .setMimeType(getMimeType(downloadUri));
                manager.enqueue(request);
                Toast.makeText(this, "Download Starter", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(Intent.ACTION_VIEW, downloadUri);
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadFile(id,url);
            }else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getMimeType(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
}
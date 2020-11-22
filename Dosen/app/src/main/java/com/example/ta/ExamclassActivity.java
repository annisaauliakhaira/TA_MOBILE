package com.example.ta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamclassAdapter;
import com.example.ta.ViewModel.ClassstudentViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class ExamclassActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private RecyclerView rv_detailclass;
    SessionManager sessionManager;
    private TextView tv_className, tv_classCode, tv_lecturerName, tv_dateDetail, tv_detailTime, tv_roomDetail;
    Button scan, news_event;
    String id;

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
        LoadingDialog loadingDialog = new LoadingDialog(this);

        scan = findViewById(R.id.bt_absen);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(ExamclassActivity.this, QrCodeActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                permissionDeniedResponse.getRequestedPermission();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
        ExamclassAdapter iAdapter = new ExamclassAdapter(new ExamclassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {

            }
        });

        iAdapter.notifyDataSetChanged();
        rv_detailclass.setAdapter(iAdapter);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        if (sessionManager.getUserDetail().equals("")){
            Intent intent = new Intent(ExamclassActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            try {
                Intent intent = getIntent();
                JSONObject classDetail = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
                id = classDetail.getString("id");
                tv_className.setText(classDetail.getJSONObject("class").getString("name"));
                tv_classCode.setText(classDetail.getJSONObject("class").getString("id"));
                tv_lecturerName.setText("");
                tv_dateDetail.setText(classDetail.getString("date"));
                tv_detailTime.setText(classDetail.getString("start_hour")+" - " + classDetail.getString("ending_hour"));
                tv_roomDetail.setText(classDetail.getString("room"));

                news_event = findViewById(R.id.bt_berita);
                news_event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.bt_berita:
                                Intent intent = new Intent(ExamclassActivity.this, NewsEventActivity.class);
                                intent.putExtra("data", id);
                                startActivity(intent);
                                loadingDialog.startLoadingDialog();
                        }
                    }
                });

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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(ExamclassActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            AlertDialog alertDialog = new AlertDialog.Builder(ExamclassActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }

}
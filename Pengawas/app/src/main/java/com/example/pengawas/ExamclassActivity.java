package com.example.pengawas;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.example.pengawas.API.ApiClient;
import com.example.pengawas.API.ApiInterface;
import com.example.pengawas.API.SessionManager;
import com.example.pengawas.Adapter.ExamclassAdapter;
import com.example.pengawas.ViewModel.ClassstudentViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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

public class ExamclassActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private RecyclerView rv_detailclass;
    SessionManager sessionManager;
    private TextView tv_className, tv_classCode, tv_lecturerName, tv_dateDetail, tv_detailTime, tv_roomDetail;
    Button bt_scan, bt_newsevent;
    String id, token;
    ApiInterface apiInterface;

    private ClassstudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examclass);
        rv_detailclass = findViewById(R.id.rv_student);
        rv_detailclass.setLayoutManager(new LinearLayoutManager(this));
        tv_className = findViewById(R.id.tv_className);
        tv_classCode = findViewById(R.id.tv_classCode);
        tv_lecturerName = findViewById(R.id.tv_lectuerName);
        tv_dateDetail = findViewById(R.id.tv_dateDetail);
        tv_detailTime = findViewById(R.id.tv_timeDetail);
        tv_roomDetail = findViewById(R.id.tv_roomDetail);
        LoadingDialog loadingDialog = new LoadingDialog(this);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        bt_scan = findViewById(R.id.bt_absen);

        ExamclassAdapter iAdapter = new ExamclassAdapter(new ExamclassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExamclassActivity.this);
                builder.setTitle("Choose Action");
                    // add a radio button list'
                String[] action = {"Absence", "Presence", "Permit"};
                int checkedItem = item.getInt("presence_status"); // absence
                builder.setSingleChoiceItems(action, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                    // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String presence_id = item.getString("id");
                            int selectedStatus = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            updateManual(presence_id, String.valueOf(selectedStatus));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        iAdapter.notifyDataSetChanged();
        rv_detailclass.setAdapter(iAdapter);

        bt_scan.setOnClickListener(new View.OnClickListener() {
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


        if (sessionManager.getUserDetail().equals("")){
            Intent intent = new Intent(ExamclassActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            try {
                Intent intent = getIntent();
                JSONObject classDetail = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
                id = classDetail.getString("exam_id");
                tv_className.setText(classDetail.getString("class_name"));
                tv_classCode.setText(classDetail.getString("class_id"));
                tv_lecturerName.setText("");
                tv_dateDetail.setText(classDetail.getString("date"));
                tv_detailTime.setText(classDetail.getString("start_hour")+" - " + classDetail.getString("ending_hour"));
                tv_roomDetail.setText(classDetail.getString("room"));

                bt_newsevent = findViewById(R.id.bt_berita);
                bt_newsevent.setOnClickListener(new View.OnClickListener() {
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
                studentViewModel.setStudentClass(token, id);
                studentViewModel.getStudentClass().observe(this, new Observer<JSONArray>() {
                    @Override
                    public void onChanged(JSONArray jsonArray) {
                        iAdapter.setData(jsonArray);
                        Log.e("DATA :", jsonArray.toString());
                    }
                });
            } catch (JSONException e) {
                Log.e("Data", "error");
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
            String code = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            PresenceUpdate(token, code);

        }
    }

    public void PresenceUpdate(String token, String code){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> presenceCall = apiInterface.getPresence(token, code);
        presenceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONObject data = jsonRESULTS.getJSONObject("data");
                        String student_name = "Name: "+data.getString("student_name");
                        String nim = "NIM: " + data.getString("nim");
                        String presenceStatus = "Presence Status: " +data.getString("presence_status");
                        String keterangan = "Status Not Found";
                        if (presenceStatus.equals("0")){
                            keterangan = "Belum Ujian";
                        }else if (presenceStatus.equals("1")){
                            keterangan = "Sudah Ujian";
                        }else if (presenceStatus.equals("2")){
                            keterangan = "Tidak Ujian";
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(ExamclassActivity.this).create();
                        alertDialog.setTitle("Presence result");
                        alertDialog.setMessage(student_name);
                        alertDialog.setMessage(nim);
                        alertDialog.setMessage(keterangan);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void updateManual(String presence_id, String presence_status){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> presenceCall = apiInterface.UpdateManual(token, presence_id, presence_status);
        presenceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(ExamclassActivity.this, "Update Data is Success", Toast.LENGTH_SHORT).show();
                    studentViewModel.setStudentClass(token, id);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
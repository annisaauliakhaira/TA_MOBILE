package com.example.ta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.example.ta.Adapter.ExamclassAdapter;
import com.example.ta.ViewModel.ClassstudentViewModel;
import com.example.ta.ViewModel.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    ImageButton iv_geofence, scan, news_event, verified;
    String id, token, lat, lng;
    ApiInterface apiInterface;
    private LoadingDialog loadingDialog;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private float GEOFENCE_RADIUS = 15;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private ClassstudentViewModel studentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        setContentView(R.layout.activity_examclass);
        rv_detailclass = findViewById(R.id.rv_student);
        rv_detailclass.setLayoutManager(new LinearLayoutManager(this));
        tv_className = findViewById(R.id.tv_className);
        tv_classCode = findViewById(R.id.tv_classCode);
        tv_lecturerName = findViewById(R.id.tv_lectuerName);
        tv_dateDetail = findViewById(R.id.tv_dateDetail);
        tv_detailTime = findViewById(R.id.tv_timeDetail);
        tv_roomDetail = findViewById(R.id.tv_roomDetail);
        loadingDialog = new LoadingDialog(this);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        iv_geofence = findViewById(R.id.iv_geofence);

        scan = findViewById(R.id.bt_absen);

        ExamclassAdapter iAdapter = new ExamclassAdapter(new ExamclassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JSONObject item) throws JSONException {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExamclassActivity.this);
                builder.setTitle("Choose Action");
                String[] action ={"Absence", "Presence", "Permit"};
                int checkedItem = item.getInt("presence_status");
                builder.setSingleChoiceItems(action, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String presence_code = item.getString("presence_code");
                            int selectedStatus =((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            updateManual(presence_code, String.valueOf(selectedStatus));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        iAdapter.notifyDataSetChanged();
        rv_detailclass.setAdapter(iAdapter);

        loadingDialog.startLoadingDialog();

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


        try {
            Intent intent = getIntent();
            JSONObject classDetail = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
            id = classDetail.getString("id");
            if(statusAbsensi(classDetail.getString("waktu_masuk"))){
                scan.setVisibility(View.GONE);
            }
            tv_className.setText(classDetail.getString("course_name"));
            tv_classCode.setText(classDetail.getString("class_name"));
            String lecturerName = "";
            for (int i=0; i<classDetail.getJSONArray("lecturer").length(); i++){
                if(i==0){
                    lecturerName = lecturerName + classDetail.getJSONArray("lecturer").getJSONObject(i).getString("name");
                }else{
                    lecturerName = lecturerName + " & " + classDetail.getJSONArray("lecturer").getJSONObject(i).getString("name");
                }
            }
            tv_lecturerName.setText(lecturerName);
            tv_dateDetail.setText(classDetail.getString("date"));
            tv_detailTime.setText(classDetail.getString("start_hour")+" - " + classDetail.getString("ending_hour"));
            tv_roomDetail.setText(classDetail.getString("room"));

            String room_id = classDetail.getString("room_id");
            lat = classDetail.getString("latitude");
            lng = classDetail.getString("longitude");

            if (!lat.equals("0") && !lng.equals("0")){
                LatLng unand = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                addGeofence(unand);
            }

            news_event = findViewById(R.id.bt_berita);
            news_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.bt_berita:
                            Intent intent = new Intent(ExamclassActivity.this, NewsEventActivity.class);
                            intent.putExtra("data", id);
                            startActivity(intent);
                    }
                }
            });

            studentViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ClassstudentViewModel.class);
            studentViewModel.setStudentclass(token, id);
            studentViewModel.getStudentClass().observe(this, new Observer<JSONArray>() {
                @Override
                public void onChanged(JSONArray jsonArray) {
                    iAdapter.setData(jsonArray);
                    loadingDialog.dismissDialog();
                }
            });


            iv_geofence.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ExamclassActivity.this, GeofenceActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
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
                                scan.performClick();
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
        apiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> presenceCall = apiInterface.getPresence(token, code);
        presenceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        String pesan = jsonRESULTS.getString("pesan");
                        JSONObject jsonData = jsonRESULTS.getJSONObject("data");
                        String nama = jsonData.getString("student_name");
                        String nim = jsonData.getString("nim");

                        final Dialog dialog = new Dialog(ExamclassActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.scan_dialog);
                        TextView tv_name = (TextView) dialog.findViewById(R.id.tv_nameResult);
                        TextView tv_nim = (TextView) dialog.findViewById(R.id.tv_nimResult);
                        ImageView iv_precent = (ImageView) dialog.findViewById(R.id.iv_presenceResult);
                        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok_scan);
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scan.performClick();
                                dialog.dismiss();
                            }
                        });
                        tv_name.setText(nama);
                        tv_nim.setText(nim);
                        if(pesan.equals("-")){
                            iv_precent.setImageResource(R.drawable.ic_check);
                        }else{
                            iv_precent.setImageResource(R.drawable.ic_cross);
                        }
                        dialog.show();
                        studentViewModel.setStudentclass(token, id);
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

    public void updateManual(String presence_code, String presence_status){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> presenceCall = apiInterface.UpdateManual(token, presence_code, presence_status);
        presenceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(ExamclassActivity.this, "Update Data is Success", Toast.LENGTH_SHORT).show();
                studentViewModel.setStudentclass(token, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void addGeofence(LatLng latLng) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS,Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent(id, token);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);

                    }
                });
    }

    public boolean statusAbsensi(String mWaktu_mulai){
        Log.e("aaaa", "statusAbsensi: "+ mWaktu_mulai);
        if(!mWaktu_mulai.equals("null")){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
            Log.e("aaaa", "statusAbsensi: "+ simpleDateFormat.format(Calendar.getInstance().getTime()));

            try {
                Date waktu_sekarang = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));
                Date waktu_mulai = simpleDateFormat.parse(mWaktu_mulai);

                long different = waktu_sekarang.getTime() - (waktu_mulai.getTime() + (1000 * 60 * 10));
                if(different < 0){
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void removeGeofence(){
        if (!lat.equals("0") && !lng.equals("0")){
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent(id, token);
            geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    public void onPause(){
        super.onPause();
    }

}
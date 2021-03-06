package com.example.mahasiswa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahasiswa.API.SessionManager;
import com.example.mahasiswa.ViewModel.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ExamcardActivity extends AppCompatActivity implements OnMapReadyCallback  {
    ImageView iv_qrcode, img_mahasiswa;
    TextView tv_course, tv_date, tv_presenceStatus, tv_studentName, tv_studentNim, tv_roomCard,
            tv_timeCard, tv_timeCard2, tv_className, tv_examtype, tv_lecturerName;
    JSONObject data;
    Bitmap bitmap;

    private static final String TAG = "GeofenceActivity";

    SessionManager sessionManager;
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private float GEOFENCE_RADIUS = 15;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private String token, lat, lng, id, classId, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examcard);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        sessionManager = new SessionManager(this);
        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        iv_qrcode = findViewById(R.id.iv_qr_code);
        tv_course = findViewById(R.id.tv_course);
        tv_date = findViewById(R.id.tv_date);
        tv_presenceStatus = findViewById(R.id.tv_presenceStatus);
        tv_studentName = findViewById(R.id.tv_studentName);
        tv_studentNim = findViewById(R.id.tv_studentNim);
        tv_roomCard = findViewById(R.id.tv_roomCard);
        tv_timeCard = findViewById(R.id.tv_timeCard);
        tv_timeCard2 = findViewById(R.id.tv_timeCard2);
        tv_className = findViewById(R.id.tv_class_name);
        tv_examtype = findViewById(R.id.tv_examtype);
        tv_lecturerName = findViewById(R.id.tv_lecturerName);

        Intent intent = getIntent();
        try {
            data = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
            id = data.getString("exam_id");
            String presenceStatus = data.getString("presence_status");
            String keterangan = "Status Not Found";
            if (presenceStatus.equals("0")){
                keterangan = ": "+ "Absence";
            }else if (presenceStatus.equals("1")){
                keterangan = ": "+ "Presence";
            }else if (presenceStatus.equals("2")){
                keterangan = ": "+ "Permit";
            }
            code = data.getString("presence_code");
            String courses = data.getJSONObject("classes").getString("class_name");
            String date = ": "+data.getString("date");
            String student_name = ": "+ data.getString("student_name");
            String student_nim = ": "+ data.getString("nim");
            String room = ": "+ data.getString("room");
            String time_begin = ": "+ data.getString("start_hour");
            String time_finished = " - "+ data.getString("ending_hour");
            String classKode = data.getJSONObject("classes").getString("class_id");
            classId = data.getJSONObject("classes").getString("id");
            String examtype = data.getString("exam_type");
            Log.e(TAG, "onCreate: " + classId );

            String lecturerName = "";
            for (int i=0; i<data.getJSONArray("lecturer").length(); i++){
                if(i==0){
                    lecturerName = lecturerName + data.getJSONArray("lecturer").getJSONObject(i).getString("name");
                }else{
                    lecturerName = lecturerName + " & " + data.getJSONArray("lecturer").getJSONObject(i).getString("name");
                }
            }
            tv_lecturerName.setText(lecturerName);
            lat = data.getString("latitude");
            lng = data.getString("longitude");

            if (!lat.equals("0") && !lng.equals("0")){
                LatLng unand = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                addGeofence(unand);
            }

            QRGEncoder qrgEncoder = new QRGEncoder(code, null, QRGContents.Type.TEXT, 300);

            bitmap = qrgEncoder.encodeAsBitmap();

            iv_qrcode.setImageBitmap(bitmap);
            tv_course.setText(courses);
            tv_date.setText(date);
            tv_presenceStatus.setText(keterangan);
            tv_studentName.setText(student_name);
            tv_studentNim.setText(student_nim);
            tv_roomCard.setText(room);
            tv_timeCard.setText(time_begin);
            tv_timeCard2.setText(time_finished);
            tv_className.setText(classKode);
            tv_examtype.setText(examtype);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //we don't have the permission
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //we don't have the permission
                Toast.makeText(this, "Background location access is necessary for geofences..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addGeofence(LatLng latLng) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent(code, classId, token);

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
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    public void removeGeofence(){
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent(code, classId, token);
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

    public void onPause(){
        super.onPause();
//        removeGeofence();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng unand = new LatLng(-0.9143, 100.4616);
        if (!lat.equals("0") && !lng.equals("0")){
            unand = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unand, 20));

        enableUserLocation();

        initLocation();
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    private void initLocation(){
        if (!lat.equals("0") && !lng.equals("0")){
            LatLng oldGeo = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
            mMap.clear();
            addMarker(oldGeo);
            addCircle(oldGeo);
            addGeofence(oldGeo);
        }
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(GEOFENCE_RADIUS);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }


}
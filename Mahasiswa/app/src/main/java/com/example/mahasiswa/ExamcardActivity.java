package com.example.mahasiswa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ExamcardActivity extends AppCompatActivity {
    ImageView iv_qrcode;
    TextView tv_course, tv_date, tv_presenceStatus;
    JSONObject data;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examcard);

        iv_qrcode = findViewById(R.id.iv_qr_code);
        tv_course = findViewById(R.id.tv_course);
        tv_date = findViewById(R.id.tv_date);
        tv_presenceStatus = findViewById(R.id.tv_presenceStatus);

        Intent intent = getIntent();
        try {
            data = new JSONObject(Objects.requireNonNull(intent.getStringExtra("data")));
            String presenceStatus = data.getString("presence_status");
            String keterangan = "Status Not Found";
            if (presenceStatus.equals("0")){
                keterangan = "Belum Ujian";
            }else if (presenceStatus.equals("1")){
                keterangan = "Sudah Ujian";
            }else if (presenceStatus.equals("2")){
                keterangan = "Tidak Ujian";
            }
            String passcode = data.getString("presence_code");
            String course = data.getJSONObject("courses").getString("courses_name");
            String date = data.getString("date");
            QRGEncoder qrgEncoder = new QRGEncoder(passcode, null, QRGContents.Type.TEXT, 300);

            bitmap = qrgEncoder.encodeAsBitmap();

            iv_qrcode.setImageBitmap(bitmap);
            tv_course.setText(course);
            tv_date.setText(date);
            tv_presenceStatus.setText(keterangan);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
package com.example.ta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.example.ta.ViewModel.AboutViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class AboutFragment extends Fragment {
    View v;
    private AboutViewModel aboutViewModel;
    SessionManager sessionManager;
    private TextView tv_name, tv_nip;
    private CardView cv_changePass, cv_changeImage, cv_examSchedule, cv_examHistory;
    private LoadingDialog loadingDialog;
    private Uri filepath;
    private Bitmap bitmap;
    private static final int STORAGE_PERMISSION_CODE = 4655;

    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        tv_name =v.findViewById(R.id.tv_name1);
        tv_nip = v.findViewById(R.id.tv_nip1);
        requestStoragePermission();

        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
//        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        String token = User.get(sessionManager.TOKEN);

        aboutViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(AboutViewModel.class);

        loadingDialog.startLoadingDialog();
        aboutViewModel.setAbout(token);
        aboutViewModel.getAbout().observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                if(stringStringHashMap.size() > 0){
                    tv_name.setText(stringStringHashMap.get(aboutViewModel.NAME));
                    tv_nip.setText("NIP. "+ stringStringHashMap.get(aboutViewModel.NIP));
                }
                loadingDialog.dismissDialog();
            }
        });

        cv_changePass = (CardView) v.findViewById(R.id.cv_changePassword);
        cv_examSchedule = (CardView) v.findViewById(R.id.cv_examSchedule);
        cv_examHistory = (CardView) v.findViewById(R.id.cv_examHistory);
        cv_changeImage = (CardView) v.findViewById(R.id.cv_changeImage);

        cv_examSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabExamscheduleFragment examscheduleFragment = new tabExamscheduleFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, examscheduleFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        cv_examHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabHistoryFragment tabHistoryFragment = new tabHistoryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, tabHistoryFragment, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        cv_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        cv_changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
            } catch (Exception ex) {

            }
        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        String encode = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
        return encode;
    }

    public void uploadImage(){
//        String path = getPath(filepath);
//        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
//        Log.e("TAG", "uploadImage: "+body.toString() );
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

}

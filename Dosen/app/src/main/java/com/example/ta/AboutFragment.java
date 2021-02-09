package com.example.ta;

import android.Manifest;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;
import com.example.ta.API.SessionManager;
import com.example.ta.ViewModel.AboutViewModel;
import com.example.ta.ViewModel.PathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AboutFragment extends Fragment {
    View v;
    private AboutViewModel aboutViewModel;
    SessionManager sessionManager;
    private TextView tv_name, tv_nip;
    private CardView cv_changePass, cv_changeImage, cv_examSchedule, cv_examHistory;
    private LoadingDialog loadingDialog;
    private Uri filepath;
    private static final int STORAGE_PERMISSION_CODE = 4655;
    private CircleImageView img;
    String token;

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
        img = v.findViewById(R.id.iv_dosenProfile);
        requestStoragePermission();

        sessionManager = new SessionManager(getContext());
        loadingDialog = new LoadingDialog(getActivity());
//        sessionManager.isLogin();
        HashMap<String, String> User = sessionManager.getUserDetail();
        token = User.get(sessionManager.TOKEN);

        aboutViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(AboutViewModel.class);

        loadingDialog.startLoadingDialog();
        aboutViewModel.setAbout(token);
        String linkImage = "";
        aboutViewModel.getAbout().observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                if(stringStringHashMap.size() > 0){
                    tv_name.setText(stringStringHashMap.get(aboutViewModel.NAME));
                    tv_nip.setText("NIP. "+ stringStringHashMap.get(aboutViewModel.NIP));
                    if(!stringStringHashMap.get(aboutViewModel.IMAGE).equals("")){
                        Glide.with(getActivity()).load(stringStringHashMap.get(aboutViewModel.IMAGE).toString()).into(img);
                    }
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
            uploadImage();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String name = "";
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null);
        if(cursor != null){
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            name = cursor.getString(nameIndex);
            cursor.close();
        }
        return name;
    }

    public void uploadImage(){
        try {
            String filePath = PathUtil.getPath(getContext(),filepath);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseBody> res = apiInterface.changePicture(token, body);
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code()==200){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            String hasil = jsonRESULTS.getString("data");
                            Toast.makeText(getActivity(), hasil, Toast.LENGTH_SHORT).show();
                            aboutViewModel.setAbout(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(response.code()==409){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.errorBody().string());
                            String hasil = jsonRESULTS.getString("data");
                            Toast.makeText(getActivity(), hasil, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.e("TAG", "onResponse: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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

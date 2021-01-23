package com.example.ta.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ta.API.ApiClient;
import com.example.ta.API.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassstudentViewModel extends ViewModel {
    private ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listStudent = new MutableLiveData<>();

    public void setStudentclass(String token, String id){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> studentCall = apiInterface.getDetailKelas(token, id);
        studentCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray studentclasses = jsonRESULTS.getJSONArray("data");
                        listStudent.postValue(studentclasses);
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

    public LiveData<JSONArray> getStudentClass(){ return listStudent; }
}

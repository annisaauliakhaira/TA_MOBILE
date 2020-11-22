package com.example.mahasiswa.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mahasiswa.API.ApiClient;
import com.example.mahasiswa.API.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listExamschedule = new MutableLiveData<>();

    public void setExamschedule(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> examScheduleCall = apiInterface.getExamschedule(token);
        examScheduleCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject jsonRESULTS = null;
                if (response.isSuccessful()){
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray examschedules = jsonRESULTS.getJSONArray("data");
                        listExamschedule.postValue(examschedules);
//                        Log.e("Data", examschedules.toString());
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
    public LiveData<JSONArray> getExamschedule(){
        return listExamschedule;
    }
}

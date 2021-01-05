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

public class ExamViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listExamsUtsSchedule = new MutableLiveData<>();
    private MutableLiveData<JSONArray> listExamUasschedule = new MutableLiveData<>();

    public void setExamUtsSchedule(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> examScheduleCall = apiInterface.getExamschedule(token, "1");
        examScheduleCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray examschedules = jsonRESULTS.getJSONArray("data");
                        listExamsUtsSchedule.postValue(examschedules);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("eror view model",t.getMessage());
            }
        });
    }

    public void setExamUasSchedule(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> examScheduleCall = apiInterface.getExamschedule(token, "2");
        examScheduleCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray examschedules = jsonRESULTS.getJSONArray("data");
                        listExamUasschedule.postValue(examschedules);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("eror view model",t.getMessage());
            }
        });
    }

    public LiveData<JSONArray> getExamUtsSchedule(){
        return listExamsUtsSchedule;
    }

    public LiveData<JSONArray> getExamUasSchedule(){
        return listExamUasschedule;
    }
}

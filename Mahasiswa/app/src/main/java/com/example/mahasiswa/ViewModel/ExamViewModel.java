package com.example.mahasiswa.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mahasiswa.API.ApiClient;
import com.example.mahasiswa.API.ApiInterface;
import com.example.mahasiswa.ExamSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<ArrayList<ExamSchedule>> listExamschedule = new MutableLiveData<>();

    public void setExamschedule(String token){
        final ArrayList<ExamSchedule> listItems = new ArrayList<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> examScheduleCall = apiInterface.getExamschedule(token);
        examScheduleCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray examschedules = jsonRESULTS.getJSONArray("data");
                        for (int i = 0; i < examschedules.length(); i++){
                            JSONObject examschedule = examschedules.getJSONObject(i);
                            ExamSchedule examscheduleItem = new ExamSchedule();
                            examscheduleItem.setClasses(examschedule.getJSONObject("classes").getString("class_name"));
                            examscheduleItem.setDate(examschedule.getString("date"));
                            examscheduleItem.setTime(examschedule.getString("start_hour")+" - "+examschedule.getString("ending_hour"));
                            examscheduleItem.setRoom(examschedule.getString("room"));
                            listItems.add(examscheduleItem);
                        }
                        listExamschedule.postValue(listItems);
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
    public LiveData<ArrayList<ExamSchedule>> getExamschedule(){
        return listExamschedule;
    }
}

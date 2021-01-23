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

public class HistoryViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listHistoryUts = new MutableLiveData<>();
    private MutableLiveData<JSONArray> listHistoryUas = new MutableLiveData<>();

    public void setHistoryUts(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> historyCall = apiInterface.getHistory(token, "1");
        historyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray histories = jsonRESULTS.getJSONArray("data");
                        listHistoryUts.postValue(histories);
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

    public void setHistoryUas(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> historyCall = apiInterface.getHistory(token, "2");
        historyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray histories = jsonRESULTS.getJSONArray("data");
                        listHistoryUas.postValue(histories);
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


    public LiveData<JSONArray> getHistoryUts(){
        return listHistoryUts;
    }


    public LiveData<JSONArray> getHistoryUas(){
        return listHistoryUas;
    }
}

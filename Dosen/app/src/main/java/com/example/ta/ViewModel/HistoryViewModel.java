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

public class HistoryViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listHistory = new MutableLiveData<>();

    public void setHistory(String token){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> historyCall = apiInterface.getHistory(token);
        historyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray histories = jsonRESULTS.getJSONArray("data");
                        listHistory.postValue(histories);
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

    public LiveData<JSONArray> getHistory(){
        return listHistory;
    }
}

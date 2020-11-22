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

public class NewseventViewModel extends ViewModel {
    ApiInterface apiInterface;
    private MutableLiveData<JSONArray> listNews = new MutableLiveData<>();

    public void setNewsevent(String token, String id){
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> newsCall = apiInterface.getNewsEvent(token, id);
        newsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {
                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONArray newsEvents = jsonRESULTS.getJSONArray("data");
                        listNews.postValue(newsEvents);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("ERROR DATA", "ada error di server code : "+response.isSuccessful());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FAIL DATA", "on failur error : "+t.toString());
            }
        });
    }

    public LiveData<JSONArray> getNewsEvent(){ return listNews; }
}

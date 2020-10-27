package com.example.pengawas.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pengawas.API.ApiClient;
import com.example.pengawas.API.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutViewModel extends ViewModel {
    private ApiInterface apiInterface;
    private MutableLiveData<HashMap<String, String>> mData = new MutableLiveData<>();
    public static final String NAME = "name";
    public static final String NIP = "nip";
    public static final String EMAIL = "email";

    public void setAbout(String token){
        final HashMap<String, String> about_ = new HashMap<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> aboutCall = apiInterface.getDetail(token);
        aboutCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject jsonRESULTS = null;
                    try {

                        jsonRESULTS = new JSONObject(response.body().string());
                        JSONObject abouts = jsonRESULTS.getJSONObject("data");
                        if (abouts.length()!=0){
                            String name =abouts.getJSONObject("staff").getString("name");
                            String nip = abouts.getJSONObject("staff").getString("nip");
                            String email = abouts.getJSONObject("user").getString("email");
                            about_.put(NAME, name);
                            about_.put(NIP, nip);
                            about_.put(EMAIL, email);
                            mData.setValue(about_);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error View Model", t.getMessage());
            }
        });
    }

    public LiveData<HashMap<String, String>> getAbout(){ return mData; }
}

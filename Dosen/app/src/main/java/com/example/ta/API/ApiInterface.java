package com.example.ta.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("lecturer/login")
    Call<ResponseBody> loginResponse(
            @Field("username") String username,
            @Field("password") String password
    );
    @POST("lecturer/logout")
    Call<ResponseBody>logout(@Header("Authorization") String authToken);

    @POST("lecturer/examschedule")
    Call<ResponseBody> getExamschedule(@Header("Authorization") String authToken);
}

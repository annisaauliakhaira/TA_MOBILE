package com.example.pengawas.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("staff/login")
    Call<ResponseBody>loginResponse(
        @Field("username") String username,
        @Field("password") String password
    );

    @POST("staff/logout")
    Call<ResponseBody>logout(@Header("Authorization") String authToken);

    @POST("staff/examschedule")
    Call<ResponseBody>getExamSchedule(@Header("Authorization") String authToken);

    @POST("staff/details")
    Call<ResponseBody>getDetail(@Header("Authorization") String authToken);

    @POST("staff/examclass/{id}")
    Call<ResponseBody>getDetailClass(@Header("Authorization") String authToken, @Path("id") String examclass_id);
}

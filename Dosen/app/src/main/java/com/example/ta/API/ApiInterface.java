package com.example.ta.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("lecturer/login")
    Call<ResponseBody> loginResponse(
            @Field("username") String username,
            @Field("password") String password
    );
    @POST("lecturer/logout")
    Call<ResponseBody>logout(@Header("Authorization") String authToken);

    @POST("lecturer/isLogin")
    Call<ResponseBody>isLogin(@Header("Authorization") String authToken);

    @POST("lecturer/examschedule")
    Call<ResponseBody> getExamschedule(@Header("Authorization") String authToken);

    @POST("lecturer/details")
    Call<ResponseBody> getDetail(@Header("Authorization") String authToken);

    @POST("lecturer/examclass/{id}")
    Call<ResponseBody>getDetailKelas(@Header("Authorization") String authToken, @Path("id") String examclass_id);

    @POST("lecturer/show/{id}")
    Call<ResponseBody>getNewsEvent(@Header("Authorization") String authToken, @Path("id") String news_id);

}

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
    Call<ResponseBody>getDetail(@Header("Authorization") String authToken);

    @POST("lecturer/examclass/{id}")
    Call<ResponseBody>getDetailKelas(@Header("Authorization") String authToken, @Path("id") String examclass_id);

    @FormUrlEncoded
    @POST("lecturer/saveNews/{exam_id}")
    Call<ResponseBody>saveNewsData(
            @Field("news_event") String news_event,
            @Header("Authorization") String authToken,
            @Path("exam_id") String exam_id);

    @POST("lecturer/show/{id}")
    Call<ResponseBody>getNewsEvent(@Header("Authorization") String authToken, @Path("id") String news_id);

    @FormUrlEncoded
    @POST("lecturer/update/{id}")
    Call<ResponseBody>getUpdateNews(
            @Field("news_event") String news_event,
            @Header("Authorization") String authToken,
            @Path("id") String news_id);

    @POST("lecturer/delete/{id}")
    Call<ResponseBody>getDeleteNews(@Header("Authorization") String authToken, @Path("id") String news_id);

    @FormUrlEncoded
    @POST("lecturer/presence")
    Call<ResponseBody>getPresence(@Header("Authorization") String authToken, @Field("code") String code);

    @POST("lecturer/getHistory")
    Call<ResponseBody>getHistory(@Header("Authorization") String authToken);

    @POST("lecturer/updateManual/{id}/{presence_status}")
    Call<ResponseBody>UpdateManual(@Header("Authorization") String authToken, @Path("id") String id, @Path("presence_status") String presence_status);

}

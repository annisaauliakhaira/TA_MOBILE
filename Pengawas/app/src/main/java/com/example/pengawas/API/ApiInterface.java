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

    @POST("staff/isLogin")
    Call<ResponseBody>isLogin(@Header("Authorization") String authToken);

    @FormUrlEncoded
    @POST("staff/changePassword")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String authToken,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword
    );

    @FormUrlEncoded
    @POST("staff/examschedule")
    Call<ResponseBody>getExamSchedule(@Header("Authorization") String authToken, @Field("examtype_id") String examtype_id);

    @POST("staff/details")
    Call<ResponseBody>getDetail(@Header("Authorization") String authToken);

    @POST("staff/examclass/{id}")
    Call<ResponseBody>getDetailClass(@Header("Authorization") String authToken, @Path("id") String examclass_id);

    @FormUrlEncoded
    @POST("staff/saveNews/{exam_id}")
    Call<ResponseBody>getNewsData(
            @Field("news_event") String news_event,
            @Header("Authorization") String authToken,
            @Path("exam_id") String exam_id);

    @POST("staff/show/{id}")
    Call<ResponseBody>getNewsEvent(@Header("Authorization") String authToken, @Path("id") String news_id);

    @FormUrlEncoded
    @POST("staff/update/{id}")
    Call<ResponseBody>getUpdateNews(
            @Field("news_event") String news_event,
            @Header("Authorization") String authToken,
            @Path("id") String news_id);

    @POST("staff/delete/{id}")
    Call<ResponseBody>getDeleteNews(@Header("Authorization") String authToken, @Path("id") String news_id);

    @FormUrlEncoded
    @POST("staff/presence")
    Call<ResponseBody>getPresence(@Header("Authorization") String authToken, @Field("code") String code);

    @POST("staff/updateManual/{code}/{presence_status}")
    Call<ResponseBody>UpdateManual(@Header("Authorization") String authToken, @Path("code") String id, @Path("presence_status") String presence_status);

    @FormUrlEncoded
    @POST("staff/saveLatLong/{id}")
    Call<ResponseBody>saveLatLong(@Header("Authorization") String authToken, @Path("id") String id, @Field("lat")String lat, @Field("lng")String lng);
}

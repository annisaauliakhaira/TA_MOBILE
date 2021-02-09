package com.example.ta.API;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("lecturer/login")
    Call<ResponseBody> loginResponse(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("lecturer/changePassword")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String authToken,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword
    );


    @POST("lecturer/logout")
    Call<ResponseBody>logout(@Header("Authorization") String authToken);

    @POST("lecturer/isLogin")
    Call<ResponseBody>isLogin(@Header("Authorization") String authToken);

    @FormUrlEncoded
    @POST("lecturer/examschedule")
    Call<ResponseBody> getExamschedule(@Header("Authorization") String authToken, @Field("examtype_id") String examtype_id);

    @POST("lecturer/examschedule/{id}")
    Call<ResponseBody> examScheduleUpdateStatus(@Header("Authorization") String authToken, @Path("id") String id);

    @POST("lecturer/details")
    Call<ResponseBody>getDetail(@Header("Authorization") String authToken);

    @POST("lecturer/examclass/{id}")
    Call<ResponseBody>getDetailKelas(@Header("Authorization") String authToken, @Path("id") String examclass_id);

    @POST("lecturer/PresenceHistory/{id}")
    Call<ResponseBody>getDetailPresence(@Header("Authorization") String authToken, @Path("id") String exam_id);

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

    @FormUrlEncoded
    @POST("lecturer/getHistory")
    Call<ResponseBody>getHistory(@Header("Authorization") String authToken, @Field("type_id") String examtype_id);

    @POST("lecturer/updateManual/{code}/{presence_status}")
    Call<ResponseBody>UpdateManual(@Header("Authorization") String authToken, @Path("code") String id, @Path("presence_status") String presence_status);

    @GET("lecturer/print-daftar-hadir/{id}")
    Call<ResponseBody>downloadDaftarHadir(@Header("Authorization") String authToken, @Path("id") String jadwalId);

    @GET("lecturer/print-berita-acara/{id}")
    Call<ResponseBody>downloadBeritaAcara(@Header("Authorization") String authToken, @Path("id") String jadwalId);

    @Multipart
    @POST("lecturer/changePicture")
    Call<ResponseBody>changePicture(@Header("Authorization") String authToken, @Part MultipartBody.Part image);

}

package com.example.mahasiswa.API;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("student/login")
    Call<ResponseBody> loginResponse(
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("student/logout")
    Call<ResponseBody>logout(@Header("Authorization") String authToken);

    @POST("student/isLogin")
    Call<ResponseBody>isLogin(@Header("Authorization") String authToken);

    @FormUrlEncoded
    @POST("student/changePassword")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String authToken,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword
    );

    @FormUrlEncoded
    @POST("student/examhistory")
    Call<ResponseBody>getHistory(@Header("Authorization") String authToken,  @Field("examtype_id") String examtype_id);

    @FormUrlEncoded
    @POST("student/examschedule")
    Call<ResponseBody>getExamschedule(@Header("Authorization") String authToken,  @Field("examtype_id") String examtype_id);

    @FormUrlEncoded
    @POST("student/waktuMasukPresence")
    Call<ResponseBody>updateWaktuPresence(@Header("Authorization") String authToken,  @Field("code") String code, @Field("class_id") String class_id, @Field("type") String type);

    @POST("student/details")
    Call<ResponseBody>getDetail(@Header("Authorization") String authToken);

    @Multipart
    @POST("student/changePicture")
    Call<ResponseBody>changePicture(@Header("Authorization") String authToken, @Part MultipartBody.Part image);

}

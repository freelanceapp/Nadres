package com.endpoint.nadres.services;


import com.endpoint.nadres.models.ArticleModel;
import com.endpoint.nadres.models.CreateRoomModel;
import com.endpoint.nadres.models.NotificationDataModel;
import com.endpoint.nadres.models.SettingModel;
import com.endpoint.nadres.models.SingleArticleModel;
import com.endpoint.nadres.models.SingleRoomModel;
import com.endpoint.nadres.models.StageDataModel;
import com.endpoint.nadres.models.TeacherModel;
import com.endpoint.nadres.models.UserModel;
import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.rest.Get;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("email") String email,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("user_type") String user_type,
                                       @Field("software_type") String software_type,
                                       @Field("stage[]") List<String> stage,
                                       @Field("class[]") List<String> classs


    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("email") RequestBody email,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("user_type") RequestBody user_type,
                                    @Part("software_type") RequestBody software_type,
                                    @Part("stage[]") List<RequestBody> stage,
                                    @Part("class[]") List<RequestBody> classs,
                                    @Part MultipartBody.Part image
    );


    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImageTeacher(@Field("name") String name,
                                              @Field("email") String email,
                                              @Field("phone_code") String phone_code,
                                              @Field("phone") String phone,
                                              @Field("user_type") String user_type,
                                              @Field("software_type") String software_type,
                                              @Field("details") String details,
                                              @Field("stage[]") List<String> stage,
                                              @Field("skills[]") List<String> skills


    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImageTeacher(@Part("name") RequestBody name,
                                           @Part("email") RequestBody email,
                                           @Part("phone_code") RequestBody phone_code,
                                           @Part("phone") RequestBody phone,
                                           @Part("user_type") RequestBody user_type,
                                           @Part("software_type") RequestBody software_type,
                                           @Part("details") RequestBody details,
                                           @Part("stage[]") List<RequestBody> stage,
                                           @Part("skills[]") List<RequestBody> skills,
                                           @Part MultipartBody.Part image
    );


    @GET("api/teachers-by-skill_type")
    Call<TeacherModel> getTeacher(
            @Query("skill_type") String skill_type);

    @GET("api/all-articles")
    Call<ArticleModel> getArticles(@Query("pagination_status") String pagination_status,
                                   @Query("page") int page,
                                   @Query("per_link_") int limit_per_page

    );

    @GET("api/articles-no-this_id")
    Call<ArticleModel> getArticlesExpext(@Query("pagination_status") String pagination_status,
                                         @Query("page") int page,
                                         @Query("per_link_") int limit_per_page,
                                         @Query("article_id") String article_id
    );

    @GET("api/articles-details")
    Call<SingleArticleModel> articledetials(@Query("article_id") String article_id

    );

    @GET("api/stages-classess")
    Call<StageDataModel> getStages();


    @POST("api/logout")
    Call<ResponseBody> Logout(@Header("Authorization") String user_token);


    @GET("api/show-setting")
    Call<SettingModel> getSettings();

    @FormUrlEncoded
    @POST("api/update-profile")
    Call<UserModel> EditstudentprofileWithoutImage(@Field("name") String name,
                                                   @Field("email") String email,
                                                   @Field("user_type") String user_type,
                                                   @Field("software_type") String software_type,
                                                   @Field("id") String id,
                                                   @Field("stage[]") List<String> stage,
                                                   @Field("class[]") List<String> classs


    );

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> EditstudentprofileWithImage(@Part("name") RequestBody name,
                                                @Part("email") RequestBody email,
                                                @Part("user_type") RequestBody user_type,
                                                @Part("software_type") RequestBody software_type,
                                                @Part("id") RequestBody id,
                                                @Part("stage[]") List<RequestBody> stage,
                                                @Part("class[]") List<RequestBody> classs,
                                                @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("api/update-profile")
    Call<UserModel> EditteacherprofileWithoutImage(@Field("name") String name,
                                                   @Field("email") String email,
                                                   @Field("user_type") String user_type,
                                                   @Field("software_type") String software_type,
                                                   @Field("id") String id,
                                                   @Field("details") String details,
                                                   @Field("stage[]") List<String> stage,
                                                   @Field("skills[]") List<String> skills


    );

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> EditteacherprofileWithImage(@Part("name") RequestBody name,
                                                @Part("email") RequestBody email,
                                                @Part("user_type") RequestBody user_type,
                                                @Part("software_type") RequestBody software_type,
                                                @Part("id") RequestBody id,
                                                @Part("details") RequestBody details,
                                                @Part("stage[]") List<RequestBody> stage,
                                                @Part("skills[]") List<RequestBody> skills,
                                                @Part MultipartBody.Part image
    );

    @POST("api/create-room-chat")
    Call<SingleRoomModel> CreateChatRoom(@Body CreateRoomModel createRoomModel,
                                         @Header("Authorization") String user_token);
}
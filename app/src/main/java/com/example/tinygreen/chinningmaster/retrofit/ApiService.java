package com.example.tinygreen.chinningmaster.retrofit;

import com.example.tinygreen.chinningmaster.models.UserInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by tinygreen on 2018-05-06.
 */
public interface ApiService {

    String BASE_URL = "http://ec2-52-78-163-48.ap-northeast-2.compute.amazonaws.com:3000/";

    @POST("signin")
    Call<UserInfo> newContent(@Body UserInfo userInfo);

    //ShowArticle?article_id=1
    @GET("ShowArticle")
    Call<ResponseBody> showArticle(@Query("article_id") String id);

    @GET("GetAllArticle")
    Call<ResponseBody> getAllArticle(@Query("article_id") String id);

}

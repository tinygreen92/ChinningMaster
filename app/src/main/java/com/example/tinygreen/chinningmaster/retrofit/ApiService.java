package com.example.tinygreen.chinningmaster.retrofit;

import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by tinygreen on 2018-05-06.
 */
public interface ApiService {

    String BASE_URL = "http://ec2-54-168-66-66.ap-northeast-1.compute.amazonaws.com:3000";

    @POST("/signin")
    Call<User> newContent(@Body User user);

    @POST("/WriteArticle")
    Call<Article> writeArticle(@Body Article article);

    //ShowArticle?article_id=1
    @GET("/ShowArticle")
    Call<ResponseBody> showArticle(@Query("article_id") int id);

    @GET("/GetAllArticle")
    Call<ResponseBody> getAllArticle();

}

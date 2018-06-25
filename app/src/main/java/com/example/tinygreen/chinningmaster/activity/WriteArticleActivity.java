package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteArticleActivity extends AppCompatActivity {

    private EditText mEditArticleTitle;
    private EditText mEditArticleContent;
    /**
     * TODO : 운동 데이터 가공해서 집어넣을 것.
     */
    private Spinner mEditArticleWorkoutRecord ;
    private Button mArticleSubmit;
    private Button mArticleCancel;

    private LinearLayout myArticleLayout;

    /**
     * 레트로핏 설정
     */
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();

    private ApiService apiService = retrofit.create(ApiService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("글 쓰기");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //UI 세팅
        mEditArticleTitle = findViewById(R.id.editArticleTitle);
        mEditArticleContent = findViewById(R.id.editArticleContent);
        mEditArticleWorkoutRecord  = findViewById(R.id.editArticleWorkoutRecord);
        //
        myArticleLayout = findViewById(R.id.myArticleLayout);
        /**
         * 글 쓰기 버튼
         */
        mArticleSubmit  = findViewById(R.id.articleSubmit);
        mArticleSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setPostItem();
                Article article = new Article();
                // null 값 초기화
                article.title = null;
                article.content = null;
                article.workout_record = null;
                //
                article.title = mEditArticleTitle.getText().toString();
                article.content = mEditArticleContent.getText().toString();
                //article.workout_record = mEditArticleWorkoutRecord.getText().toString();
                article.workout_record = "empty";
                /**
                 * TODO : user_id 는 이전 Activityt에서 인텐트로 받아서 넣어야한다.
                 * 아니면 DB 오류뜸. 임시로 signin 박아넣은거 쓰셈.
                 */
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String userId = pref.getString("userName", "");

                article.user_id = userId;
                //article.time = null; DB 자동 생성?

                Call<Article> writeArticle = apiService.writeArticle(article);
                writeArticle.enqueue(new Callback<Article>() {
                    @Override
                    public void onResponse(Call<Article> call, Response<Article> response) {
                        //POST CREATE 통신에서는 Response 발생하지 않음 인데 왜!!?
                        if (response.isSuccessful()) {
                            Log.e("::::::Successful", String.valueOf(response.code()));
                            finish();
                        } else {
                            Log.e("::::::Error", String.valueOf(response.code()));
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<Article> call, Throwable t) {
                        //서버에서 Response가 안 옴.
                        //POST로 값 넘길때는 이거 실행됨.
                        Log.e("::::::Failure", t.toString());
                        finish();
                    }
                });
                //
            }
        });


        /**
         * 글쓰기 취소
         */
        mArticleCancel  = findViewById(R.id.articleCancel);
        mArticleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : alrert dialog 하나 띄우고 내용 삭제
                //
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //액션바 뒤로가기 버튼 동작
        if(id == android.R.id.home){
            //
            Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /**
     * POST로 쏘아보내기
     */
//    private void setPostItem() {
//        Article article = new Article();
//        // null 값 초기화
//        article.title = null;
//        article.content = null;
//        article.workout_record = null;
//        //
//        article.title = mEditArticleTitle.getText().toString();
//        article.content = mEditArticleContent.getText().toString();
//        article.workout_record = mEditArticleWorkoutRecord.getText().toString();
//        //쩌리값
//        article.user_id = null;
//        article.article_id = 0;
//        //article.time = null; DB 자동 생성?
//
//
//
//        Call<Article> writeArticle = apiService.writeArticle(article);
//        writeArticle.enqueue(new Callback<Article>() {
//            @Override
//            public void onResponse(Call<Article> call, Response<Article> response) {
//                //POST CREATE 통신에서는 Response 발생하지 않음
//                if (response.isSuccessful()) {
//                    Log.e("::::::Successful", "썪쎼쓰");
//                } else {
//                    Log.e("::::::Error", "에러");
//                }
//            }
//            @Override
//            public void onFailure(Call<Article> call, Throwable t) {
//                //서버에서 Response가 안 옴.
//                //POST로 값 넘길때는 이거 실행됨.
//                Log.e("::::::Failure", t.getMessage());
//            }
//        });
//
//    }



}

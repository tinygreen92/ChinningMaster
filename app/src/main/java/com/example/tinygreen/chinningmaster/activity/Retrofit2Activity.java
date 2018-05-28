package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by tinygreen on 2018-05-05.
 */
public class Retrofit2Activity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(ApiService.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    //UI setting
    private TextView mTextArticleId;
    private TextView mTextTitle;
    private TextView mTextContent;
    private TextView mTextUserId;
    private TextView mTextWorkoutRecord;
    //
    private TextView mTextReply;
    private Button mBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_article);

        mTextArticleId = findViewById(R.id.textArticleId);
        mTextTitle = findViewById(R.id.textTitle);
        mTextContent = findViewById(R.id.textContent);
        mTextUserId = findViewById(R.id.textUserId);
        mTextWorkoutRecord = findViewById(R.id.textWorkoutRecord);
        //
        mTextReply = findViewById(R.id.textReply);
        mBackButton = findViewById(R.id.backButton);

        //
        Intent intent = getIntent();

        final int position = intent.getIntExtra("position",0);

        int articleId = position;

        Call<ResponseBody> showArticle = apiService.showArticle(articleId);
        showArticle.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        Log.e(":: 인덱스 0 ::", jsonArray.get(0).toString());
                        Log.e(":: 인덱스 1 ::", jsonArray.get(1).toString());

                        // 본문, 댓글 분리 -> 배열 두겹 벗기고
                        JSONArray jsonArrayContent = new JSONArray(jsonArray.get(0).toString());
                        JSONArray jsonArrayReply = new JSONArray(jsonArray.get(1).toString());

                        JSONObject jsonObject = jsonArrayContent.getJSONObject(0);

                        mTextArticleId.setText(jsonObject.getString("article_id"));
                        mTextTitle.setText(jsonObject.getString("title"));
                        mTextUserId.setText(jsonObject.getString("user_id"));
                        mTextContent.setText(jsonObject.getString("content"));
                        mTextWorkoutRecord.setText(jsonObject.getString("workout_record"));

                        //TODO : 덧글 여러개일때 구현할 것
                        jsonObject = jsonArrayReply.getJSONObject(0);

                        String replyContent = jsonObject.getString("user_id") + " : "+jsonObject.getString("content");
                        mTextReply.setText(replyContent);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {
                    Log.e("::::::Error", "에러");
                    Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.getMessage());
                Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();

            }
        });

        mTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //back 버튼
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }



}

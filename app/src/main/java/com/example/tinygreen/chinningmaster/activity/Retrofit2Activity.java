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
        setContentView(R.layout.activity_retrofit);

        mTextArticleId = findViewById(R.id.textArticleId);
        mTextTitle = findViewById(R.id.textTitle);
        mTextContent = findViewById(R.id.textContent);
        mTextUserId = findViewById(R.id.textUserId);
        mTextWorkoutRecord = findViewById(R.id.textWorkoutRecord);
        //
        mTextReply = findViewById(R.id.textReply);
        mBackButton = findViewById(R.id.backButton);

        //클릭 게시물 받아오기
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position",0);

        int articleId = position;

        if(articleId == 0) {
            try {
                String result = "[[{\"article_id\":1,\"user_id\":\"QW4793\",\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\",\"workout_record\":\"2018년4월16일 23개 56% 10분\"}],[{\"article_id\":1,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"}]]";
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            try {
                String result = "[[{\"article_id\":1,\"user_id\":\"가즈아\",\"title\":\"님들 저 애국가 다 외움\",\"content\":\"동해 물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세 남산 위에 저 소나무 철갑을 두른 듯 바람서리 불변함은 우리 기상일세 가을 하늘 공활한데 높고 구름 없이 밝은 달은 우리 가슴 일편단심일세 이 기상과 이 맘으로 충성을 다하여 괴로우나 즐거우나 나라 사랑하세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세\",\"workout_record\":\"2018년5월16일 53개 80% 30분\"}],[{\"article_id\":1,\"user_id\":\"부럽다\",\"content\":\"대단하네요~~~~~~~~~~~~~~~~~~~\",\"time\":\"2018-05-15T23:00:32.000Z\"}]]";
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





//        mTextContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String articleId = position;
//
//                Call<ResponseBody> showArticle = apiService.showArticle(articleId);
//                showArticle.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        if (response.isSuccessful()) {
//                            Log.e("::::::Successful", "성공");
//                            Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();
//
//                            try {
//                                String result = response.body().string();
//                                JSONArray jsonArray = new JSONArray(result);
//
//                                Log.e(":: 인덱스 0 ::", jsonArray.get(0).toString());
//                                Log.e(":: 인덱스 1 ::", jsonArray.get(1).toString());
//
//                                // 본문, 댓글 분리 -> 배열 두겹 벗기고
//                                JSONArray jsonArrayContent = new JSONArray(jsonArray.get(0).toString());
//                                JSONArray jsonArrayReply = new JSONArray(jsonArray.get(1).toString());
//
//                                JSONObject jsonObject = jsonArrayContent.getJSONObject(0);
//
//                                mTextArticleId.setText(jsonObject.getString("article_id"));
//                                mTextTitle.setText(jsonObject.getString("title"));
//                                mTextUserId.setText(jsonObject.getString("user_id"));
//                                mTextContent.setText(jsonObject.getString("content"));
//                                mTextWorkoutRecord.setText(jsonObject.getString("workout_record"));
//
//                                //TODO : 덧글 여러개일때 구현할 것
//                                jsonObject = jsonArrayReply.getJSONObject(0);
//
//                                String replyContent = jsonObject.getString("user_id") + " : "+jsonObject.getString("content");
//                                mTextReply.setText(replyContent);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//
//                        } else {
//                            Log.e("::::::Error", "에러");
//                            Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Log.e("::::::Failure", t.getMessage());
//                        Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//            }
//        });

        //back 버튼
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }



}

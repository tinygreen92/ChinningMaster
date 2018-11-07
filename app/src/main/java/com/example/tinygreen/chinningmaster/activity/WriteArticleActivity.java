package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.models.Record;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);
    //
    private static ArrayList<String> userRecordTitle;
    private ArrayAdapter recordAdapter;

    /**
     * 아티클 본문
     */
    private Article article = new Article();




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

        //
        myArticleLayout = findViewById(R.id.myArticleLayout);

        /**
         * 유저의 개인 record 넣을 준비
         */
        userRecordTitle = new ArrayList<>();
        userRecordTitle.add("---운동기록을 선택해주세요---");
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        getRecord(userId);
        //ArrayAdaper 세팅
        recordAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, userRecordTitle);
        /**
         * Record spinner 스피너
         *
         */
        mEditArticleWorkoutRecord  = findViewById(R.id.editArticleWorkoutRecord);
        mEditArticleWorkoutRecord.setAdapter(recordAdapter);
        mEditArticleWorkoutRecord.setSelection(0);
        final Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
        mEditArticleWorkoutRecord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //스피너 초기값 아니라면
                if(position !=0){
                //spinner에 출력되는 값 intent로 넘김.
                intent.putExtra("userRecord",userRecordTitle.get(position));
                } else { // ---운동기록 고르시오 --- 라면
                    intent.putExtra("userRecord","운동 기록을 공개하지 않았어요");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 글 쓰기 버튼
         */
        mArticleSubmit  = findViewById(R.id.articleSubmit);
        mArticleSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //폼이 둘다 채워져 있다면 REST 실행
                if(mEditArticleTitle.getText().length() != 0 && mEditArticleContent.getText().length() != 0){
                    writeArti(intent);
                } else { //하나라도 빈다?
                    emptyDialog();
                }


//                // null 값 초기화
//                //article.article_id = 0;
//                article.title = null;
//                article.content = null;
//                article.workout_record = null;
//                article.time = null;
//                //
//                article.title = mEditArticleTitle.getText().toString();
//                article.content = mEditArticleContent.getText().toString();
//                //article.workout_record = mEditArticleWorkoutRecord.getText().toString();
//                article.workout_record = "empty";
//                /**
//                 * TODO : user_id 는 이전 Activityt에서 인텐트로 받아서 넣어야한다.
//                 * 아니면 DB 오류뜸. 임시로 signin 박아넣은거 쓰셈.
//                 */
//                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//                String userId = pref.getString("userId", "");
//                String userName = pref.getString("userName", "");
//
//                article.user_id = userId;
//                article.name = userName;
//                //article.time = null; DB 자동 생성?
//
//                Log.e("유저 인포 : ", article.user_id);
//
//                Call<Article> writeArticle = apiService.writeArticle(article);
//                writeArticle.enqueue(new Callback<Article>() {
//                    @Override
//                    public void onResponse(Call<Article> call, Response<Article> response) {
//                        //POST CREATE 통신에서는 Response 발생하지 않음 인데 왜!!?
//                        if (response.isSuccessful()) {
//                            Log.e("::::::Successful", String.valueOf(response.code()));
//                            finish();
//                        } else {
//                            Log.e("::::::Error", String.valueOf(response.code()));
//                            finish();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<Article> call, Throwable t) {
//                        //서버에서 Response가 안 옴.
//                        //POST로 값 넘길때는 이거 실행됨.
//                        Log.e("::::::Failure??", t.toString());
//                        startActivity(intent);
//                        finish();
//                        Log.e("::값 조회 : ", intent.getStringExtra("userRecord"));
//                    }
//                });
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
                startActivity(intent);
                finish();
            }
        });

    }
//    /**
//     * 입력값있다 경고
//     */
//    private void showNotEmptyDialog(){
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WriteArticleActivity.this);
//        //
//        alertDialog.setTitle("알림");
//        alertDialog.setMessage("입력된 내용이 있습니다.\n" +
//                "이전 화면으로 돌아가시겠습니까?");
//        // 확인 버튼 설정
//        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }

    /**
     * 제목/본문 비었다 경고
     */
    private void emptyDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WriteArticleActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("제목 혹은 내용을 입력해주세요.");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     *  글쓰기 rest
     */
    private void writeArti(final Intent intent){
        // null 값 초기화
        //article.article_id = 0;
        article.title = null;
        article.content = null;
        article.workout_record = null;
        article.time = null;
        //
        article.title = mEditArticleTitle.getText().toString();
        article.content = mEditArticleContent.getText().toString();
        //article.workout_record = mEditArticleWorkoutRecord.getText().toString();
        article.workout_record = intent.getStringExtra("userRecord");
        /**
         * TODO : user_id 는 이전 Activityt에서 인텐트로 받아서 넣어야한다.
         * 아니면 DB 오류뜸. 임시로 signin 박아넣은거 쓰셈.
         */
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        String userName = pref.getString("userName", "");

        article.user_id = userId;
        article.name = userName;
        //article.time = null; DB 자동 생성?

        Log.e("유저 인포 : ", article.user_id);

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
                Log.e("::::::Failure??", t.toString());
                startActivity(intent);
                finish();
                Log.e("::값 조회 : ", intent.getStringExtra("userRecord"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO : main.xml 수정하든 팝업으로 고치든
        getMenuInflater().inflate(R.menu.main_without, menu);
        return true;
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
        //super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 기록 가져오기 뿌리기
     */
    private void getRecord(String userId){
        Call<ResponseBody> getPersonalRecord = apiService.getPersonalRecord(userId);
        getPersonalRecord.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        //
                        String user_id;
                        String start_time;
                        String elapsed_time;
                        String correction_rate;
                        int count;
                        int is_shared;
                        //
                        for(int i=jsonArray.length()-1 ; i>=0 ; i-- ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            user_id = jsonObject.getString("user_id");
                            start_time = jsonObject.getString("start_time");
                            elapsed_time = jsonObject.getString("elapsed_time");
                            correction_rate = jsonObject.getString("correction_rate");
                            count = jsonObject.getInt("count");
                            is_shared = jsonObject.getInt("is_shared");
                            //
                            userRecordTitle.add(start_time+" "+elapsed_time+" "+count+"개 "+correction_rate);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("::::::Error", "에러");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.getMessage());
            }
        });
    }
//    /**
//     * POST로 쏘아보내기
//     */
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

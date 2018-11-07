package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.adapter.ReplyAdapter;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.models.Reply;
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

public class ArticleActivity extends AppCompatActivity {
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

    //본문 UI
    private TextView mTextArticleId;
    private TextView mTextTitle;
    private TextView mTextContent;
    private TextView mTextUserId;
    private TextView mTextTime;
    private TextView mTextWorkoutRecord;
    //댓글 UI
    private TextView mTextReplyUserId;
    private TextView mTextReplyContent;
    private TextView mTextReplyTime;
    //
    private TextView mReplyDelete;
    private LinearLayout mMyArticleLayout;
    //
    private EditText mEReplyWrite;
    private Button mReButton;
    //댓글
    private NestedScrollView mScroll;

    /**
     * 리싸이클러 뷰 설정
     */
    private RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<Article> myDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("정보 공유");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextTitle = findViewById(R.id.articleTitle);
        mTextContent = findViewById(R.id.articleContent);
        mTextUserId = findViewById(R.id.articleUserId);
        mTextTime = findViewById(R.id.articleTime);
        mTextWorkoutRecord = findViewById(R.id.articleWorkoutRecord);
        //
        mTextReplyUserId = findViewById(R.id.textReplyUserId);
        mTextReplyContent = findViewById(R.id.textReplyContent);
        mTextReplyTime = findViewById(R.id.textReplyTime);
        //
        mScroll = findViewById(R.id.myScroll);
        //
        mEReplyWrite =findViewById(R.id.eReplyWrite);
        mReButton = findViewById(R.id.reButton);

        /**
         * TODO : 본인 게시글이면 삭제/수정 버튼 나와라
         */
        mMyArticleLayout = findViewById(R.id.myArticleLayout);
        //GONE은 사라져서 공간도 차지 하지 않는다.
        mMyArticleLayout.setVisibility(View.GONE);

        /**
         * 리싸이클러 뷰 세팅
         */
        //리싸이클러뷰 사용
        mRecyclerView = findViewById(R.id.recyclerViewReply);
        //뷰 사이즈 고정
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        //배열 초기화
        myDataset = new ArrayList<>();
        //레이아웃 매니저 붙이고
        mRecyclerView.setLayoutManager(mLayoutManager);
        //데이터랑 연결
        mAdapter = new ReplyAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);


        //스크롤 false
        mRecyclerView.setNestedScrollingEnabled(false);



        /**
         * CommunityAct 178 라인에서 클릭한 id 얻어오기
         * 리스트뷰
         */
        Intent intent = getIntent();
        int articleId = intent.getIntExtra("position",0);


        /**
         * JSON 뿌려주기
         * TODO : 서버키면 바꿔줄 것
         */
        addItem(articleId);
        //addItem2(articleId);

        /**
         *  덧글 입력 버튼
         */
        mReButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int articleId = intent.getIntExtra("position",0);
                /**
                 * JSON 뿌려주기
                 * TODO : 서버키면 바꿔줄 것
                 */
                //게시판 id 받아서 리체킹하고 POST 하기
                reCheckReply(articleId);
                //
                //writeReply(articleId);
                //


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO : main.xml 수정하든 팝업으로 고치든
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //액션바 뒤로가기 버튼 동작
        if(id == android.R.id.home){
            Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
            startActivity(intent);
            finish();
            //onBackPressed();
            return true;
        }else if (id == R.id.action_write) {
            // TODO : 글쓰기로 바꿔라
            Intent intent = new Intent(getBaseContext(), WriteArticleActivity.class);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 서버 연결 될때
     */
    private void addItem(int articleId){
        Call<ResponseBody> showArticle = apiService.showArticle(articleId);
        showArticle.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    //Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        // 본문, 댓글 분리 -> 배열 두겹 벗기고
                        JSONArray jsonArrayContent = new JSONArray(jsonArray.get(0).toString());

                        JSONObject jsonObject = jsonArrayContent.getJSONObject(0);

                        //mTextArticleId.setText(jsonObject.getInt("article_id"));
                        mTextTitle.setText(jsonObject.getString("title"));
                        mTextUserId.setText(jsonObject.getString("name"));
                        mTextTime.setText(jsonObject.getString("time").replace("T"," ").substring(0,19));
                        mTextContent.setText(jsonObject.getString("content"));
                        mTextWorkoutRecord.setText(jsonObject.getString("workout_record"));

                        /**
                         * TODO : 댓글 리싸이클러 뷰 구현
                         */
                        //댓글있으면
                        if(jsonArray.length() != 1){
                            JSONArray jsonArrayReply = new JSONArray(jsonArray.get(1).toString());

                            int article_id;
                            String user_id;
                            String title;
                            String content;
                            String workout_record;
                            String name;
                            String time;

                            for(int i=0 ; i<jsonArrayReply.length() ; i++ ) {
                                JSONObject jsonObjectReply = jsonArrayReply.getJSONObject(i);

                                user_id = jsonObjectReply.getString("user_id");
                                content = jsonObjectReply.getString("content");
                                //TODO : 본문 말고 jsonObjectReply의 time 불러와야지
                                //time = moment.tz("2013-11-18 11:55", "Asia/Seoul");
                                time = jsonObjectReply.getString("time").replace("T"," ").substring(0,19);
                                //쩌리값 댓글이랑 본문이랑 article로 붙어있어서 이 작업해줘야함
                                name = null;
                                title = null;
                                article_id = 0;
                                workout_record = null;

                                //
                                myDataset.add(new Article(article_id, user_id, title, content, workout_record, name, time));
                            }

                            //새로고침
                            mAdapter.notifyDataSetChanged();
                            //처리 후 하단 스크롤
                            mScroll.fullScroll(View.FOCUS_DOWN);
                        }

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
    }



    /**
     * 댓글 정말 입력???
     */
    private void reCheckReply(final int value){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ArticleActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("댓글을 등록하시겠습니까?");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeReply(value);
                dialog.dismiss();
                mEReplyWrite.setText("");
            }
        });
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
        //
    }



    /**
     * 덧글 작성 메소드
     *
     */
    private void writeReply(int articleId){
        //setPostItem();
        Reply reply = new Reply();
        // null 값 초기화
        reply.article_id = articleId;
        reply.user_id = null;
        reply.content = null;
        reply.time = null;
        //
        reply.content = mEReplyWrite.getText().toString();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userName", "");
        reply.user_id = userId;

        Call<Reply> writeReply = apiService.writeReply(reply);
        writeReply.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", String.valueOf(response.code()));
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e("::::::Error", String.valueOf(response.code()));
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                //POST로 값 넘길때는 이거 실행됨.
                Log.e("::::::Failure", t.toString());
                mAdapter.notifyDataSetChanged();
                //
                myDataset.clear();
                Intent intent = getIntent();
                int articleId = intent.getIntExtra("position",0);
                addItem(articleId);
                //하단 스크롤
                mScroll.fullScroll(View.FOCUS_DOWN);


            }
        });
        //

    }

}

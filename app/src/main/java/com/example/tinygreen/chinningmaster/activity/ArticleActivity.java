package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        /**
         * TODO : article_id 본문에도 표기하나?
         */
        //mTextArticleId = findViewById(R.id.textArticleId);
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
        //mReplyDelete = findViewById(R.id.replyDelete);
        //
        mEReplyWrite =findViewById(R.id.eReplyWrite);
        mReButton = findViewById(R.id.reButton);

        /**
         * TODO : 본인 게시글이면 삭제/수정 버튼 나와라
         */
        mMyArticleLayout = findViewById(R.id.myArticleLayout);
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
                writeReply(articleId);
                mEReplyWrite.setText("");
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
                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        //Log.e(":: 인덱스 0 ::", jsonArray.get(0).toString());
                        //Log.e(":: 인덱스 1 ::", jsonArray.get(1).toString());

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

//
//    /**
//     * 테스트 메소드
//     */
//    private void addItem2(int articleId){
//
//        try {
//            String result = "[[{\"article_id\":1,\"user_id\":\"QW4793\",\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\",\"workout_record\":\"2018년4월16일 23개 56% 10분\"}],[{\"article_id\":1,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":2,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":3,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":4,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":1,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":2,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":3,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":4,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":1,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":2,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":3,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":4,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":1,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":2,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":3,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":4,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"},{\"article_id\":5,\"user_id\":\"QW4793\",\"content\":\"오 열심히 운동하셨네요 !!!\",\"time\":\"2018-04-15T23:00:32.000Z\"}]]";
//            JSONArray jsonArray = new JSONArray(result);
//
//            Log.e(":: 인덱스 0 ::", jsonArray.get(0).toString());
//            Log.e(":: 인덱스 1 ::", jsonArray.get(1).toString());
//
//            // 본문, 댓글 분리 -> 배열 두겹 벗기고
//            JSONArray jsonArrayContent = new JSONArray(jsonArray.get(0).toString());
//            JSONArray jsonArrayReply = new JSONArray(jsonArray.get(1).toString());
//
//            JSONObject jsonObject = jsonArrayContent.getJSONObject(0);
//
//            //mTextArticleId.setText(String.valueOf(jsonObject.getInt("article_id")));
//            Log.e("밸류오브",String.valueOf(jsonObject.getInt("article_id")));
//
//            mTextTitle.setText(jsonObject.getString("title"));
//            mTextUserId.setText(jsonObject.getString("user_id"));
//            mTextContent.setText(jsonObject.getString("content"));
//            mTextWorkoutRecord.setText(jsonObject.getString("workout_record"));
//
//            /**
//             * TODO : 댓글 리싸이클러 뷰 구현
//             */
//            int article_id;
//            String user_id;
//            String title;
//            String content;
//            String workout_record;
//            String time;
//
//            for(int i=0 ; i<jsonArrayReply.length() ; i++ ) {
//                JSONObject jsonObjectReply = jsonArrayReply.getJSONObject(i);
//
//                user_id = jsonObjectReply.getString("user_id");
//                content = jsonObjectReply.getString("content");
//                time = "2018/06/04 01:22:45";
//                //쩌리값
//                title = null;
//                article_id = 0;
//                workout_record = null;
//
//                //
//                myDataset.add(new Article(article_id, user_id, title, content, workout_record, time));
//
//            }
//            //리플갯수 만큼 높이 늘려줌
//            //resizeCommentList(jsonArrayReply.length());
//            //새로고침
//            mAdapter.notifyDataSetChanged();
//
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

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
            }
        });
        //

    }

}

package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.adapter.RecyclerAdapter;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommunityActivity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    //TODO : 리싸이클러뷰
    private RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<Article> myDataset;
    private Context ctx=this;

    //검색창 토글
    private boolean serachToggle = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("정보 공유");
        //뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //검색창 초기화
        TextInputLayout textInputLayout = findViewById(R.id.serachInputLayout);
        textInputLayout.setVisibility(View.GONE);

        /**
         * 플로팅 버튼 클릭 리스너
         */
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView,null,0);
                fab.hide();
            }
        });

        /**
         * 리싸이클러 뷰 세팅
         */
        //리싸이클러뷰 사용
        mRecyclerView = findViewById(R.id.my_recycler_view);
        //뷰 사이즈 고정
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        //TODO : 뷰정렬 세로가로 나중에 정하기
        //mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //배열 초기화
        myDataset = new ArrayList<>();
        //레이아웃 매니저 붙이고
        mRecyclerView.setLayoutManager(mLayoutManager);
        //데이터랑 연결
        mAdapter = new RecyclerAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * 데이터 뿌리기
         */

        addListItem2();


        /**
         * 리싸이클러뷰 스크롤리스너
         */

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FloatingActionButton fab = findViewById(R.id.fab);
                if (dy > 0) {
                    // 아래로 스크롤
                    fab.show();
                } else if (dy < 0) {
                    // 위로 스크롤
                    fab.hide();
                }
            }
        });

        /**
         * 리싸이클러뷰 아이템 클릭시
         */
        final GestureDetector gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                // 터치시 이벤트 구현
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child!=null&&gestureDetector.onTouchEvent(e)) {

                    //클릭한 위치 받아옴 - 맨 위부터 0
                    int position = rv.getChildLayoutPosition(child);
                    //yes/no
                    Toast.makeText(getApplicationContext(),position + " 번째 클릭",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getBaseContext(), Retrofit2Activity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO : main.xml 수정하든 팝업으로 고치든
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO : 글쓰기로 바꿔라

            return true;

        }else if(id == R.id.action_search){
            //검색 에딧 뷰
            // TODO : 다이얼로그 vs 반응형 검색바
            //showSearchDialog();
            showSerachEditText();
            return true;
        }else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * JSON 긁어오기
     */
    private void addListItem(){

        //String id = "1";

        //
        Call<ResponseBody> getAllArticle = apiService.getAllArticle();
        getAllArticle.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);

                        //
                        String article_id;
                        String user_id;
                        String title;
                        String content;
                        String workout_record;
                        //
                        for(int i=0 ; i<jsonArray.length() ; i++ ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            article_id = jsonObject.getString("article_id");
                            user_id = jsonObject.getString("user_id");
                            title = jsonObject.getString("title");
                            content = jsonObject.getString("content");
                            workout_record = jsonObject.getString("workout_record");

                            Log.e("::::::::", jsonArray.getJSONObject(i).toString());

                            //카드뷰 추가
                            myDataset.add(new Article(article_id, user_id, title, content, workout_record));

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

    private void addListItem2(){

        String result;
        result = "[{\"title\":\"턱걸이 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"오늘 치킨 ㄱㄱ??\",\"content\":\"운동한만큼 먹어야 될듯 ㅋㅋ\"},{\"title\":\"님들 저 애국가 다 외움\",\"content\":\"동해 물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세 남산 위에 저 소나무 철갑을 두른 듯 바람서리 불변함은 우리 기상일세 가을 하늘 공활한데 높고 구름 없이 밝은 달은 우리 가슴 일편단심일세 이 기상과 이 맘으로 충성을 다하여 괴로우나 즐거우나 나라 사랑하세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세\"},{\"title\":\"절대 광고 아니에요 들어오세요\",\"content\":\"♚♚히어로즈 오브 더 스☆톰♚♚가입시$$전원 카드팩☜☜뒷면100%증정※ ♜월드오브 워크래프트♜펫 무료증정￥ 특정조건 §§디아블로3§§★공허의유산★초상화♜오버워치♜겐지스킨￥획득기회@@@ 즉시이동http://kr.battle.net/heroes/ko/\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"},{\"title\":\"스쿼트 10개 했어요 !!!\",\"content\":\"오랜만에 운동했더니 힘드네요..ㅠㅠㅠ\"},{\"title\":\"철봉 10개하기 진짜 힘드네요;;;\",\"content\":\"이제 꾸준하게 운동 해야할듯..ㅠㅠㅠ\"}]";

        try {
            JSONArray jsonArray = new JSONArray(result);
            //
            String article_id;
            String user_id;
            String title;
            String content;
            String workout_record;
            //
            for(int i=0 ; i<jsonArray.length() ; i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                article_id = "article_id";
                user_id = "user_id";
                title = jsonObject.getString("title");
                content = jsonObject.getString("content");
                workout_record = "workout_record";

                Log.e("::::::::", jsonArray.getJSONObject(i).toString());

                //데이터셋 추가
                myDataset.add(new Article(article_id, user_id, title, content, workout_record));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //새로고침
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 검색 다이얼로그
     */
    private void showSearchDialog(){
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(CommunityActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_serach,null);
        alerDialog.setView(view);
        //
        final EditText editText = view.findViewById(R.id.edittextSerach);
        //
        //alerDialog.setTitle("검색");

        final AlertDialog dialog = alerDialog.create();

        // 확인 버튼 설정
        alerDialog.setPositiveButton("검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(":::::", editText.getText().toString());
                dialog.dismiss();
            }
        });
        // 확인 버튼 설정
        alerDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(":::::", "Yes Btn Click");
                dialog.dismiss();
            }
        });

        alerDialog.show();
    }

    /**
     * 검색 버튼 누르면 껏다 켰다
     */
    private void showSerachEditText(){
        TextInputLayout textInputLayout = findViewById(R.id.serachInputLayout);
        // TODO : 애니메이션 해결
        if(serachToggle){
            textInputLayout.setVisibility(View.VISIBLE);
            serachToggle =!serachToggle;
        }else{
            textInputLayout.setVisibility(View.GONE);
            serachToggle =!serachToggle;
        }


    }

}

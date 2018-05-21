package com.example.tinygreen.chinningmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("정보공유");

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

//        //임시 데이터 10개
//        for(int i = 1; i<11; i++){
//            addListItem(i);
//        }

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 테스트용 메소드
     */

    private void addListItem(int position){

        String article_id;
        String user_id;
        String title;
        String content;
        String workout_record;
        //
        article_id = Integer.toString(position);
        user_id="유저아이디";
        title="타이틀";
        content="본문 내용";
        workout_record="운동기록";

        myDataset.add(new Article(article_id, user_id, title, content, workout_record));
        //새로고침
        mAdapter.notifyDataSetChanged();
    }

    /**
     * JSON 긁어오기
     */
    private void addListItem3(){

        String id = "1";

        //
        Call<ResponseBody> getAllArticle = apiService.getAllArticle(id);
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

                            //카드뷰 추가
                            myDataset.add(new Article(article_id, user_id, title, content, workout_record));
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


        //새로고침
        mAdapter.notifyDataSetChanged();
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


}

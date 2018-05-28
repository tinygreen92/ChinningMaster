package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.adapter.RecyclerAdapter;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    //검색창 관련
    private Spinner mSpinner;
    private LinearLayout mTextInputLayout;
    private EditText mEditText;
    private static ArrayList<Article> searchDataset;
    private boolean mSearchToggle = true;
    private String spinnerPosition;


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
        LinearLayout textInputLayout = findViewById(R.id.searchInputLayout);
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
         * TODO : 서버 켤때 바꿔라
         */

        //addListItem();
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

                    Intent intent = new Intent(getBaseContext(), ArticleActivity.class);
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

        /**
         * 검색 기능 한글자 타이핑마다 갱신
         */
        mEditText = findViewById(R.id.articleSearch);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //검색어 입력 후 동작
                String inputText = mEditText.getText().toString();
                search(inputText);

            }
        });
        /**
         * 스피너 셀렉트리스너 구현
         */
        mSpinner = findViewById(R.id.searchSpinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //valuse/array.xml 참조
                spinnerPosition = parent.getItemAtPosition(position).toString();
                //스피너 아이템 클릭할 때도 검색 갱신해야하니까
                String inputText = mEditText.getText().toString();
                search(inputText);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    /**
     * 오른쪽 옵션 메뉴 클릭시 동작
     */
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
            showSearchEditText();
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
                        String time;
                        //
                        for(int i=0 ; i<jsonArray.length() ; i++ ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            article_id = jsonObject.getString("article_id");
                            user_id = jsonObject.getString("user_id");
                            title = jsonObject.getString("title");
                            content = jsonObject.getString("content");
                            workout_record = jsonObject.getString("workout_record");
                            time = jsonObject.getString("time");

                            Log.e("::::::::", jsonArray.getJSONObject(i).toString());

                            //카드뷰 추가
                            myDataset.add(new Article(article_id, user_id, title, content, workout_record, time));
                            //검색을 위한 데이터 복붙
                            searchDataset = new ArrayList<>();
                            searchDataset.addAll(myDataset);
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
    //TODO : TEST UNIT : 서버연결할때 삭제할 것
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
            String time;
            //
            for(int i=0 ; i<jsonArray.length() ; i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                article_id = String.valueOf(jsonArray.length()-i) ;
                user_id = "작성자";
                title = jsonObject.getString("title");
                content = jsonObject.getString("content");
                workout_record = "workout_record";
                time = getTime();

                //데이터셋 추가
                myDataset.add(new Article(article_id, user_id, title, content, workout_record, time));
                //검색을 위한 데이터 복붙
                searchDataset = new ArrayList<>();
                searchDataset.addAll(myDataset);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //새로고침
        mAdapter.notifyDataSetChanged();
    }

    /**
     * TODO : AlertDialog 사용하는 건데 사용안하고 보류
     * 검색 다이얼로그
     */
    private void showSearchDialog(){
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(CommunityActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_search,null);
        alerDialog.setView(view);
        //
        final EditText editText = view.findViewById(R.id.edittextSearch);
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
        // 취소 버튼 설정
        alerDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(":::::", "cancel Btn Click");
                dialog.dismiss();
            }
        });

        alerDialog.show();
    }

    /**
     * 검색 버튼 누르면 껏다 켰다
     */
    private void showSearchEditText(){
        mTextInputLayout = findViewById(R.id.searchInputLayout);
        // TODO : 애니메이션 해결
        if(mSearchToggle){
            mTextInputLayout.setVisibility(View.VISIBLE);
            mSearchToggle =!mSearchToggle;
        }else{
            mTextInputLayout.setVisibility(View.GONE);
            mSearchToggle =!mSearchToggle;
        }


    }

    private void search(String text){
        //본문 내용 초기화
        myDataset.clear();
        //입력 없으면 모든 내용 출력
        if(text.length() == 0){
            myDataset.addAll(searchDataset);
        }else{
            //본래 리스트 몽땅 검색
            for(int i =0; i< searchDataset.size(); i++){
                // TODO : case 문 돌려
                switch (spinnerPosition){
                    default:
                        //제목 + 내용 검색
                        if(searchDataset.get(i).title.toLowerCase().contains(text) || searchDataset.get(i).content.toLowerCase().contains(text)){
                            myDataset.add(searchDataset.get(i));
                        }
                        break;

                    case "제목 + 내용":
                        //제목 + 내용 검색
                        if(searchDataset.get(i).title.toLowerCase().contains(text) || searchDataset.get(i).content.toLowerCase().contains(text)){
                            myDataset.add(searchDataset.get(i));
                        }
                        break;
                    case "제목":
                        //제목 검색
                        if(searchDataset.get(i).title.toLowerCase().contains(text)){
                            //본문 + 내용
                            myDataset.add(searchDataset.get(i));
                        }
                        break;
                    case "내용":
                        //내용 검색
                        if(searchDataset.get(i).content.toLowerCase().contains(text)){
                            //본문 + 내용
                            myDataset.add(searchDataset.get(i));
                        }
                        break;
                    case "작성자":
                        //작성 검색
                        if(searchDataset.get(i).user_id.toLowerCase().contains(text)){
                            //본문 + 내용
                            myDataset.add(searchDataset.get(i));
                        }
                        break;
                }

            }
        }
        //어댑터 갱신
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 시간 연월일만 가져와
     */
    private String getTime(){
        Long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

        return mFormat.format(mDate);
    }


}

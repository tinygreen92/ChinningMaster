package com.example.tinygreen.chinningmaster.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.adapter.RankingAdapter;
import com.example.tinygreen.chinningmaster.adapter.RecyclerAdapter;
import com.example.tinygreen.chinningmaster.models.Article;
import com.example.tinygreen.chinningmaster.models.Record;
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

public class RankingActivity extends AppCompatActivity {
    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    /**
     * 리싸이클러 뷰 설정
     */
    private RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<Record> myDataset;

    //
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        getSupportActionBar().setTitle("전체 사용자 기록");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 리싸이클러 뷰 세팅
         */
        //리싸이클러뷰 사용
        mRecyclerView = findViewById(R.id.my_ranking_view);
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
        mAdapter = new RankingAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * 데이터 뿌리기
         * TODO : 서버 켤때 바꿔라
         */

        addListItem();
        //addListItem2();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //액션바 뒤로가기 버튼 동작
        if(id == android.R.id.home){
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
        Call<ResponseBody> getAllUserRecord = apiService.getAllUserRecord();
        getAllUserRecord.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {

                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        // JOIN 해서 얻어온 값 user_id = name
                        String user_id;
                        String start_time;
                        String elapsed_time;
                        String correction_rate;
                        int record_id;
                        int count;
                        int is_shared;
                        //
                        for(int i=0 ; i<jsonArray.length() ; i++ ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.e(":::츄라이:::", jsonObject.toString());

                            start_time = null;
                            is_shared = 0;
                            //
                            elapsed_time = jsonObject.getString("elapsed_time");
                            correction_rate = jsonObject.getString("correction_rate");
                            record_id = jsonObject.getInt("record_id");
                            count = jsonObject.getInt("count");
                            user_id = jsonObject.getString("name");

                            Log.e(":::츄라이::::", jsonArray.getJSONObject(i).toString());

                            //카드뷰 추가
                            myDataset.add(new Record(user_id, start_time, elapsed_time, correction_rate, record_id, count, is_shared));

                        }
                        //새로고침
                        mAdapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        Log.e(":::IOE:::", "머임??");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.e(":::JSON:::", "머임??");
                        e.printStackTrace();
                    }



                } else {
                    Log.e("::::::Error", "에러");
                    Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.toString());
                //Log.e("::::::Failure", t.getMessage());
                Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();

            }
        });
    }

    //TODO : TEST UNIT : 서버연결할때 삭제할 것
    private void addListItem2(){

        String result;
        result = "[{\"user_id\":\"QW4793\",\"count\":20,\"start_time\":\"2018년4월16일 15:30\",\"elapsed_time\":\"03:36\",\"correction_rate\":\"70%\",\"is_shared\":0},{\"user_id\":\"QW1123\",\"count\":20,\"start_time\":\"2018년4월16일 16:30\",\"elapsed_time\":\"07:36\",\"correction_rate\":\"45%\",\"is_shared\":0}]";

        try {
            JSONArray jsonArray = new JSONArray(result);
            //
            String user_id;
            String start_time;
            String elapsed_time;
            String correction_rate;
            int record_id;
            int count;
            int is_shared;
            //
            for(int i=0 ; i<jsonArray.length() ; i++ ){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                user_id = jsonObject.getString("user_id");
                start_time = jsonObject.getString("start_time");
                elapsed_time = jsonObject.getString("elapsed_time");
                correction_rate = jsonObject.getString("correction_rate");
                record_id = jsonObject.getInt("record_id");
                count = jsonObject.getInt("count");
                is_shared = jsonObject.getInt("is_shared");

                //데이터셋 추가
                myDataset.add(new Record(user_id, start_time, elapsed_time, correction_rate, record_id, count, is_shared));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //새로고침
        mAdapter.notifyDataSetChanged();
    }


}

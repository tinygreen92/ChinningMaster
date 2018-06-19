package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    // UI references.
    /**
     * TODO: 세팅 버튼 추가해야 댐
     */
    private ImageView mImageView; // 프사
    private ImageView mSettingBtn; // <- xml 에 settingImageView
    private TextView mUserHelloTv; // [USERNAME]님 환영합니다.
    private TextView mBMIVTv; // BMI 수치 ~~ 입니다.
    // 메인 버튼
    private TextView mWorkoutRecordTv;
    private TextView mRankingTv;
    private TextView mCommunityTv;
    //뒤로가기 두번 눌러 종료 시간
    private long backKeyPressedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * LoginAct 에서 인텐트 받아오기
         */
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        getUserName(userId);


//        /**
//         * 상단 표시 데이터
//         */
//
//        //mImageView = findViewById(R.id.imageView);
//
//        mUserHelloTv = findViewById(R.id.user_hello);
//        mUserHelloTv.setText("["+USERNAME+"]님 환영합니다!");


        /**
         * 세팅 페이지 테스트
         */

        mSettingBtn = findViewById(R.id.settingImageView);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                //intent.putExtra("userName",USERNAME);
                startActivity(intent);
            }
        });


        /**
         * 나의 운동기록 페이지
         */
        mWorkoutRecordTv = findViewById(R.id.myWorkTv);
        mWorkoutRecordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MyWorkActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 전체 사용자 기록
         */
        mRankingTv = findViewById(R.id.rankTv);
        mRankingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RankingActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 커뮤니티 버튼 누르면 레트로핏 테스트 페이지
         */
        mCommunityTv = findViewById(R.id.communityTv);
        mCommunityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CommunityActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            //toast.cancel();
            //백그라운드로 보내고
            moveTaskToBack(true);
            finish();
            //프로세스 킬
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 리스트 뿌리기
     */
    private void getUserName(String userId){
        Call<ResponseBody> enterPersonalInfo = apiService.enterPersonalInfo(userId);
        enterPersonalInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "메인 화면 성공");
//                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();
                    try {
                        String result = response.body().string();

                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String userName = jsonObject.getString("name");
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("userName", userName);
                        editor.commit();

                        /**
                         * 상단 표시 데이터
                         */

                        //mImageView = findViewById(R.id.imageView);

                        mUserHelloTv = findViewById(R.id.user_hello);
                        mUserHelloTv.setText("["+userName+"]님 환영합니다!");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("::::::Error", "에러");
                    Log.e("::::::코드", String.valueOf(response.code()));
//                    Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

}

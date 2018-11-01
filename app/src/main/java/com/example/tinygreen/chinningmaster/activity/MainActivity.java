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
         * SharedPreferences 에서 유저아이디 가져와서 retrofit 호출
         */
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        //TODO : 서버 켜지면 스위치
        getUserName(userId);
        //getUserName2(userId);


//        /**
//         * 상단 표시 데이터
//         */
//
//        //mImageView = findViewById(R.id.imageView);
//
//        mUserHelloTv = findViewById(R.id.user_hello);
//        mUserHelloTv.setText("["+USERNAME+"]님 환영합니다!");


        /**
         * 세팅 페이지
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
         * 커뮤니티 버튼
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
//    /**
//     * TODO : 더미 데이터 지울 것
//     */
//    private void getUserName2(String userId){
//        try {
//            String result = "[\n" +
//                    "  {\n" +
//                    "    \"user_id\": \"qwer1234\",\n" +
//                    "    \"user_pw\": \"qwer1234!\",\n" +
//                    "    \"birth_date\": 930414,\n" +
//                    "    \"name\": 짱짱맨,\n" +
//                    "    \"height\": 173,\n" +
//                    "    \"weight\": 78\n" +
//                    "  }\n" +
//                    "]";
//
//            JSONArray jsonArray = new JSONArray(result);
//            JSONObject jsonObject = jsonArray.getJSONObject(0);
//
//            String userName = jsonObject.getString("name");
//            int birth_date = jsonObject.getInt("birth_date");
//            int height = jsonObject.getInt("height");
//            int weight = jsonObject.getInt("weight");
//            //
//            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("userName", userName);
//            editor.putInt("birth_date", birth_date);
//            editor.putInt("height", height);
//            editor.putInt("weight", weight);
//            editor.commit();
//
//            /**
//             * 상단 표시 데이터
//             */
//
//            mBMIVTv = findViewById(R.id.BMI_textView);
//            mBMIVTv.setText(bmiLogic(height,weight));
//
//            mUserHelloTv = findViewById(R.id.user_hello);
//            mUserHelloTv.setText("["+userName+"]님 환영합니다!");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
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
                        int birth_date = jsonObject.getInt("birth_date");
                        int height = jsonObject.getInt("height");
                        int weight = jsonObject.getInt("weight");
                        //
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("userName", userName);
                        editor.putInt("birth_date", birth_date);
                        editor.putInt("height", height);
                        editor.putInt("weight", weight);
                        editor.commit();

                        /**
                         * 상단 표시 데이터
                         */

                        mBMIVTv = findViewById(R.id.BMI_textView);
                        mBMIVTv.setText(bmiLogic(height,weight));

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

    /**
     * BMI 수치 계산 메소드
     */
    private String bmiLogic(int height, int weight){
        //BMI(Body Mass Index) = 체중을 자기 키의 제곱으로 나눈 값.
        float h = (float) 100.0;
        float fHeight = height/h;
        float result = weight/(fHeight * fHeight);
        String sResult = "BMI 수치 : " + String.format("%.1f", result);
        String textStus;
        //
        if(result < 18.5){
            textStus = "(저체중)";
        }else if(result >= 18.50 && result < 23.0){
            textStus = "(표준체중)";
        }else if(result >= 23.0 && result < 25.0){
            textStus = "(과체중 경고)";
        }else if(result >= 25.0 && result < 27.0){
            textStus = "(과체중)";
        }else if(result >= 27.0 && result < 30.0){
            textStus = "(경도비만)";
        }else if(result >= 30.0 && result < 35.0){
            textStus = "(고도비만)";
        }else if(result >= 35.0){
            textStus = "(초고도비만)";
        }else return "()";


        return sResult + textStus;

    }

}

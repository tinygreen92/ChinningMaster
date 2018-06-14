package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;

public class MainActivity extends AppCompatActivity {

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
    // 인텐트로 넘겨오는 유저 닉네임
    private String USERNAME;
    //뒤로가기 두번 눌러 종료 시간
    private long backKeyPressedTime = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * LoginAct 에서 인텐트 받아오기
         */
        Intent intent = getIntent();
        USERNAME = intent.getStringExtra("USERNAME");

        //mImageView = findViewById(R.id.imageView);

        mUserHelloTv = findViewById(R.id.user_hello);
        mUserHelloTv.setText("["+USERNAME+"]님 환영합니다!");


        /**
         * 세팅 페이지 테스트
         */

        mSettingBtn = findViewById(R.id.settingImageView);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                intent.putExtra("USERNAME",USERNAME);
                startActivity(intent);
            }
        });

        /**
         * 나의 운동기록 페이지
         */
        mWorkoutRecordTv = findViewById(R.id.textView2);
        mWorkoutRecordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MyWorkActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 커뮤니티 버튼 누르면 레트로핏 테스트 페이지
         */
        mCommunityTv = findViewById(R.id.textView4);
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

}

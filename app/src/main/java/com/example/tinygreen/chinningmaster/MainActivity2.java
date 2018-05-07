package com.example.tinygreen.chinningmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    // UI references.
    /**
     * TODO: 세팅 버튼 추가해야 댐
     */
    private ImageView mImageView; // 프사
    private Button mSettingBtn; // <- xml 에 추가 필요
    private TextView mUserHelloTv; // [USERNAME]님 환영합니다.
    private TextView mBMIVTv; // BMI 수치 ~~ 입니다.
    //
    private TextView mUndongTv;
    private TextView mCommunityTv;
    private TextView mWriteTv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        /**
         * LoginAct 에서 인텐트 받아오기
         */
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USERNAME");

        mImageView = findViewById(R.id.imageView);

        mUserHelloTv = findViewById(R.id.user_hello);
        mUserHelloTv.setText("["+userName+"]님 환영합니다!");


        /**
         * 세팅 페이지 테스트
         */

        mCommunityTv = findViewById(R.id.textView4);
        mCommunityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Retrofit2Activity.class);
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
                Intent intent = new Intent(getBaseContext(), Retrofit2Activity.class);
                startActivity(intent);
            }
        });

    }
}

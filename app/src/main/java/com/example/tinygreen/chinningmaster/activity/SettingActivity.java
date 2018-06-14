package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;

public class SettingActivity extends AppCompatActivity {

    private TextView mEditUserInfo;
    private TextView mIsShared;
    private TextView mAutoLogin;
    private TextView mLogOut;

    private Switch mAutoLoginSwitch;
    private boolean loginAutoChecked = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("정보 수정");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //SharedPreferences : autoLogin
        SharedPreferences autoLogin = getSharedPreferences("autoLogin", getBaseContext().MODE_PRIVATE);
        //SharedPreferences 읽어와서 뿌리기

        if(autoLogin.getAll().toString() != "{}"){
            loginAutoChecked = autoLogin.getBoolean("autoLogin",true);
            mAutoLoginSwitch = findViewById(R.id.autoLoginSwitch);
            mAutoLoginSwitch.setChecked(loginAutoChecked);
        }

        Log.e("::머임::", String.valueOf(loginAutoChecked));
        Log.e("::머임??::", autoLogin.getAll().toString());


        /**
         * 개인정보 수정
         */
        mEditUserInfo = findViewById(R.id.editUserInfo);
        mEditUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * 기록 공유
         */
        mIsShared = findViewById(R.id.isShared);
        mIsShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * 자동 로그인
         */
        mAutoLogin = findViewById(R.id.autoLogin);
        mAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //토글
                loginAutoChecked = !loginAutoChecked;
                //
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", getBaseContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("autoLogin", loginAutoChecked);
                editor.commit();
                //
                mAutoLoginSwitch.setChecked(loginAutoChecked);
                //
                Toast.makeText(SettingActivity.this, "체크상태 = " + loginAutoChecked, Toast.LENGTH_SHORT).show();
            }
        });
        // 스위치
        mAutoLoginSwitch = findViewById(R.id.autoLoginSwitch);
        mAutoLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loginAutoChecked = isChecked;
                //
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", getBaseContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("autoLogin", loginAutoChecked);
                editor.commit();
                //
                mAutoLoginSwitch.setChecked(loginAutoChecked);
                Toast.makeText(SettingActivity.this, "체크상태 = " + loginAutoChecked, Toast.LENGTH_SHORT).show();
            }
        });


        /**
         * 로그아웃
         */
        mLogOut = findViewById(R.id.logout);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * SharedPreferences 내용 삭제후 초기 화면으로
                 */
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                //
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", getBaseContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                //
                editor.clear();
                editor.commit();
                //
                Toast.makeText(SettingActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                //
                finish();

            }
        });




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
}

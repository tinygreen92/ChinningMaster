package com.example.tinygreen.chinningmaster.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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
    private CheckBox mCheckBox;
    private Switch mAutoLoginSwitch;
    private boolean loginAutoChecked = true;
    private boolean sharedRecord = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("정보 수정");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //SharedPreferences : autoLogin
        SharedPreferences autoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
        //SharedPreferences 읽어와서 뿌리기

        if(autoLogin.getAll().toString() != "{}"){
            loginAutoChecked = autoLogin.getBoolean("autoLogin",true);
            mAutoLoginSwitch = findViewById(R.id.autoLoginSwitch);
            mAutoLoginSwitch.setChecked(loginAutoChecked);
            //
            sharedRecord = autoLogin.getBoolean("recordShare",true);
            mCheckBox = findViewById(R.id.checkBox);
            mCheckBox.setChecked(sharedRecord);
        }



        /**
         * 개인정보 수정
         */
        mEditUserInfo = findViewById(R.id.editUserInfo);
        mEditUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EditUserInfoActivity.class);
                startActivity(intent);

            }
        });

        /**
         * 기록 공유
         */
        mIsShared = findViewById(R.id.isShared);
        mIsShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //토글
                sharedRecord = !sharedRecord;
                //
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("recordShare", sharedRecord);
                editor.commit();
                //
                mCheckBox.setChecked(sharedRecord);
                //
                if(sharedRecord){
                    Toast.makeText(SettingActivity.this, "운동 기록을 공개합니다.", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(SettingActivity.this, "운동 기록을 공개하지 않습니다.", Toast.LENGTH_SHORT).show();

            }
        });
        //쳌 박스
        mCheckBox = findViewById(R.id.checkBox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckBox(isChecked);
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
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                editor.putBoolean("autoLogin", loginAutoChecked);
                editor.commit();
                //
                mAutoLoginSwitch.setChecked(loginAutoChecked);
                //
                if(loginAutoChecked){
                    Toast.makeText(SettingActivity.this, "자동 로그인 기능을 사용합니다.", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(SettingActivity.this, "자동 로그인 기능을 해제합니다.", Toast.LENGTH_SHORT).show();
            }
        });
        // 스위치
        mAutoLoginSwitch = findViewById(R.id.autoLoginSwitch);
        mAutoLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //SharedPreferences put
                putStringAutoLogin(isChecked);
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
                //
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", getBaseContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLogin.edit();
                //
                editor.clear();
                editor.commit();
                //
                SharedPreferences pref = getSharedPreferences("pref", getBaseContext().MODE_PRIVATE);
                editor = pref.edit();
                //
                editor.clear();
                editor.commit();
                //
                Toast.makeText(SettingActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                //
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
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

    /**
     * 자동 로그인 세팅
     */
    private void putStringAutoLogin(boolean isChecked){
        loginAutoChecked = isChecked;
        //
        SharedPreferences autoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLogin.edit();
        editor.putBoolean("autoLogin", loginAutoChecked);
        editor.commit();
        //
        mAutoLoginSwitch.setChecked(loginAutoChecked);
        if(loginAutoChecked){
            Toast.makeText(SettingActivity.this, "자동 로그인 기능을 사용합니다.", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(SettingActivity.this, "자동 로그인 기능을 해제합니다.", Toast.LENGTH_SHORT).show();

    }

    /**
     * 기록 공유 온 오프
     */
    private void setCheckBox(boolean isChecked){
        sharedRecord = isChecked;
        //
        SharedPreferences autoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLogin.edit();
        editor.putBoolean("recordShare", sharedRecord);
        editor.commit();
        //
        mCheckBox.setChecked(sharedRecord);
        if(sharedRecord){
            Toast.makeText(SettingActivity.this, "운동 기록을 공개합니다.", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(SettingActivity.this, "운동 기록을 공개하지 않습니다.", Toast.LENGTH_SHORT).show();
    }
}

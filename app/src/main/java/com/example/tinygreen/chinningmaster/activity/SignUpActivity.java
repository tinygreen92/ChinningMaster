package com.example.tinygreen.chinningmaster.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.UserInfo;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    // UI references.
    private AutoCompleteTextView mLoginId;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mKinectNum;
    private EditText mResidentNum;
    private EditText mResidentNumTail;
    private EditText mHeight;
    private EditText mWeight;
    // Button
    private Button mBackButton;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mLoginId = findViewById(R.id.login_id);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.password_confirm);
        mKinectNum = findViewById(R.id.kinect_num);
        mResidentNum = findViewById(R.id.resident_num);
        mResidentNumTail = findViewById(R.id.resident_num_tail);
        mHeight = findViewById(R.id.height);
        mWeight = findViewById(R.id.weight);



        /**
         * 백버튼 누를때
         */
        mBackButton = findViewById(R.id.sign_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * 서밋 버튼
         */
        mSubmitButton = findViewById(R.id.sign_in_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfo userInfo = new UserInfo();
                // null 값 초기화
                userInfo.user_id = null;
                userInfo.user_pw = null;
                userInfo.birth_date = null;
                userInfo.height = null;
                userInfo.weight = null;
                //
                userInfo.user_id = mLoginId.getText().toString();
                userInfo.user_pw = mPassword.getText().toString();
                userInfo.birth_date = mResidentNum.getText().toString();
                userInfo.height = mHeight.getText().toString();
                userInfo.weight = mWeight.getText().toString();


                Call<UserInfo> signInfo = apiService.newContent(userInfo);
                signInfo.enqueue(new Callback<UserInfo>() {
                    @Override
                    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        //POST CREATE 통신에서는 Response 발생하지 않음
                        if (response.isSuccessful()) {
                            Log.e("::::::Successful", "썪쎼쓰");
                        } else {
                            Log.e("::::::Error", "에러");
                        }
                    }
                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        //서버에서 Response가 안 옴.
                        //POST로 값 넘길때는 이거 실행됨.
                        Log.e("::::::Failure", t.getMessage());
                    }
                });
            }
        });
    }
}

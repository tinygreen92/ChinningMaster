package com.example.tinygreen.chinningmaster.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.User;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
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
        setContentView(R.layout.activity_sign_in);

        mLoginId = findViewById(R.id.editLogin_id);
        mPassword = findViewById(R.id.editPassword);
        mPasswordConfirm = findViewById(R.id.editPassword_confirm);
        mKinectNum = findViewById(R.id.editKinect_num);
        mResidentNum = findViewById(R.id.editResident_num);
        mResidentNumTail = findViewById(R.id.editResident_num_tail);
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

                User user = new User();
                // null 값 초기화
                user.user_id = null;
                user.user_pw = null;
                user.name = null;
                user.birth_date = 0;
                user.height = 0;
                user.weight = 0;
                // TODO : 닉네임 칸 뚫을 것
                user.user_id = mLoginId.getText().toString();
                user.user_pw = mPassword.getText().toString();
                //user.name = mLoginId.getText().toString();
                user.name = "Test value";
                user.birth_date = Integer.parseInt(mResidentNum.getText().toString());
                user.height = Integer.parseInt(mHeight.getText().toString());
                user.weight = Integer.parseInt(mWeight.getText().toString());

                Log.e("유저 인포", user.user_id);

                Call<User> signInfo = apiService.newContent(user);
                signInfo.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        //POST CREATE 통신에서는 Response 발생하지 않음
                        if (response.isSuccessful()) {
                            Log.e("::::::Successful", String.valueOf(response.code()));
                        } else {
                            Log.e("::::::Error", String.valueOf(response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        //서버에서 Response가 안 옴.
                        //POST로 값 넘길때는 이거 실행됨.
                        Log.e("::::::Failure", "POST 넘어감");
                        Log.e("머임??",t.toString());

                    }
                });
            }
        });
    }
}

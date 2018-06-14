package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.User;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    //private Button mBackButton;
    private Button mSubmitButton;
    //
    private User userInfo = new User();
    private boolean isPass;

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


//        /**
//         * 백버튼 누를때
//         */
//        mBackButton = findViewById(R.id.sign_back_button);
//        mBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        /**
         * 서밋 버튼
         */
        mSubmitButton = findViewById(R.id.sign_in_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = mLoginId.getText().toString();
                String user_pw = mPassword.getText().toString();
                //
                showDialog();

//                //유효성 검사
//                if(checkValidity(user_id,user_pw)){
//                    //id/pw 검사랑 중복검사 true 면
//                    //JSON post
//                    retrofitPost(user_id,user_pw);
//                }

            }
        });
    }

    /**
     * id/pw 유효성 검사
     */
    private boolean checkValidity(String id, String pw){
        //불린값 초기화
        isPass = true;
        //아이디 + 패스워드 규칙
        if(!Pattern.matches("^[a-zA-Z0-9]*$", id)) {
            isPass = false;
            Toast.makeText(this,"ID는 영문 대, 소문자와 숫자만 사용 가능합니다.",Toast.LENGTH_SHORT).show();
        } else if(!Pattern.matches("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~`#?!@$%^&*-]).{6,}$", pw)) {
            isPass = false;
            Toast.makeText(this,"PW에 6자 이상의 영문 소문자, 숫자, 특수문자(~`#?!@$%^&*)를 포함하세요.",Toast.LENGTH_SHORT).show();
        }
        //아이디 중복체크
        if(isPass){
            //통과하면
            idDoubleCheck(id,pw);
        }

        return isPass;
    }
    /**
     * 아이디 중복 체크
     */
    private void idDoubleCheck(String id, String pw){

        Call<ResponseBody> checkLogin = apiService.checkLogin(id, pw);
        checkLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    if(response.body().toString()=="true"){
                        isPass = false;
                        Toast.makeText(SignInActivity.this,"이미 사용중인 ID입니다.",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(SignInActivity.this,"사용할 수 있는 ID입니다.",Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Log.e("::::::Error", "에러");

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.toString());

            }
        });
    }

    private void showDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("입력된 내용이 있습니다.\n" +
                "이전 화면으로 돌아가시겠습니까?");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.v(":::::", editText.getText().toString());
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * POST 형식으로 보내
     */
    private void retrofitPost(String id, String pw){

        //값 초기화
        userInfo.name = null;
        userInfo.birth_date = 0;
        userInfo.height = 0;
        userInfo.weight = 0;

        // 값 넣기
        userInfo.user_id = id;
        userInfo.user_pw = pw;
        userInfo.name = "Test value";
        userInfo.birth_date = Integer.parseInt(mResidentNum.getText().toString());
        userInfo.height = Integer.parseInt(mHeight.getText().toString());
        userInfo.weight = Integer.parseInt(mWeight.getText().toString());

        Log.e("유저 인포", userInfo.user_id);

        Call<User> signInfo = apiService.newContent(userInfo);
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
                Log.e("::::에러코드??",t.toString());

            }
        });
    }

}

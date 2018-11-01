package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
//    OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            .connectTimeout(1, TimeUnit.MINUTES)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build();
//
//    private Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(ApiService.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build();


    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ApiService apiService = retrofit.create(ApiService.class);

    // UI references.
    private AutoCompleteTextView mLoginId;
    private AutoCompleteTextView mLogin_name;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mResidentNum;
    private EditText mResidentNumTail;
    private EditText mHeight;
    private EditText mWeight;
    // Button
    private Button mIdCheakBtn;
    private Button mNameCheakBtn;
    private Button mSubmitButton;
    //
    private User userInfo = new User();
    private boolean isPass;
    private boolean isIdCheakPass;
    private boolean isNameCheakOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mLoginId = findViewById(R.id.eLogin_id);
        mLogin_name = findViewById(R.id.eLogin_name);
        mPassword = findViewById(R.id.ePassword);
        mPasswordConfirm = findViewById(R.id.ePassword_confirm);
        mResidentNum = findViewById(R.id.eResident_num);
        mResidentNumTail = findViewById(R.id.eResident_num_tail);
        mHeight = findViewById(R.id.height);
        mWeight = findViewById(R.id.weight);

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

                //유효성 검사
                if(checkValidity(user_id,user_pw)){
                    //id/pw 검사랑 중복검사 true 면
                    //JSON post
                    if(isNameCheakOk){ //if 중복체크 버튼을 이미 눌렀을 때

                        if(
                                mLoginId.getText().length() != 0 &&
                                mLogin_name.getText().length() != 0 &&
                                mPassword.getText().length() != 0 &&
                                mPasswordConfirm.getText().length() != 0 &&
                                mResidentNum.getText().length() != 0 &&
                                mResidentNumTail.getText().length() != 0 &&
                                mHeight.getText().length() != 0 &&
                                mWeight.getText().length() != 0 ) {

                            retrofitPost(user_id,user_pw);
                            finish();
                        }

                    } else NameOkDialog();

                }

            }
        });
//        /**
//         * id 쳌
//         */
//        mIdCheakBtn = findViewById(R.id.idCheakBtn);
//        mIdCheakBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String user_id = mLoginId.getText().toString();
//                String user_pw = mPassword.getText().toString();
//                /**
//                 * TODO :  서버 연결시 스위칭
//                 */
//                //showIdDialog();
//                idDoubleCheck(user_id, user_pw);
//            }
//        });
        /**
         * name 쳌
         */
        mNameCheakBtn = findViewById(R.id.nameCheakBtn);
        mNameCheakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("::중복체크::", "되냐? " + user_name);
                //아이디 칸 값 얻어와서 내용이 있다면~
                if (mLogin_name.getText().toString().length() != 0){
                    isNameCheakOk = true;
                    //닉네임 중복 확인 다이얼로그 불러주고
                    showNameOkDialog();
                }
                // 내용 비었으면 무반응

            }
        });


    }

    /**
     * id/pw 유효성 검사
     */
    private boolean checkValidity(String id, String pw){
        //불린값 초기화
        //id/pw 검사랑 중복검사 true 면
        isPass = true;
        //
        if(mLoginId.getText().toString().length() == 0 ){
            isPass = false;
            Toast.makeText(this,"제품번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mLogin_name.getText().toString().length() == 0 ){
            isPass = false;
            Toast.makeText(this,"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mPassword.getText().toString().length() == 0 ){
            isPass = false;
            Toast.makeText(this,"PW를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mPasswordConfirm.getText().toString().length() == 0 ){
            isPass = false;
            Toast.makeText(this,"PW를 재입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mResidentNum.getText().toString().length() == 0 || mResidentNumTail.getText().toString().length() == 0 ){
            isPass = false;
            Toast.makeText(this,"주민등록번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mHeight.getText().toString().length() == 0 || mWeight.getText().toString().length() == 0){
            isPass = false;
            Toast.makeText(this,"키와 몸무게를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        /**
         *  각종 예외 사항
         */
        //if(mPassword.getText().toString() != mPasswordConfirm.getText().toString() ){
        if(mPassword.getText().equals(mPasswordConfirm.getText()) ){
            //txtUserName.getText().equals(txtPassword.getText())
            Log.e("암호 :: ", mPassword.getText().toString());
            Log.e("암호재입력 :: ", mPasswordConfirm.getText().toString());
            //
            isPass = false;
            Toast.makeText(this,"PW 재입력 값이 맞지 않습니다.",Toast.LENGTH_SHORT).show();
            return isPass;
        }
        //
        if(mResidentNumTail.getText().toString().length() > 4){
            isPass = false;
            Toast.makeText(this,"주민등록번호 오류.",Toast.LENGTH_SHORT).show();
            return isPass;
        }



//        //아이디 + 패스워드 규칙
//        if(!Pattern.matches("^[a-zA-Z0-9]*$", id)) {
//            isPass = false;
//            Toast.makeText(this,"ID는 영문 대, 소문자와 숫자만 사용 가능합니다.",Toast.LENGTH_SHORT).show();
//        }else if(!Pattern.matches("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~`#?!@$%^&*-]).{6,}$", pw)) {
//            isPass = false;
//            Toast.makeText(this,"PW에 6자 이상의 영문 소문자, 숫자, 특수문자(~`#?!@$%^&*)를 포함하세요.",Toast.LENGTH_SHORT).show();
//        }else if(pw.toString().length() != 0){
//            isPass = false;
//            Toast.makeText(this,"PW 값이 없습니다.",Toast.LENGTH_SHORT).show();
//        }

//        //아이디 중복체크 TODO : 수정할 것!!!!!!!!!!!!!!!!!!!!!
//        if(isPass){
//            //통과하면
//            idDoubleCheck(id,pw);
//        }

        return isPass;
    }
//    /**
//     * 아이디 중복 체크 TODO : 수정할 것!!!!!!!!!!!!!!!!!!!!!
//     * EnterEditPersonalInfo?user_id=kosaf <- 요걸사용
//     */
//    private void idDoubleCheck(String id){
//
//        Call<ResponseBody> enterPersonalInfo = apiService.enterPersonalInfo(id);
//        enterPersonalInfo.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//                if (response.isSuccessful()) {
//                    Log.e("::::::Successful", "성공");
//                    //DB에 같은 아이디가 있다면 사용불가 메세지
//                    //isPass = false;
//                    showIdDialog();
//
//                } else {
//                    Log.e("::::::Error", "에러");
//                    // 해당 유저 없으면 실패할테니.
//                    Toast.makeText(SignInActivity.this,"사용할 수 있는 ID입니다.",Toast.LENGTH_SHORT).show();
//                    isIdCheakPass = true;
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("::::::Failure", t.toString());
//
//            }
//        });
//    }

    /**
     * 백버튼 누를때
     */
    public void onBackPressed() {
        //하나라도 내용이 있을때
        if( mLoginId.getText().length() != 0 ||
            mLogin_name.getText().length() != 0 ||
            mPassword.getText().length() != 0 ||
            mPasswordConfirm.getText().length() != 0 ||
            mResidentNum.getText().length() != 0 ||
            mResidentNumTail.getText().length() != 0 ||
            mHeight.getText().length() != 0 ||
            mWeight.getText().length() != 0 ){

            //경고창
            showNotEmptyDialog();
        } else {
            finish();
        }
        //Log.e("::어쩌라구요::",String.valueOf(mLoginId.getText().length()));
    }

    /**
     * 입력값있다 경고
     */
    private void showNotEmptyDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("입력된 내용이 있습니다.\n" +
                "이전 화면으로 돌아가시겠습니까?");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
     * 닉네임 중복 경고
     */
    private void showNameDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("이미 사용중인 닉네임 입니다.");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * 닉네임 사용 가능
     */
    private void showNameOkDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("사용 할 수 있는 닉네임 입니다.");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * 중복 확인 버튼 안 누름
     */
    private void NameOkDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("ID 중복 확인을 해주세요.");
        // 확인 버튼 설정
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
        userInfo.name = mLogin_name.getText().toString();
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
        /**
         * 레트로핏 통신 끝난 다음 실행할 것.
         */
        //성별 저장해서 BMI 수치 표시에 쓸것.
        putStringUserSex();
    }

    /**
     * SharedPreferences 에 사용자 성별 넣기
     */
    private void putStringUserSex(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String userSex = mResidentNumTail.getText().toString();
        editor.putString("userSex", userSex);
        editor.commit();
    }

}

package com.example.tinygreen.chinningmaster.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

/**
 * A login screen that offers login via login_id/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ApiService apiService = retrofit.create(ApiService.class);

    /**
     * Id to identity READ_CONTACTS permission request.
     * READ_CONTACTS 승인 요청 식별
     */
    //private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * 더미 정보 저장
     * TODO: remove after connecting to a real authentication system. 나중에 실제 DB값 받아올때 지워
     */
//    private static final String[] DUMMY_CREDENTIALS = new String[]{
//            //ID:PW
//            "foo:hello", "bar:world" ,"test:12345"
//    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     * 로그인 취소 할 수도 있으니까 계속 추적
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private LinearLayout mLayout;
    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Button mSignInButton;
    private Button mExitButton;

    private TextView mRegisterBtn;
    //로그인 성공 판별
    private Boolean isPass = false;
    //뒤로가기 두번 눌러 종료 시간
    private long backKeyPressedTime = 0;

    /**
     * onCreate 시작
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /**
         * 리니어 레이아웃 클릭하면 포커스 잃음
         */
        mLayout = findViewById(R.id.layout);
        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mIdView.getWindowToken(), 0);
            }
        });

        // Set up the login form.
        /**
         * 아이디 패스워드 id password
         */
        mIdView = findViewById(R.id.Login_id);
        //populateAutoComplete();

        mPasswordView = findViewById(R.id.Password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        /**
         * 로그인 버튼
         */
        mSignInButton = findViewById(R.id.log_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                //더미
//                String intentUserId = "치닝마스터";
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                intent.putExtra("USERID",intentUserId);
//                startActivity(intent);
                attemptLogin();

            }
        });
        /**
         * 종료 버튼 intent 종료시켜버려 exit_button
         */
        mExitButton = findViewById(R.id.exit_button);
        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                finish();
                //프로세스 킬
                android.os.Process.killProcess(android.os.Process.myPid());
                //그래프 조지기 TODO : 삭제할것
//                Intent intent = new Intent(getBaseContext(), MyWorkActivity.class);
//                startActivity(intent);

            }
        });
        /**
         * 회원가입 버튼 함께 운동해요!
         */
        mRegisterBtn = findViewById(R.id.register_button);
        mRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent);
            }
        });


        /**
         * 뷰
         */

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

//        /**
//         * 자동 로그인시 LoginActivity 스킵
//         */
//        String loginId = mIdView.getText().toString();
//        String password = mPasswordView.getText().toString();
//        Toast.makeText(LoginActivity.this, "자동 로그인 시도",Toast.LENGTH_SHORT).show();
//        attemptLogin();
//
////        if(!loginId.equals("") && !password.equals("")){
////            Toast.makeText(LoginActivity.this, "자동 로그인",Toast.LENGTH_SHORT).show();
////
////        }



    } // onCreate END

//    /**
//     * 더미 로그인
//     */
//    private void loginCheck2(String id, String pw){
//        isPass = true;
//        //Toast.makeText(LoginActivity.this, "님 환영합니다.",Toast.LENGTH_SHORT).show();
//    }


    /**
     * 로그인 판별
     */
    private void loginCheck(String id, String pw){
        Call<ResponseBody> checkLogin = apiService.checkLogin(id, pw);
        checkLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        //
                        Log.e("::::::jsonObject", String.valueOf(jsonObject.getBoolean("result")));
                        //
                        if(jsonObject.getBoolean("result")){
                            //TODO : 로그인 성공
                            isPass = true;
                            Toast.makeText(LoginActivity.this, "로그인 성공",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LoginActivity.this,"계정 정보를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("::::::Error", "에러");
                    Toast.makeText(LoginActivity.this,"계정 정보를 확인해주세요.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.toString());

            }
        });
    }


    /**
     * 뒤로가기 두 번 누르면 종료
     */
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
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid login_id, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * 로그인 시도 유효성 검사
     */
    private void attemptLogin() {
        //딴짓하던중인가?
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        // 에러메시지 초기화시켜주고
        mIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        // id pw 가져오고
        String loginId = mIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         *  TODO : 비번 체크 필요해?
         */

//        // Check for a valid password, if the user entered one.
//        // 패스워드 비어있지않고, 5자 이하면
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password)); // 패스워드가 너무 짧다
//            focusView = mPasswordView;
//            cancel = true;
//        }

        // Check for a valid login_id address.
        // 아이디 비어있니?
        if (TextUtils.isEmpty(loginId)) {
            mIdView.setError(getString(R.string.error_field_required));
            focusView = mIdView;
            cancel = true;
        }
        /**
         * 로그인 실패/성공 시 동작
          */
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            // 유효성 검사 하나라도 걸리면 해당 칸으로
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //
            showProgress(true);
            mAuthTask = new UserLoginTask(loginId, password);
            mAuthTask.execute((Void) null);
        }
    }

//    private boolean isPasswordValid(String password) {
//        //TODO: 패스워드 규칙 멀루 하까
//        return password.length() > 5;
//    }

    /**
     * Shows the progress UI and hides the login form.
     * 뱅뱅이 돌아가는거 보여줌
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLoginId;
        private final String mPassword;

        UserLoginTask(String id, String password) {
            mLoginId = id;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String loginId = mIdView.getText().toString();
            String password = mPasswordView.getText().toString();

            try {
                // Simulate network access.
                /**
                 * TODO : 서버 켜지면 스위칭
                 */
                loginCheck(loginId,password);
                //loginCheck2(loginId,password);
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                return false;
            }
//            // 회원정보 대조
//            // TODO : JSON 구축하면 더미 날릴 것
//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mLoginId)) {
//                    // Account exists, return true if the password matches.
//                    // ID PW 일치하면 true
//                    return pieces[1].equals(mPassword);
//                }
//            }

            // TODO : 여기서 True 넘겨야 로그인 된다
            return isPass;
            //return loginCheck(loginId,password);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // TODO : 로그인 성공시 동작.
                String intentUserId = mIdView.getText().toString();
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userId", intentUserId);
                editor.commit();
                //
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                //intent.putExtra("USERID",intentUserId);
                startActivity(intent);
                //finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}


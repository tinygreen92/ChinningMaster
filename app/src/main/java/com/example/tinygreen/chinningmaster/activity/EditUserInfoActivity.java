package com.example.tinygreen.chinningmaster.activity;

import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditUserInfoActivity extends AppCompatActivity {

    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    //ui set
    private AutoCompleteTextView mEditLogin_name;
    private AutoCompleteTextView mEditLogin_id;
    private EditText mEditPassword;
    private EditText mEditResident_num;
    private EditText mEditResident_num_tail;
    private EditText mEditHeight;
    private EditText mEditWeight;
    //btn
    private Button mEdit_submit;
    private Button mEdit_back_button;
    //
    private User userInfo = new User();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        //
        mEditLogin_id = findViewById(R.id.editLogin_id);
        mEditLogin_name = findViewById(R.id.editLogin_name);
        mEditPassword = findViewById(R.id.editPassword);
        mEditResident_num = findViewById(R.id.editResident_num);
        mEditResident_num_tail = findViewById(R.id.editResident_num_tail);
        mEditHeight = findViewById(R.id.editHeight);
        mEditWeight = findViewById(R.id.editWeight);
        //
        TextInputLayout mTextInputLayoutEdit1 = findViewById(R.id.textInputLayoutEdit1);
        TextInputLayout mTextInputLayoutEdit2 = findViewById(R.id.textInputLayoutEdit2);
        TextInputLayout mTextInputLayoutEdit3 = findViewById(R.id.textInputLayoutEdit3);
        TextInputLayout mTextInputLayoutEdit4 = findViewById(R.id.textInputLayoutEdit4);
        TextInputLayout mTextInputLayoutEdit5 = findViewById(R.id.textInputLayoutEdit5);
        mTextInputLayoutEdit1.setHintAnimationEnabled(false);
        mTextInputLayoutEdit2.setHintAnimationEnabled(false);
        mTextInputLayoutEdit3.setHintAnimationEnabled(false);
        mTextInputLayoutEdit4.setHintAnimationEnabled(false);
        mTextInputLayoutEdit5.setHintAnimationEnabled(false);
        //터치랑 수정 못하게
        mEditLogin_id.setFocusable(false);
        mEditLogin_id.setClickable(false);
        mEditResident_num_tail.setFocusable(false);
        mEditResident_num_tail.setClickable(false);
        //

        /**
         * submit 버튼 처리
         */
        mEdit_submit = findViewById(R.id.edit_submit);
        mEdit_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserinfo();
                finish();
            }
        });

        /**
         * back 버튼 처리
         */
        mEdit_back_button = findViewById(R.id.edit_back_button);
        mEdit_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         * SP 불러와서 userinfo  GET 처리
         */
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        getUserInfo(userId); //String userId



    }

    /**
     * POST 형식으로 보내
     */
    private void updateUserinfo(){

        //값 초기화
        userInfo.name = null;
        userInfo.birth_date = 0;
        userInfo.height = 0;
        userInfo.weight = 0;

        // 값 넣기
        userInfo.user_id = mEditLogin_id.getText().toString();
        userInfo.user_pw = mEditPassword.getText().toString();
        userInfo.name = mEditLogin_name.getText().toString();
        userInfo.birth_date = Integer.parseInt(mEditResident_num.getText().toString());
        userInfo.height = Integer.parseInt(mEditHeight .getText().toString());
        userInfo.weight = Integer.parseInt(mEditWeight .getText().toString());
        //

        Call<User> editInfo = apiService.editpersonalInfo(userInfo);
        editInfo.enqueue(new Callback<User>() {
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

    /**
     * 리스트 뿌리기
     */
    private void getUserInfo(String userId){
        Call<ResponseBody> enterPersonalInfo = apiService.enterPersonalInfo(userId);
        enterPersonalInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.e("::::::Successful", "성공");
                   // Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        /**
                         "user_id": "qwer1234",
                         "user_pw": "qwer1234",
                         "birth_date": 111122,
                         "height": 111,
                         "weight": 333,
                         "name": "rewq"
                         */
                        mEditLogin_id.setText(jsonObject.getString("user_id"));
                        mEditPassword.setText(jsonObject.getString("user_pw"));
                        mEditLogin_name.setText(jsonObject.getString("name"));
                        mEditResident_num.setText(jsonObject.getString("birth_date"));
                        mEditHeight.setText(jsonObject.getString("height"));
                        mEditWeight.setText(jsonObject.getString("weight"));
                        /**
                         * mEditResident_num_tail <- * 라고 두고 수정불가
                         */
                        mEditResident_num_tail.setText("*");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("::::::Error", "에러");
                    Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.getMessage());
                Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();

            }
        });
    }
}

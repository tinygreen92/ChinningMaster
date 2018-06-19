package com.example.tinygreen.chinningmaster.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        //터치랑 수정 못하게
        mEditLogin_id.setFocusable(false);
        mEditLogin_id.setClickable(false);
        //

        /**
         * TODO : 서버연결시 토글
         */
        getUserInfo("userId"); //String userId


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
                    Toast.makeText(getApplicationContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();

                    try {
                        String result = response.body().toString();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        mEditLogin_id.setText(jsonObject.getString("user_id"));
                        mEditLogin_name.setText(jsonObject.getString("name"));
                        mEditPassword.setText(jsonObject.getString("user_pw"));
                        mEditResident_num.setText(jsonObject.getString("birth_date"));
                        //mEditResident_num_tail.setText(인텐트값 넘겨);
                        mEditHeight.setText(jsonObject.getString("height"));
                        mEditWeight.setText(jsonObject.getString("weight"));



                        /**
                         * mEditResident_num_tail <- 이거 어떻게??
                         */

                    } catch (JSONException e) {
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

package com.example.tinygreen.chinningmaster.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tinygreen.chinningmaster.MyMarkerView;
import com.example.tinygreen.chinningmaster.R;
import com.example.tinygreen.chinningmaster.models.Record;
import com.example.tinygreen.chinningmaster.retrofit.ApiService;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyWorkActivity extends AppCompatActivity {
    /**
     * 레트로핏 설정
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);
    //
    private LineChart lineChart;
    //
//    private static ArrayList<String> userRecordTitle;
    private static ArrayList<Record> userRecord;
    private ArrayAdapter recordAdapter;

//    /**
//     * 유저 DB 비교
//     */
//    private boolean isExist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work);
        getSupportActionBar().setTitle("나의 운동기록");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 유저의 개인 record 넣을 준비
        userRecord = new ArrayList<>();
//        userRecordTitle = new ArrayList<>();
        //ArrayAdaper 세팅
//        recordAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, userRecordTitle);
        //
        Intent intent = getIntent();
        String userid = intent.getStringExtra("USERID");
        String isExist = intent.getStringExtra("isExist");
//        //TODO : 아무런 운동기록 없을때 예외처리.
//        //checkGetAllUserRecord(userid);
//        //Activity 죽이고 경고창 ,"start_time":"2018-10-19","elapsed_time":"00:00:12","correction_rate":"27%",
//        String start_time = "1999-01-01";
//        String elapsed_time = "00:00:00";
//        String correction_rate = "00%";
//        int count = 0 ;
//        int is_shared = 0;
//        userRecord.add(new Record(userid, start_time, elapsed_time, correction_rate, count, is_shared));
        //showNoUserDialog();
        //checkGetAllUserRecord(userid);
        getRecord(userid);
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * WorkoutType spinner 스피너
         *
         */
        Spinner s = findViewById(R.id.WorkoutType);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //0 = 횟수 / 1 = 경과시간 / 2 = 평균교정율
                setWorkCnt(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /**
         * 차트 붙여주고
         */
        lineChart = findViewById(R.id.chart);
        /**
         * 마커 뷰로 컨트롤
         */
        MyMarkerView marker = new MyMarkerView(this,R.layout.marker_view);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);
//        /**
//         * 차트 뿌려지는 로직
//         */
//        List<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0, 0));
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "test");
//        lineDataSet.setLineWidth(2);
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setCircleColorHole(Color.BLUE);
//        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
//        lineDataSet.setDrawCircleHole(true);
//        lineDataSet.setDrawCircles(true);
//        lineDataSet.setDrawHorizontalHighlightIndicator(false);
//        lineDataSet.setDrawHighlightIndicators(false);
//        lineDataSet.setDrawValues(false);
//
//        LineData lineData = new LineData(lineDataSet);
//        lineChart.setData(lineData);
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextColor(Color.BLACK);
//        xAxis.enableGridDashedLine(8, 24, 0);
//
//        YAxis yLAxis = lineChart.getAxisLeft();
//        yLAxis.setTextColor(Color.BLACK);
//
//        YAxis yRAxis = lineChart.getAxisRight();
//        yRAxis.setDrawLabels(false);
//        yRAxis.setDrawAxisLine(false);
//        yRAxis.setDrawGridLines(false);
//
//        Description description = new Description();
//        description.setText("");
//
//        lineChart.setDoubleTapToZoomEnabled(false);
//        lineChart.setDrawGridBackground(false);
//        lineChart.setDescription(description);
//        lineChart.animateY(1000, Easing.EasingOption.EaseInCubic);
//        lineChart.invalidate();
//

    }

    /**
     * 리스트 뿌리기
     */
    private void getRecord(String userId){
        Call<ResponseBody> getPersonalRecord = apiService.getPersonalRecord(userId);
        getPersonalRecord.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        //
                        String user_id;
                        String start_time;
                        String elapsed_time;
                        String correction_rate;
                        int count;
                        int is_shared;
                        //
                        for(int i=jsonArray.length()-1 ; i>=0 ; i-- ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            user_id = jsonObject.getString("user_id");
                            start_time = jsonObject.getString("start_time");
                            elapsed_time = jsonObject.getString("elapsed_time");
                            correction_rate = jsonObject.getString("correction_rate");
                            count = jsonObject.getInt("count");
                            is_shared = jsonObject.getInt("is_shared");
                            //
                            userRecord.add(new Record(user_id, start_time, elapsed_time, correction_rate, count, is_shared));
//                            userRecordTitle.add(start_time+" "+elapsed_time+" "+count+"개 "+correction_rate);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("::::::Error", "에러");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("::::::Failure", t.getMessage());
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //액션바 뒤로가기 버튼 동작
        if(id == android.R.id.home){
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO : 그래프 그리는 로직
     */
    private void setWorkCnt(int position){

        /**
         * 웹 통신 딜레이 예외처리
         */
        Intent intent = getIntent();
        String userid = intent.getStringExtra("USERID");
        String isExist = intent.getStringExtra("isExist");
        String start_time = "1999-01-01";
        String elapsed_time = "00:00:00";
        String correction_rate = "00%";
        int count = 0 ;
        int is_shared = 0;
        //
        //유저 기록이 record 테이블에서 확인 되지 않으면 더미 기록 하나 추가.
        if(isExist.equals("no")){
            userRecord.add(new Record(userid, start_time, elapsed_time, correction_rate, count, is_shared));
            //기록 없음 경고
            showNoUserDialog();
        }

        List<Entry> entries = new ArrayList<>();
        String str = null;
        int _x = 0;
        int _y = 0;


        if(position == 0){ //횟수 (x축, y축)
            for(int i = 0; i < userRecord.size(); i++) {
                _y = userRecord.get(userRecord.size()-i-1).count;
                entries.add(new Entry(i, _y));
            }

            str ="횟수";

        } else if(position == 1){ //경과시간
            for(int i = 0; i < userRecord.size(); i++) {
                _y = Integer.parseInt(userRecord.get(userRecord.size()-i-1).elapsed_time.substring(6, 8));
                _y += Integer.parseInt(userRecord.get(userRecord.size()-i-1).elapsed_time.substring(3, 5))*60;
                entries.add(new Entry(i, _y));
            }

            str ="경과시간";

        } else if(position == 2){//평균 교정율
            for(int i = 0; i < userRecord.size(); i++) {
                _y = Integer.parseInt(userRecord.get(userRecord.size()-i-1).correction_rate.substring(0, 2));
                entries.add(new Entry(i, _y));
            }

            str ="교정율";
        }


        LineDataSet lineDataSet = new LineDataSet(entries, str);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(1000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
    }

//    /**
//     * getAllUserRecord 에서 기록있나 없나 판별
//     */
//    private void checkGetAllUserRecord(final String userId){
//        isExist = false;
//        //
//        Call<ResponseBody> getAllUserRecord = apiService.getAllUserRecord();
//        getAllUserRecord.enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    isExist = false;
////                    Log.e("::뭐임???::", "안되냐??"); 됨
//                    try {
//                        String result = response.body().string();
//                        JSONArray jsonArray = new JSONArray(result);
//                        // JOIN 해서 얻어온 값 user_id = name
//                        String user_id;
//
////                        Log.e("::뭐임???::", jsonArray.toString());
//                        for(int i=jsonArray.length()-1 ; i>=0 ; i-- ){
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
////                            Log.e("::뭐임???::", jsonObject.toString());
//                            user_id = jsonObject.getString("user_id");
//
//                            //Log.e("::뭐임???::", user_id);
//                            /**
//                             * 유저 아이디가 전체 record에 존재하면 true 반환
//                             */
//                            if (user_id.equals(userId)){
//                                Log.e("::뭐임???::", user_id);
//                                isExist = true;
//                            }
//
//                        }
//                        //로직
//                        if (isExist ==true){
//                            //finish();
//                            //showNoUserDialog();
////                            getRecord(userId);
//
//                        } else {
//                            //TODO : 아무런 운동기록 없을때 예외처리.
//                            //checkGetAllUserRecord(userid);
//                            //Activity 죽이고 경고창 ,"start_time":"2018-10-19","elapsed_time":"00:00:12","correction_rate":"27%",
//                            String start_time = "1999-01-01";
//                            String elapsed_time = "00:00:00";
//                            String correction_rate = "00%";
//                            int count = 0 ;
//                            int is_shared = 0;
//                            userRecord.add(new Record(userId, start_time, elapsed_time, correction_rate, count, is_shared));
//                            showNoUserDialog();
//                        }
//                        isExist = false;
//
//                    } catch (IOException e) {
//                        Log.e(":::IOE:::", "머임??");
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        Log.e(":::JSON:::", "머임??");
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Log.e("::::::Error", "에러");
//                    Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("::::::Failure", t.toString());
//                //Log.e("::::::Failure", t.getMessage());
//                Toast.makeText(getApplicationContext(),"서버 연결 실패",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }

    /**
     * 기록 없음 경고
     */
    private void showNoUserDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyWorkActivity.this);
        //
        alertDialog.setTitle("알림");
        alertDialog.setMessage("운동 기록이 존재하지 않습니다.");
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



}

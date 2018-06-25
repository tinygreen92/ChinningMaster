package com.example.tinygreen.chinningmaster.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tinygreen.chinningmaster.MyMarkerView;
import com.example.tinygreen.chinningmaster.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MyWorkActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work);
        getSupportActionBar().setTitle("나의 운동기록");
        //액션바 뒤로가기 버튼 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * 스피너
         *
         */

        Spinner s = findViewById(R.id.WorkoutRecord);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //0이 아무것도 아닐때
                setWork(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //
        lineChart = findViewById(R.id.chart);
        /**
         * 마커 뷰로 컨트롤
         */
        MyMarkerView marker = new MyMarkerView(this,R.layout.marker_view);
        marker.setChartView(lineChart);
        lineChart.setMarker(marker);
        /**
         * 로직
         */
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));

        LineDataSet lineDataSet = new LineDataSet(entries, "교정률");
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

    private void setWork(int position){
        /**
         * 로직
         */
        List<Entry> entries = new ArrayList<>();
        if(position == 0){
            entries.add(new Entry(0, 0));

        } else if(position == 1){
                entries.add(new Entry(0, 50));
                entries.add(new Entry(1, 45));
                entries.add(new Entry(2, 40));
                entries.add(new Entry(3, 10));
                entries.add(new Entry(4, 60));
                entries.add(new Entry(5, 20));
                entries.add(new Entry(6, 100));
                entries.add(new Entry(7, 100));
                entries.add(new Entry(8, 40));
                entries.add(new Entry(9, 40));
                entries.add(new Entry(10, 100));
                entries.add(new Entry(11, 60));
                entries.add(new Entry(12, 100));
                entries.add(new Entry(13, 70));
        } else if(position == 2){
            entries.add(new Entry(0, 100));
            entries.add(new Entry(1, 80));
            entries.add(new Entry(2, 70));
            entries.add(new Entry(3, 30));
            entries.add(new Entry(4, 100));
            entries.add(new Entry(5, 800));
            entries.add(new Entry(6, 20));
        } else if(position == 3){
            entries.add(new Entry(0, 20));
            entries.add(new Entry(1, 30));
            entries.add(new Entry(3, 0));
            entries.add(new Entry(4, 60));
        } else if(position == 4) {
            entries.add(new Entry(0, 50));
            entries.add(new Entry(1, 70));
            entries.add(new Entry(2, 60));
            entries.add(new Entry(3, 60));
            entries.add(new Entry(4, 100));
            entries.add(new Entry(5, 20));
            entries.add(new Entry(6, 10));
        } else if(position == 5) {
            entries.add(new Entry(0, 0));
            entries.add(new Entry(1, 0));
            entries.add(new Entry(2, 40));
            entries.add(new Entry(3, 20));
            entries.add(new Entry(4, 0));
            entries.add(new Entry(5, 90));
            entries.add(new Entry(6, 70));
        }



        LineDataSet lineDataSet = new LineDataSet(entries, "교정률");
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
}

package com.example.tinygreen.chinningmaster.models;

/**
 * Created by tinygreen on 2018-06-06.
 */
public class Record {
    /**
     * /GetAllUserRecord  전체 사용자 기록 열람
     */
    public String user_id;
    public String start_time; // 날짜
    public String elapsed_time; // 경과시간
    public String correction_rate; //교정율
    public int record_id;
    public int count;
    public int is_shared;

    public Record() {
    }

    public Record(String user_id, String start_time, String elapsed_time, String correction_rate, int record_id, int count, int is_shared) {
        this.user_id = user_id;
        this.start_time = start_time;
        this.elapsed_time = elapsed_time;
        this.correction_rate = correction_rate;
        this.record_id = record_id;
        this.count = count;
        this.is_shared = is_shared;
    }
}

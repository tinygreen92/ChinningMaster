package com.example.tinygreen.chinningmaster.models;

/**
 * Created by tinygreen on 2018-05-06.
 */
public class User {
    /**
     * /signin 회원가입
     * /EditPersonalInfo 유저 정보 수정
     */
    public String user_id;
    public String user_pw;
    public String name;
    public int birth_date;
    public int height;
    public int weight;

    public User() {
    }
}

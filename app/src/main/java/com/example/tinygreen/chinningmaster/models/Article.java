package com.example.tinygreen.chinningmaster.models;

/**
 * Created by tinygreen on 2018-05-07.
 */
public class Article {
    /**
     * /WriteArticle 게시물 쓰기
     * /Reply 댓글 쓰기
     */
    public String article_id;
    public String user_id;
    public String title;
    public String content;
    public String workout_record;
    //TODO : time은 GET 할때만 필요한가?
    public String time;


    public Article(String article_id, String user_id, String title, String content, String workout_record, String time) {
        this.article_id = article_id;
        this.user_id = user_id;
        this.title = title;
        this.content = content;
        this.workout_record = workout_record;
        this.time = time;
    }
}

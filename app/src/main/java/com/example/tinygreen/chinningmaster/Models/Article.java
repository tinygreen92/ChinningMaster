package com.example.tinygreen.chinningmaster.Models;

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
    //public String time;

}

package com.example.tinygreen.chinningmaster.models;

/**
 * Created by tinygreen on 2018-05-07.
 */
public class Reply {
    /**
     * /WriteArticle 게시물 쓰기
     * /Reply 댓글 쓰기
     */
    public int article_id;
    public String user_id;
    public String content;
    public String time;

    public Reply() {
        //생성자
    }


}

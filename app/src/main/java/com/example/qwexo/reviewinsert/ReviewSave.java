package com.example.qwexo.reviewinsert;

/**
 * Created by qwexo on 2017-08-14.
 */

public class ReviewSave {
    public String userID;
    public String body;    //리뷰내용
    public String time;
    public String userName;
    public String marketID;
    public String marketName;
    public String score;
    public String imageBool;

    public ReviewSave(){

    }

    public ReviewSave(String userID, String userName, String body, String time, String marketID, String marketName,String score,String imageBool) {
        this.userID = userID;
        this.userName = userName;
        this.body = body;
        this.time = time;
        this.marketID = marketID;
        this.marketName = marketName;
        this.score = score;
        this.imageBool = imageBool;
    }
}

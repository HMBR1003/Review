package com.example.qwexo.reviewinsert;

/**
 * Created by qwexo on 2017-08-23.
 */

public class ReviewManagerListItem {
    private String marketName;
    private String body;
    private String time;
    private String reviewID;
    private String marketID;
    private String score;
    private String imageBool;

    public String getImageBool() {
        return imageBool;
    }

    public void setImageBool(String imageBool) {
        this.imageBool = imageBool;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMarketID() {
        return marketID;
    }

    public void setMarketID(String marketID) {
        this.marketID = marketID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getTime() {
        return time;
    }

    public String getBody() {
        return body;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
}

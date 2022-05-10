package com.example.yamatablog.Models;

import com.google.firebase.database.ServerValue;

public class UserRate {
    private String ratedByID, rateComment;
    int ratePoint;
    Object rateTime;

    public UserRate(String ratedByID, String rateComment, int ratePoint) {
        this.ratedByID = ratedByID;
        this.rateComment = rateComment;
        this.ratePoint = ratePoint;
        this.rateTime = ServerValue.TIMESTAMP;
    }

    public UserRate() {
    }

    public String getRatedByID() {
        return ratedByID;
    }

    public void setRatedByID(String ratedByID) {
        this.ratedByID = ratedByID;
    }

    public String getRateComment() {
        return rateComment;
    }

    public void setRateComment(String rateComment) {
        this.rateComment = rateComment;
    }

    public int getRatePoint() {
        return ratePoint;
    }

    public void setRatePoint(int ratePoint) {
        this.ratePoint = ratePoint;
    }

    public Object getRateTime() {
        return rateTime;
    }

    public void setRateTime(Object rateTime) {
        this.rateTime = rateTime;
    }
}

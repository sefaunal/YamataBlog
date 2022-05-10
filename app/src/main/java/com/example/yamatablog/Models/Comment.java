package com.example.yamatablog.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content,userID;
    private Object commentTime;

    public Comment() {
    }

    public Comment(String content, String userID) {
        this.content = content;
        this.userID = userID;
        this.commentTime = ServerValue.TIMESTAMP;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Object getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Object commentTime) {
        this.commentTime = commentTime;
    }
}

package com.example.yamatablog.Models;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postID;
    private String userID;
    private String postTitle;
    private String postAddress;
    private String postAssign;
    private String postDescription;
    private String postPicture;
    private String postStatus;
    private String postFixedBy;
    private Object postCreateDate;
    private Object postFixDate;
    private String userRated;

    public Post(String userID, String postTitle, String postAddress, String postAssign, String postDescription, String postPicture) {
        this.userID = userID;
        this.postTitle = postTitle;
        this.postAddress = postAddress;
        this.postAssign = postAssign;
        this.postDescription = postDescription;
        this.postPicture = postPicture;
        this.postStatus = "notFixed";
        this.postFixedBy = "Will Be Fixed";
        this.postCreateDate = ServerValue.TIMESTAMP;
        this.postFixDate = ServerValue.TIMESTAMP;
        this.userRated = "false";
    }

    public Post() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostAssign() {
        return postAssign;
    }

    public void setPostAssign(String postAssign) {
        this.postAssign = postAssign;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(String postPicture) {
        this.postPicture = postPicture;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getPostFixedBy() {
        return postFixedBy;
    }

    public void setPostFixedBy(String postFixedBy) {
        this.postFixedBy = postFixedBy;
    }

    public Object getPostCreateDate() {
        return postCreateDate;
    }

    public void setPostCreateDate(Object postCreateDate) {
        this.postCreateDate = postCreateDate;
    }

    public Object getPostFixDate() {
        return postFixDate;
    }

    public void setPostFixDate(Object postFixDate) {
        this.postFixDate = postFixDate;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserRated() {
        return userRated;
    }

    public void setUserRated(String userRated) {
        this.userRated = userRated;
    }
}

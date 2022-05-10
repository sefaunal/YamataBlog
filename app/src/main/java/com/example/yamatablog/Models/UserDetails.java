package com.example.yamatablog.Models;

public class UserDetails {
    private String userName;
    private String userMail;
    private String userPhoto;
    private String userDepartment;
    private String userID;
    private String userNameSearch;
    private String userStatus;
    private String userLastSeen;
    private String userToken;

    public UserDetails(String userID, String userName, String userMail, String userDepartment, String userPhoto, String userNameSearch, String userLastSeen) {
        this.userID = userID;
        this.userName = userName;
        this.userMail = userMail;
        this.userDepartment = userDepartment;
        this.userPhoto = userPhoto;
        this.userNameSearch = userNameSearch;
        this.userStatus = "";
        this.userLastSeen = userLastSeen;
        this.userToken = "";
    }



    public UserDetails() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserNameSearch() {
        return userNameSearch;
    }

    public void setUserNameSearch(String userNameSearch) {
        this.userNameSearch = userNameSearch;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserLastSeen() {
        return userLastSeen;
    }

    public void setUserLastSeen(String userLastSeen) {
        this.userLastSeen = userLastSeen;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}

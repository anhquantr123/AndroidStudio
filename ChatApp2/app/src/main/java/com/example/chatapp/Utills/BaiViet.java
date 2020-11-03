package com.example.chatapp.Utills;

public class BaiViet {
    private String date,postDesc,postImageUri,userProfileImageUrl,username;

    public BaiViet() {
    }

    public BaiViet(String date, String postDesc, String postImageUri, String userProfileImageUrl, String username) {
        this.date = date;
        this.postDesc = postDesc;
        this.postImageUri = postImageUri;
        this.userProfileImageUrl = userProfileImageUrl;
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public void setPostImageUri(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

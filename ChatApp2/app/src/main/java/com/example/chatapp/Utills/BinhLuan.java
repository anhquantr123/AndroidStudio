package com.example.chatapp.Utills;

public class BinhLuan {
    private String comments, profileImageUrl, username;

    public BinhLuan() {
    }

    public BinhLuan(String comments, String profileImageUrl, String username) {
        this.comments = comments;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

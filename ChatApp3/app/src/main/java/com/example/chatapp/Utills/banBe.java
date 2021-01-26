package com.example.chatapp.Utills;

public class banBe {
    private  String nghenghiep , profileImageUrl , username ;

    public banBe(String nghenghiep, String profileImageUrl, String username) {
        this.nghenghiep = nghenghiep;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
    }

    public banBe() {
    }

    public String getNghenghiep() {
        return nghenghiep;
    }

    public void setNghenghiep(String nghenghiep) {
        this.nghenghiep = nghenghiep;
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

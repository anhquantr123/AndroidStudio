package com.example.chatapp.Utills;

public class Users {
    private String city,nghenghiep,profileImage,quocgia,status,username;

    public Users() {
    }

    public Users(String city, String nghenghiep, String profileImage, String quocgia, String status, String username) {
        this.city = city;
        this.nghenghiep = nghenghiep;
        this.profileImage = profileImage;
        this.quocgia = quocgia;
        this.status = status;
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNghenghiep() {
        return nghenghiep;
    }

    public void setNghenghiep(String nghenghiep) {
        this.nghenghiep = nghenghiep;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getQuocgia() {
        return quocgia;
    }

    public void setQuocgia(String quocgia) {
        this.quocgia = quocgia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

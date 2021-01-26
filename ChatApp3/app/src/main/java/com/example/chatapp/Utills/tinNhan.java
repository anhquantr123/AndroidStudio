package com.example.chatapp.Utills;

public class tinNhan {
    private  String sms , status , userID, image ;

    public tinNhan() {
    }

    public tinNhan(String sms, String status, String userID, String image) {
        this.sms = sms;
        this.status = status;
        this.userID = userID;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

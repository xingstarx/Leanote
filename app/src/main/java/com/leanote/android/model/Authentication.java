package com.leanote.android.model;


import com.google.gson.annotations.SerializedName;

public class Authentication{

    @SerializedName("Ok")
    public boolean ok;
    @SerializedName("UserId")
    public String userId = "";
    @SerializedName("Username")
    public String userName = "";
    @SerializedName("Email")
    public String email = "";
    @SerializedName("Token")
    public String accessToken = "";

    @Override
    public String toString() {
        return "Authentication{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}

package com.leanote.android.model;


import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("Ok")
    public boolean isOk;
    @SerializedName("Msg")
    public String msg;

    public boolean isOk() {
        return isOk;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "isOk=" + isOk +
                ", msg='" + msg + '\'' +
                '}';
    }
}

package com.leanote.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiongxingxing on 17/1/8.
 */

public class BaseModel<T> {
    @SerializedName("Ok")
    public boolean ok;
    @SerializedName("Msg")
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "BaseModel{" +
                "ok='" + ok + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}

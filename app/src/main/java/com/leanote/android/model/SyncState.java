package com.leanote.android.model;


import com.google.gson.annotations.SerializedName;

public class SyncState {
    @SerializedName("LastSyncUsn")
    public int lastSyncUsn;

    @SerializedName("LastSyncTime")
    public long lastSyncTime;

}

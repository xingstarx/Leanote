package com.leanote.android.model;


import com.google.gson.annotations.SerializedName;

public class SyncState {
    @SerializedName("LastSyncUsn")
    public int mLastSyncUsn;

    @SerializedName("LastSyncTime")
    public long mLastSyncTime;

}

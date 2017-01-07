package com.leanote.android.model;


public class SyncEvent {
    private boolean isSucceed;

    public SyncEvent(boolean isSucceed) {
        this.isSucceed = isSucceed;
    }

    public boolean isSucceed() {
        return isSucceed;
    }
}

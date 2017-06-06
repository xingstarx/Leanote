package com.leanote.android.rxbus;

/**
 * Created by xingxing on 17/6/6.
 */

public class MenuRefreshEvent {
    public static final int TYPE_HIDE_MENU = 1;
    public static final int TYPE_SHOW_MENU = 2;
    public int type;

    public MenuRefreshEvent(int type) {
        this.type = type;
    }
}

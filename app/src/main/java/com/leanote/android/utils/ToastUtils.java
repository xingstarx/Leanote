package com.leanote.android.utils;


import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.leanote.android.R;


public class ToastUtils {
    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, @StringRes int message) {
        show(context, context.getString(message));
    }

    public static void showNetworkUnavailable(Context context) {
        show(context, R.string.network_is_unavailable);
    }

    public static void showNetworkError(Context context) {
        show(context, R.string.network_error);
    }
}

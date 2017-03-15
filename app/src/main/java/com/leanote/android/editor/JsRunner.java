package com.leanote.android.editor;


import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.apkfuns.logutils.LogUtils;
import com.leanote.android.utils.StringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JsRunner implements ValueCallback<String> {
    private static final String TAG = "JsRunner:";
    private String mResult;
    private CountDownLatch mLatch;

    public String get(final WebView webView, final String script) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogUtils.w(TAG + "Call from main thread");
        }
        mLatch = new CountDownLatch(1);
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(script, JsRunner.this);
            }
        });
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            mResult = null;
        }
        return StringUtils.notNullStr(mResult);
    }

    @Override
    public void onReceiveValue(String value) {
        LogUtils.i(TAG + "rsp=" + value);
        mResult = value.substring(1, value.length() - 1);
        mLatch.countDown();
    }
}
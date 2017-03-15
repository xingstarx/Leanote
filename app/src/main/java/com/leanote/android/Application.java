package com.leanote.android;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        JodaTimeAndroid.init(this);
    }
}

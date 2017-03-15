package com.leanote.android.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;

import com.leanote.android.R;

/**
 * Created by xiongxingxing on 17/3/15.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setElevation(0);
            Drawable up = ContextCompat.getDrawable(this, R.drawable.ic_action_arrow_back);
            DrawableCompat.setAutoMirrored(up, true);
            getSupportActionBar().setHomeAsUpIndicator(up);
        }
    }
}

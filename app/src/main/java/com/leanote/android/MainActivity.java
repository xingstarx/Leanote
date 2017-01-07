package com.leanote.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.leanote.android.database.AppDataBase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppDataBase.getAccountWithToken() == null) {
            LoginActivity.startLogin(this);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
    }
}

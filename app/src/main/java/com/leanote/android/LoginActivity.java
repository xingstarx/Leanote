package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.AuthApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.custom_website)
    EditText mCustomWebsiteView;
    @BindView(R.id.sign_in)
    TextView mSignInBtn;
    @BindView(R.id.forget_pwd)
    TextView mForgetPwdBtn;
    @BindView(R.id.sign_up)
    TextView mSignUpBtn;
    @BindView(R.id.add_custom_website)
    TextView mAddCustomWebsiteBtn;
    private AuthApi mAuthApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuthApi = ApiProvider.getInstance().getAuthApi();
    }

    public static void startLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.sign_in, R.id.forget_pwd, R.id.sign_up, R.id.add_custom_website})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
//                mAuthApi.
                break;
            case R.id.forget_pwd:
                break;
            case R.id.sign_up:
                break;
            case R.id.add_custom_website:
                break;
        }
    }
}


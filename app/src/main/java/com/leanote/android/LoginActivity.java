package com.leanote.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leanote.android.api.ApiProvider;
import com.leanote.android.model.Account;
import com.leanote.android.model.Authentication;
import com.leanote.android.model.BaseModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String LEANOTE_HOST = "https://leanote.com";
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
    private ApiProvider mApiProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mApiProvider = ApiProvider.getInstance();
    }

    public static void startLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.sign_in, R.id.forget_pwd, R.id.sign_up, R.id.add_custom_website})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                mApiProvider.init(getHost());
                Observable<BaseModel<Authentication>> observable = mApiProvider.getAuthApi().login(mEmailView.getText().toString(), mPasswordView.getText().toString());
                observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseModel<Authentication>>() {
                    @Override
                    public void call(BaseModel<Authentication> authenticationBaseModel) {
                        Account account = new Account();
                        account.userId = authenticationBaseModel.data.userId;
                        account.accessToken = authenticationBaseModel.data.accessToken;
                        account.email = authenticationBaseModel.data.email;
                        account.userName = authenticationBaseModel.data.userName;
                        account.host = getHost();
                        account.save();
                        MainActivity.show(LoginActivity.this);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
                break;
            case R.id.forget_pwd:
                break;
            case R.id.sign_up:
                break;
            case R.id.add_custom_website:
                break;
        }
    }

    private String getHost() {
        return !TextUtils.isEmpty(mCustomWebsiteView.getText().toString()) ? mCustomWebsiteView.getText().toString().trim() : LEANOTE_HOST;
    }
}


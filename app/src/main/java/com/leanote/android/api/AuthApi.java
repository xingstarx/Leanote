package com.leanote.android.api;


import com.leanote.android.model.Authentication;
import com.leanote.android.model.BaseModel;
import com.leanote.android.model.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface AuthApi {

    @GET("auth/login")
    Observable<BaseModel<Authentication>> login(@Query("email") String email, @Query("pwd") String password);

    @GET("auth/logout")
    Call<BaseResponse> logout(@Query("token") String token);

    @GET("auth/register")
    Call<BaseResponse> register(@Query("email") String email, @Query("pwd") String password);
}

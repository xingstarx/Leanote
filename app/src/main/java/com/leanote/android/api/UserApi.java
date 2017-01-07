package com.leanote.android.api;


import com.leanote.android.model.BaseResponse;
import com.leanote.android.model.SyncState;
import com.leanote.android.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

    @GET("user/info")
    Call<User> getInfo(@Query("userId") String userId);

    @GET("user/updateUsername")
    Call<BaseResponse> updateUsername(@Query("username") String username);

    @GET("user/updatePwd")
    Call<BaseResponse> updatePassword(@Query("oldPwd") String oldPwd, @Query("pwd") String pwd);

    @GET("user/getSyncState")
    Call<SyncState> getSyncState();
}

package com.leanote.android.api;

import com.leanote.android.api.convert.LeaResponseConverterFactory;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class ApiProvider {

    private Retrofit mApiRetrofit;

    private static class SingletonHolder {
        private final static ApiProvider INSTANCE = new ApiProvider();
    }

    public static ApiProvider getInstance() {
        Account account = AppDataBase.getAccountWithToken();
        if (account != null && SingletonHolder.INSTANCE.mApiRetrofit == null) {
            SingletonHolder.INSTANCE.init(account.getHost());
        }
        return SingletonHolder.INSTANCE;
    }

    private ApiProvider() {
    }

    public void init(String host) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url();
                        String path = url.encodedPath();
                        HttpUrl newUrl = url;
                        if (shouldAddTokenToQuery(path)) {
                            newUrl = url.newBuilder()
                                    .addQueryParameter("token", AppDataBase.getAccountWithToken().getAccessToken())
                                    .build();
                        }
                        Request newRequest = request.newBuilder()
                                .url(newUrl)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addNetworkInterceptor(interceptor)
                .build();
        mApiRetrofit = new Retrofit.Builder()
                .baseUrl(host + "/api/")
                .client(client)
                .addConverterFactory(new LeaResponseConverterFactory())
                .build();
    }

    private static boolean shouldAddTokenToQuery(String path) {
        return !path.startsWith("/api/auth/login")
                && !path.startsWith("/api/auth/register");
    }

    public AuthApi getAuthApi() {
        return mApiRetrofit.create(AuthApi.class);
    }

    public NoteApi getNoteApi() {
        return mApiRetrofit.create(NoteApi.class);
    }

    public UserApi getUserApi() {
        return mApiRetrofit.create(UserApi.class);
    }

    public NotebookApi getNotebookApi() {
        return mApiRetrofit.create(NotebookApi.class);
    }

}
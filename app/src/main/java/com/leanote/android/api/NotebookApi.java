package com.leanote.android.api;


import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Notebook;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface NotebookApi {

    @GET("notebook/getSyncNotebooks")
    Call<List<Notebook>> getSyncNotebooks(@Query("afterUsn") int afterUsn, @Query("maxEntry") int maxEntry);

    @GET("notebook/getNotebooks")
    Observable<BaseModel<List<Notebook>>> getNotebooks();

    @GET("notebook/getNotebooks")
    Call<BaseModel<List<Notebook>>> getCallNotebooks();

    @POST("notebook/addNotebook")
    Observable<BaseModel<Notebook>> addNotebook(@Query("title") String title, @Query("parentNotebookId") String parentId);
}

package com.leanote.android.api;


import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Note;
import com.leanote.android.model.UpdateRe;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rx.Observable;

public interface NoteApi {

    @GET("note/getSyncNotes")
    Observable<BaseModel<List<Note>>> getSyncNotes(@Query("afterUsn") int afterUsn, @Query("maxEntry") int maxEntry);

    @GET("note/getSyncNotes")
    Call<BaseModel<List<Note>>> getCallSyncNotes(@Query("afterUsn") int afterUsn, @Query("maxEntry") int maxEntry);


    @GET("note/getNotes")
    Call<BaseModel<List<Note>>> getCallNotes(@Query("notebookId") String notebookId);

    @GET("note/getNotes")
    Observable<BaseModel<List<Note>>> getNotes(@Query("notebookId") String notebookId);

    @GET("note/getNoteAndContent")
    Call<BaseModel<Note>> getNoteAndContent(@Query("noteId") String noteId);

    @Multipart
    @POST("note/addNote")
    Call<Note> add(@PartMap Map<String, RequestBody> body, @Part List<MultipartBody.Part> files);

    @Multipart
    @POST("note/updateNote")
    Call<Note> update(@PartMap Map<String, RequestBody> body, @Part List<MultipartBody.Part> files);

    @FormUrlEncoded
    @POST("note/updateNote")
    Observable<BaseModel<Note>> updateNote(@FieldMap Map<String, String> map);

    @POST("note/deleteTrash")
    Call<UpdateRe> delete(@Query("noteId") String noteId, @Query("usn") int usn);
}

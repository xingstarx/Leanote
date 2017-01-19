package com.leanote.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;
import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.NoteApi;
import com.leanote.android.api.NotebookApi;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Note;
import com.leanote.android.model.Notebook;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xiongxingxing on 17/1/18.
 */

public class SyncService extends Service {

    private HandlerThread mHandlerThread;
    private WeakHandler mWeakHandler;
    public static final int TYPE_FETCH_ALL = 101;
    public static final String ARG_SYNC_TYPE = "sync_type";
    public static final int MSG_FETCH_ALL = 1;
    private NotebookApi mNotebookApi;
    private NoteApi mNoteApi;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread("syncThread");
        mHandlerThread.start();
        mWeakHandler = new WeakHandler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                SyncService.this.handleMessage(msg);
                return true;
            }
        });
        mNotebookApi = ApiProvider.getInstance().getNotebookApi();
        mNoteApi = ApiProvider.getInstance().getNoteApi();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra(ARG_SYNC_TYPE, -1);
        switch (type) {
            case TYPE_FETCH_ALL:
                mWeakHandler.sendEmptyMessage(MSG_FETCH_ALL);
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_FETCH_ALL:
                fetchAll();
                break;

        }
    }

    public static void startSyncAll(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(ARG_SYNC_TYPE, TYPE_FETCH_ALL);
        context.startService(intent);
    }

    private void fetchAll() {
        mNotebookApi.getNotebooks().flatMap(new Func1<BaseModel<List<Notebook>>, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(final BaseModel<List<Notebook>> listBaseModel) {
                FlowManager.getDatabase(AppDataBase.class).executeTransaction(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        List<Notebook> notebooks = listBaseModel.data;
                        for (Notebook notebook : notebooks) {
                            Log.e("TEST", "insert notebookId == " + notebook.notebookId);
                            notebook.save(databaseWrapper);
                        }
                    }
                });
                final int maxNoteBookSun = AppDataBase.getMaxNoteBookUsn();
                return Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for (int usn = 0; usn < maxNoteBookSun; usn += 20) {
                            subscriber.onNext(usn);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        }).flatMap(new Func1<Integer, Observable<BaseModel<List<Note>>>>() {
            @Override
            public Observable<BaseModel<List<Note>>> call(Integer integer) {
                return mNoteApi.getSyncNotes(integer, 20);
            }
        }).flatMap(new Func1<BaseModel<List<Note>>, Observable<BaseModel<List<Note>>>>() {
            @Override
            public Observable<BaseModel<List<Note>>> call(final BaseModel<List<Note>> listBaseModel) {
                FlowManager.getDatabase(AppDataBase.class).executeTransaction(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        List<Note> notes = listBaseModel.data;
                        for (Note note : notes) {
                            Log.e("TEST", "insert noteId == " + note.noteId);
                            note.save(databaseWrapper);
                        }
                    }
                });
                return Observable.just(listBaseModel);
            }
        })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<BaseModel<List<Note>>>() {
            @Override
            public void call(BaseModel<List<Note>> listBaseModel) {

                Log.e("TEST", "note invoked== ");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                Log.e("TEST", "Error");
            }
        });
    }

}

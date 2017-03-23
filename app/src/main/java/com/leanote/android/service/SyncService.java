package com.leanote.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.badoo.mobile.util.WeakHandler;
import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.NoteApi;
import com.leanote.android.api.NotebookApi;
import com.leanote.android.api.UserApi;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;
import com.leanote.android.model.BaseModel;
import com.leanote.android.model.Note;
import com.leanote.android.model.NoteFile;
import com.leanote.android.model.Notebook;
import com.leanote.android.model.SyncState;
import com.leanote.android.rxbus.RxBus;
import com.leanote.android.rxbus.SyncEvent;
import com.leanote.android.utils.NetWorkUtils;
import com.leanote.android.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by xiongxingxing on 17/1/18.
 */

public class SyncService extends Service {

    public static final String TAG = "SyncService";
    private static final int MAX_ENTRY = 20;
    private HandlerThread mHandlerThread;
    private WeakHandler mWeakHandler;
    public static final int TYPE_FETCH_NOTE = 101;
    public static final String ARG_SYNC_TYPE = "sync_type";
    private static final String CONFLICT_SUFFIX = "--conflict";
    public static final int MSG_FETCH_NOTE = 1;
    private NotebookApi mNotebookApi;
    private NoteApi mNoteApi;
    private UserApi mUserApi;
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
        mUserApi = ApiProvider.getInstance().getUserApi();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra(ARG_SYNC_TYPE, -1);
        switch (type) {
            case TYPE_FETCH_NOTE:
                mWeakHandler.sendEmptyMessage(MSG_FETCH_NOTE);
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    private synchronized void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_FETCH_NOTE:
                fetchData();
                break;
        }
    }

    public static void startSyncNote(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(ARG_SYNC_TYPE, TYPE_FETCH_NOTE);
        context.startService(intent);
    }

    private void fetchData() {
        try {
            Account account = AppDataBase.getAccountWithToken();
            int maxUsn = Math.max(account.notebookUsn, account.noteUsn);
            if (maxUsn == 0) {
                fetchAllData(account, maxUsn);
            } else {
                fetchIncreaData(account);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchIncreaData(Account account) throws IOException {
        if (!NetWorkUtils.isConnected(this)) {
            RxBus.getInstance().send(new SyncEvent());
            return;
        }
        BaseModel<SyncState> syncStateModel = mUserApi.getSyncState().execute().body();
        if (syncStateModel.isError()) {
            RxBus.getInstance().send(new SyncEvent());
            return;
        }
        if (account.notebookUsn < syncStateModel.data.lastSyncUsn) {
            //同步notebook
            for (int i = account.notebookUsn; i <= syncStateModel.data.lastSyncUsn; i += MAX_ENTRY) {
                BaseModel<List<Notebook>> syncNotebookModel = mNotebookApi.getSyncNotebooks(i, MAX_ENTRY).execute().body();
                if (syncNotebookModel.isError()) {
                    continue;
                }
                for (Notebook remoteNotebook : syncNotebookModel.data) {
                    Notebook localNotebook = AppDataBase.getNotebookByServerId(remoteNotebook.notebookId);
                    if (localNotebook == null) {
                        remoteNotebook.insert();
                    } else {
                        remoteNotebook.id = localNotebook.id;
                        remoteNotebook.isDirty = false;
                        remoteNotebook.update();
                    }
                    Account usnAccount = AppDataBase.getAccountWithToken();
                    usnAccount.setNotebookUsn(remoteNotebook.usn);
                    usnAccount.update();
                }
            }

        }
        if (account.noteUsn < syncStateModel.data.lastSyncUsn) {
            //同步note
            for (int i = account.noteUsn; i <= syncStateModel.data.lastSyncUsn; i += MAX_ENTRY) {
                BaseModel<List<Note>> syncNoteModel = mNoteApi.getCallSyncNotes(i, MAX_ENTRY).execute().body();
                if (syncNoteModel.isError()) {
                    continue;
                }
                for (Note noteMeta : syncNoteModel.data) {
                    BaseModel<Note> remoteNoteModel = mNoteApi.getNoteAndContent(noteMeta.noteId).execute().body();
                    if (remoteNoteModel.isError()) {
                        continue;
                    }
                    Note localNote = AppDataBase.getNoteByServerId(noteMeta.noteId);
                    Note remoteNote = remoteNoteModel.data;
                    long localId;
                    if (localNote == null) {
                        localId = remoteNote.insert();
                        remoteNote.id = localId;
                    } else {
                        long id = localNote.id;
                        if (localNote.isDirty) {
                            LogUtils.w("note conflict, usn=" + remoteNote.usn + ", id=" + remoteNote.noteId);
                            //save local version as a local note
                            localNote.id = null;
                            localNote.title = localNote.title + CONFLICT_SUFFIX;
                            localNote.noteId = "";
                            localNote.insert();
                        }
                        LogUtils.i("note update, usn=" + remoteNote.usn + ", id=" + remoteNote.noteId);
                        remoteNote.id = id;
                        localId = localNote.id;
                    }
                    remoteNote.isDirty = false;
                    String content;
                    if (remoteNote.isMarkDown) {
                        content = convertToLocalImageLinkForMD(localId, remoteNote.content);
                    } else {
                        content = convertToLocalImageLinkForRichText(localId, remoteNote.content);
                    }
//                    LogUtils.i("content=" + remoteNote.content);
                    remoteNote.content = content;
                    remoteNote.noteAbstract = content.length() < 500 ? content : content.substring(0, 500);
                    remoteNote.update();
//                    handleFile(localId, remoteNote.getNoteFiles());
//                    updateTagsToLocal(localId, remoteNote.getTagData());
                    Account usnAccount = AppDataBase.getAccountWithToken();
                    usnAccount.setNoteUsn(remoteNote.usn);
                    account.save();
                }
            }
        }
        RxBus.getInstance().send(new SyncEvent());
    }

    private void fetchAllData(Account account, int maxUsn) throws IOException {
        LogUtils.e("fetchAllData invoked!");
        if (!NetWorkUtils.isConnected(this)) {
            RxBus.getInstance().send(new SyncEvent());
            return;
        }
        BaseModel<SyncState> syncStateModel = mUserApi.getSyncState().execute().body();
        LogUtils.e("syncStateModel.data.lastSyncUsn == " + syncStateModel.data.lastSyncUsn + ", maxUsn == " + maxUsn);

        if (syncStateModel.isError()) {
            RxBus.getInstance().send(new SyncEvent());
            return;
        }
        if (syncStateModel.data.lastSyncUsn <= maxUsn) {
            RxBus.getInstance().send(new SyncEvent());
            return;
        }

        final BaseModel<List<Notebook>> notebookModel = mNotebookApi.getCallNotebooks().execute().body();
        if (notebookModel.isError()) {
            return;
        }
        FlowManager.getDatabase(AppDataBase.class).executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Notebook> notebooks = notebookModel.data;
                for (Notebook notebook : notebooks) {
                    if (!notebook.isDeleted) {
                        notebook.save(databaseWrapper);
                    }
                }
            }
        });

        for (Notebook notebook : notebookModel.data) {
            final BaseModel<List<Note>> noteModel = mNoteApi.getCallNotes(notebook.notebookId).execute().body();
            if (noteModel.isError()) {
                continue;
            }
            for (Note note : noteModel.data) {
                note.insert();
                BaseModel<Note> contentNoteModel = mNoteApi.getNoteAndContent(note.noteId).execute().body();
                LogUtils.e("noteId == " + contentNoteModel.data.noteId + ", title == " + contentNoteModel.data.title);
                if (contentNoteModel.isError()) {
                    continue;
                }
                if (!contentNoteModel.data.isDeleted) {
                    String content;
                    if (contentNoteModel.data.isMarkDown) {
                        content = convertToLocalImageLinkForMD(note.id, contentNoteModel.data.content);
                    } else {
                        content = convertToLocalImageLinkForRichText(note.id, contentNoteModel.data.content);
                    }
                    note.content = content;
                    note.noteAbstract = content.length() < 500 ? content : content.substring(0, 500);
                    note.update();
                }
            }
        }
        account.noteUsn = syncStateModel.data.lastSyncUsn;
        account.notebookUsn = syncStateModel.data.lastSyncUsn;
        account.update();
        RxBus.getInstance().send(new SyncEvent());
    }

    private String convertToLocalImageLinkForRichText(Long id, String content) {
        return StringUtils.replace(content,
                "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>",
                String.format(Locale.US, "\\ssrc\\s*=\\s*\"%s/api/file/getImage\\?fileId=.*?\"", AppDataBase.getAccountWithToken().getHost()),
                new StringUtils.Replacer() {
                    @Override
                    public String replaceWith(String original, Object... extraData) {
                        Uri linkUri = Uri.parse(original.substring(1, original.length() - 1));
                        String serverId = linkUri.getQueryParameter("fileId");
                        NoteFile noteFile = AppDataBase.getNoteFileByServerId(serverId);
                        if (noteFile == null) {
                            noteFile = new NoteFile();
                            noteFile.noteId = (Long) extraData[0];
                            noteFile.localFileId = new ObjectId().toString();
                            noteFile.serverFileId = serverId;
                            noteFile.insert();
                        }
                        String localId = noteFile.localFileId;
                        return String.format(Locale.US, "(%s)", getLocalImageUri(localId).toString());
                    }
                }, id);
    }

    private static String convertToLocalImageLinkForMD(long id, String content) {
        return StringUtils.replace(content,
                String.format(Locale.US, "!\\[.*?\\]\\(%s/api/file/getImage\\?fileId=.*?\\)", AppDataBase.getAccountWithToken().getHost()),
                String.format(Locale.US, "\\(%s/api/file/getImage\\?fileId=.*?\\)", AppDataBase.getAccountWithToken().getHost()),
                new StringUtils.Replacer() {
                    @Override
                    public String replaceWith(String original, Object... extraData) {
                        Uri linkUri = Uri.parse(original.substring(1, original.length() - 1));
                        String serverId = linkUri.getQueryParameter("fileId");
                        NoteFile noteFile = AppDataBase.getNoteFileByServerId(serverId);
                        if (noteFile == null) {
                            noteFile = new NoteFile();
                            noteFile.noteId = (Long) extraData[0];
                            noteFile.localFileId = new ObjectId().toString();
                            noteFile.serverFileId = serverId;
                            noteFile.insert();
                        }
                        String localId = noteFile.localFileId;
                        return String.format(Locale.US, "(%s)", getLocalImageUri(localId).toString());
                    }
                }, id);
    }

    public static Uri getLocalImageUri(String localId) {
        return new Uri.Builder().scheme(SCHEME).path(IMAGE_PATH).appendQueryParameter("id", localId).build();
    }

    private static final String SCHEME = "file";
    private static final String IMAGE_PATH = "getImage";

}

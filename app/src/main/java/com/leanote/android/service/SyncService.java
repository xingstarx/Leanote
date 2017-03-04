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
    private HandlerThread mHandlerThread;
    private WeakHandler mWeakHandler;
    public static final int TYPE_FETCH_ALL = 101;
    public static final String ARG_SYNC_TYPE = "sync_type";
    public static final int MSG_FETCH_ALL = 1;
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
        try {
            BaseModel<SyncState> syncStateModel = mUserApi.getSyncState().execute().body();
            if (syncStateModel.data == null && !syncStateModel.ok) {
                return;
            }
            Account account = AppDataBase.getAccountWithToken();
            int maxUsn = Math.max(account.notebookUsn, account.noteUsn);
            if (syncStateModel.data.lastSyncUsn <= maxUsn) {
                return;
            }
            account.noteUsn = syncStateModel.data.lastSyncUsn;
            account.notebookUsn = syncStateModel.data.lastSyncUsn;
            account.update();

            final BaseModel<List<Notebook>> notebookModel = mNotebookApi.getCallNotebooks().execute().body();
            if (notebookModel.data == null && !syncStateModel.ok) {
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
                if (noteModel.data == null && !noteModel.ok) {
                    break;
                }
                for (Note note : noteModel.data) {
                    note.insert();
                    BaseModel<Note> contentNoteModel = mNoteApi.getNoteAndContent(note.noteId).execute().body();
                    if (contentNoteModel.data == null && !contentNoteModel.ok) {
                        break;
                    }
                    if (!contentNoteModel.data.isDeleted) {
                        String content = convertToLocalImageLinkForRichText(note.id, contentNoteModel.data.content);
                        note.content = content;
                        note.update();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

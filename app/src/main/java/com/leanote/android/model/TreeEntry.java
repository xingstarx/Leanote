package com.leanote.android.model;

/**
 * Created by xiongxingxing on 17/1/18.
 */
public class TreeEntry {
    public String title;
    public String notebookId;
    public String noteId;
    public String noteAbstract;
    public String updateTime;
    public boolean isMarkDown;
    public boolean isNotebook;

    public TreeEntry(String title, String notebookId, String updateTime, boolean isNotebook) {
        this.title = title;
        this.notebookId = notebookId;
        this.updateTime = updateTime;
        this.isNotebook = isNotebook;
    }

    public TreeEntry(String title, String noteId, String noteAbstract, String updateTime, boolean isMarkDown, boolean isNotebook) {
        this.title = title;
        this.noteId = noteId;
        this.noteAbstract = noteAbstract;
        this.updateTime = updateTime;
        this.isMarkDown = isMarkDown;
        this.isNotebook = isNotebook;
    }
}

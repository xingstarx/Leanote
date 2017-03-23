package com.leanote.android.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.utils.CollectionUtils;
import com.leanote.android.utils.TimeUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;


import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

@Table(name = "Note", database = AppDataBase.class)
public class Note extends BaseModel implements Serializable {
    @Unique
    @Column(name = "noteId")
    @SerializedName("NoteId")
    public String noteId = "";
    @Column(name = "notebookId")
    @SerializedName("NotebookId")
    public String noteBookId = "";
    @Column(name = "userId")
    @SerializedName("UserId")
    public String userId = "";
    @Column(name = "title")
    @SerializedName("Title")
    public String title = "";
    @Column(name = "content")
    @SerializedName("Content")
    public String content = "";
    @Column(name = "isMarkDown")
    @SerializedName("IsMarkdown")
    public boolean isMarkDown;
    @Column(name = "isTrash")
    @SerializedName("IsTrash")
    public boolean isTrash;
    @Column(name = "isDeleted")
    @SerializedName("IsDeleted")
    public boolean isDeleted;
    @Column(name = "isBlog")
    @SerializedName("IsBlog")
    public boolean isPublicBlog;
    @Column(name = "usn")
    @SerializedName("Usn")
    public int usn;

    @SerializedName("Tags")
    public List<String> tagData;
    @SerializedName("Files")
    public List<NoteFile> noteFiles;


    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    public Long id;
    @Column(name = "desc")
    public String desc = "";
    @Column(name = "noteAbstract")
    public String noteAbstract = "";
    public String fileIds;
    @Column(name = "isDirty")
    public boolean isDirty;
    @Column(name = "isUploading")
    public boolean isUploading;
    @Column(name = "createdTime")
    @SerializedName("CreatedTime")
    public String createdTime;
    @Column(name = "updatedTime")
    @SerializedName("UpdatedTime")
    public String updatedTime;
    @Column(name = "publicTime")
    @SerializedName("PublicTime")
    public String publicTime;
    @Column(name = "tags")
    public String tags = "";

    public List<String> getTagData() {
        return tagData;
    }

    public void updateTags() {
        if (CollectionUtils.isEmpty(tagData)) {
            tags = "";
            return;
        }
        StringBuilder tagBuilder = new StringBuilder();
        int size = tagData.size();
        int lastIndex = size - 1;
        for (int i = 0; i < size; i++) {
            tagBuilder.append(tagData.get(i));
            if (i < lastIndex) {
                tagBuilder.append(",");
            }
        }
        tags = tagBuilder.toString();
    }

    public boolean hasChanges(Note otherNote) {
        return otherNote == null
                || isChanged("title", title, otherNote.title)
                || isChanged("content", content, otherNote.content)
                || isChanged("notebookId", noteBookId, otherNote.noteBookId)
                || isChanged("isMarkDown", isMarkDown, otherNote.isMarkDown)
                || isChanged("tags", tags, otherNote.tags)
                || isChanged("isBlog", isPublicBlog, otherNote.isPublicBlog);
    }

    private boolean isChanged(String message, Object l, Object r) {
        boolean isEqual = l.equals(r);
        if (!isEqual) {
            Log.i("Note", message + " changed, origin  =" + l);
            Log.i("Note", message + " changed, modified=" + r);
        }
        return !isEqual;
    }
}

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

/**
 * Created by binnchx on 10/18/15.
 */
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
    @SerializedName("UpdatedTime")
    public String updatedTimeData = "";
    @SerializedName("CreatedTime")
    public String createdTimeData = "";
    @SerializedName("PublicTime")
    public String publicTimeData = "";

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    public Long id;
    public Long localNotebookId;
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
    public long createdTime;
    @Column(name = "updatedTime")
    public long updatedTime;
    @Column(name = "publicTime")
    long publicTime;
    @Column(name = "tags")
    public String tags = "";
    public boolean uploadSucc = true;

    public long getCreatedTimeVal() {
        return createdTime;
    }

    public void setCreatedTimeVal(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTimeVal() {
        return updatedTime;
    }

    public void setUpdatedTimeVal(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getPublicTimeVal() {
        return publicTime;
    }

    public void setPublicTimeVal(long publicTime) {
        this.publicTime = publicTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoteBookId() {
        return noteBookId;
    }

    public void setNoteBookId(String noteBookId) {
        this.noteBookId = noteBookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMarkDown() {
        return isMarkDown;
    }

    public void setIsMarkDown(boolean isMarkDown) {
        this.isMarkDown = isMarkDown;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setIsTrash(boolean isTrash) {
        this.isTrash = isTrash;
    }

    public int getUsn() {
        return usn;
    }

    public boolean isUploadSucc() {
        return uploadSucc;
    }

    public void setUploadSucc(boolean uploadSucc) {
        this.uploadSucc = uploadSucc;
    }

    public void setUsn(int usn) {
        this.usn = usn;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

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

    public void updateTime() {
        createdTime = TimeUtils.toTimestamp(createdTimeData);
        updatedTime = TimeUtils.toTimestamp(updatedTimeData);
        publicTime = TimeUtils.toTimestamp(publicTimeData);
    }

    public List<NoteFile> getNoteFiles() {
        return noteFiles;
    }

    //TODO:delete this
    public String getUpdatedTime() {
        return updatedTimeData;
    }

    //TODO:delete this
    public String getCreatedTime() {
        return updatedTimeData;
    }

    //TODO:delete this
    public String getPublicTime() {
        return publicTimeData;
    }

    //TODO:delete this
    public void setUpdatedTime(String v) {
    }

    //TODO:delete this
    public void setCreatedTime(String v) {
    }

    //TODO:delete this
    public void setPublicTime(String publicTime) {
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

    public boolean isPublicBlog() {
        return isPublicBlog;
    }

    public void setIsPublicBlog(boolean isPublicBlog) {
        this.isPublicBlog = isPublicBlog;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public String getNoteAbstract() {
        return noteAbstract;
    }

    public void setNoteAbstract(String noteAbstract) {
        this.noteAbstract = noteAbstract;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setIsUploading(boolean isUploading) {
        this.isUploading = isUploading;
    }

    public Long getLocalNotebookId() {
        return localNotebookId;
    }

    public void setLocalNotebookId(Long localNotebookId) {
        this.localNotebookId = localNotebookId;
    }

    public static class UpdateTimeComparetor implements Comparator<Note> {
        @Override
        public int compare(Note lhs, Note rhs) {
            long lTime = lhs.getUpdatedTimeVal();
            long rTime = rhs.getUpdatedTimeVal();
            if (lTime > rTime) {
                return -1;
            } else if (lTime < rTime) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId='" + noteId + '\'' +
                ", noteBookId='" + noteBookId + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isMarkDown=" + isMarkDown +
                ", isTrash=" + isTrash +
                ", isDeleted=" + isDeleted +
                ", isPublicBlog=" + isPublicBlog +
                ", usn=" + usn +
                ", tagData=" + tagData +
                ", noteFiles=" + noteFiles +
                ", updatedTimeData='" + updatedTimeData + '\'' +
                ", createdTimeData='" + createdTimeData + '\'' +
                ", publicTimeData='" + publicTimeData + '\'' +
                ", id=" + id +
                ", localNotebookId=" + localNotebookId +
                ", desc='" + desc + '\'' +
                ", noteAbstract='" + noteAbstract + '\'' +
                ", fileIds='" + fileIds + '\'' +
                ", isDirty=" + isDirty +
                ", isUploading=" + isUploading +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", publicTime=" + publicTime +
                ", tags='" + tags + '\'' +
                ", uploadSucc=" + uploadSucc +
                '}';
    }
}

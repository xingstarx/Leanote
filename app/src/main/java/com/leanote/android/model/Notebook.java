package com.leanote.android.model;

import com.google.gson.annotations.SerializedName;
import com.leanote.android.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(name = "Notebook", database = AppDataBase.class)
public class Notebook extends BaseModel{

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    public long id;
    @Unique
    @Column(name = "notebookId")
    @SerializedName("NotebookId")
    public String notebookId;
    @Column(name = "parentNotebookId")
    @SerializedName("ParentNotebookId")
    public String parentNotebookId;
    @Column(name = "userId")
    @SerializedName("UserId")
    public String userId;
    @Column(name = "title")
    @SerializedName("Title")
    public String title;
    public String urlTitle;
    @Column(name = "seq")
    @SerializedName("Seq")
    public int seq;
    @SerializedName("IsBlog")
    public boolean isBlog;
    @Column(name = "createdTime")
    @SerializedName("CreatedTime")
    public String createTime;
    @Column(name = "updatedTime")
    @SerializedName("UpdatedTime")
    public String updateTime;
    @Column(name = "isDirty")
    public boolean isDirty;
    @Column(name = "isDeletedOnServer")
    @SerializedName("IsDeleted")
    public boolean isDeleted;
    @Column(name = "isTrash")
    public boolean isTrash;
    @Column(name = "usn")
    @SerializedName("Usn")
    public int usn;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isBlog() {
        return isBlog;
    }

    public void setIsBlog(boolean isBlog) {
        this.isBlog = isBlog;
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

    public String getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(String notebookId) {
        this.notebookId = notebookId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUrlTitle() {
        return urlTitle;
    }

    public void setUrlTitle(String urlTitle) {
        this.urlTitle = urlTitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUsn() {
        return usn;
    }

    public void setUsn(int usn) {
        this.usn = usn;
    }

    public String getParentNotebookId() {
        return parentNotebookId;
    }

    public void setParentNotebookId(String parentNotebookId) {
        this.parentNotebookId = parentNotebookId;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setIsTrash(boolean isTrash) {
        this.isTrash = isTrash;
    }


    @Override
    public String toString() {
        return "Notebook{" +
                "id=" + id +
                ", notebookId='" + notebookId + '\'' +
                ", parentNotebookId='" + parentNotebookId + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", urlTitle='" + urlTitle + '\'' +
                ", seq=" + seq +
                ", isBlog=" + isBlog +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", isDirty=" + isDirty +
                ", isDeleted=" + isDeleted +
                ", isTrash=" + isTrash +
                ", usn=" + usn +
                '}';
    }
}

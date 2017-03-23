package com.leanote.android.model;

import com.google.gson.annotations.SerializedName;
import com.leanote.android.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(name = "Notebook", database = AppDataBase.class)
public class Notebook extends BaseModel {

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
}

package com.leanote.android.model;

import com.google.gson.annotations.SerializedName;
import com.leanote.android.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(name = "NoteFile", database = AppDataBase.class)
public class NoteFile extends BaseModel {

    @Column(name = "noteLocalId")
    public long noteId;

    @PrimaryKey
    @Column(name = "localFileId")
    @SerializedName("LocalFileId")
    public String localFileId;

    @Column(name = "serverFileId")
    @SerializedName("FileId")
    public String serverFileId;

    @SerializedName("HasBody")
    public boolean hasBody;

    @Column(name = "isAttach")
    @SerializedName("IsAttach")
    public boolean isAttach;

    @Column(name = "localPath")
    public String localPath;

    @Column(name = "type")
    @SerializedName("Type")
    public String type;

    @SerializedName("Title")
    public String title;

}

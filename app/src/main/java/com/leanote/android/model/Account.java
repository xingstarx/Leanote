package com.leanote.android.model;

import com.google.gson.annotations.SerializedName;
import com.leanote.android.database.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(name = "Account", database = AppDataBase.class)
public class Account extends BaseModel {

    public static final int EDITOR_RICH_TEXT = 0;
    public static final int EDITOR_MARKDOWN = 1;

    @Column(name = "id")
    @PrimaryKey(autoincrement = true)
    @SerializedName("LocalUserId")
    public long localUserId;
    @Column(name = "userId")
    @SerializedName("UserId")
    public String userId = "";
    @Column(name = "userName")
    @SerializedName("Username")
    public String userName = "";
    @Column(name = "email")
    @SerializedName("Email")
    public String email = "";
    @Column(name = "verified")
    @SerializedName("Verified")
    public boolean verified;
    @Column(name = "avatar")
    @SerializedName("Avatar")
    public String avatar = "";
    @Column(name = "token")
    public @SerializedName("Token")
    String accessToken = "";
    @Column(name = "defaultEditor")
    public int defaultEditor = EDITOR_MARKDOWN;
    @Column(name = "host")
    @SerializedName("Host")
    public String host = "";
    @Column(name = "noteUsn")
    public int noteUsn;
    @Column(name = "notebookUsn")
    public int notebookUsn;

    @Deprecated
    @Column(name = "lastUsn")
    @SerializedName("LastSyncUsn")
    public  int lastSyncUsn;

    public Account() {
    }

    public long getLocalUserId() {
        return localUserId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Deprecated
    public int getLastSyncUsn() {
        return lastSyncUsn;
    }

    public int getDefaultEditor() {
        return defaultEditor;
    }

    public String getHost() {
        return host;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Deprecated
    public void setLastUsn(int lastUsn) {
        this.lastSyncUsn = lastUsn;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setDefaultEditor(int defaultEditor) {
        this.defaultEditor = defaultEditor;
    }

    public int getNoteUsn() {
        return noteUsn;
    }

    public int getNotebookUsn() {
        return notebookUsn;
    }

    public void setNoteUsn(int noteUsn) {
        this.noteUsn = noteUsn;
    }

    public void setNotebookUsn(int notebookUsn) {
        this.notebookUsn = notebookUsn;
    }

    @Override
    public String toString() {
        return "Account{" +
                "localUserId=" + localUserId +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", avatar='" + avatar + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", defaultEditor=" + defaultEditor +
                ", lastSyncUsn=" + lastSyncUsn +
                ", host='" + host + '\'' +
                '}';
    }
}

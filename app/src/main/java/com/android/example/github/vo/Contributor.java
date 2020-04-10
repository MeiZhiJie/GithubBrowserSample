package com.android.example.github.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

@Entity(
        primaryKeys = {"repoName", "repoOwner", "login"},
        foreignKeys = @ForeignKey(
                entity = Repo.class,
                parentColumns = {"name", "owner_login"},
                childColumns = {"repoName", "repoOwner"},
                onUpdate = ForeignKey.CASCADE,
                deferred = true
        )
)
public class Contributor {
    @NonNull
    @SerializedName("login")
    private String login;
    @SerializedName("contributions")
    private Integer contributions;
    @SerializedName("avatar_url")
    private String avatarUrl;
    // does not show up in the response but set in post processing.
    @NonNull
    private String repoName;
    // does not show up in the response but set in post processing.
    @NonNull
    private String repoOwner;

    public Contributor(
            String login,
            Integer contributions,
            @Nullable String avatarUrl) {
        this.login = login;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public Integer getContributions() {
        return contributions;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }
}

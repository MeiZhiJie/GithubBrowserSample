package com.android.example.github.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "login")
public class User {
    //@field:SerializedName("login")
    @SerializedName("login")
    @NonNull
    private String login;
    //@field:SerializedName("avatar_url")
    @SerializedName("avatar_url")
    private String avatarUrl;
    //@field:SerializedName("name")
    @SerializedName("name")
    private String name;
    //@field:SerializedName("company")
    @SerializedName("company")
    private String company;
    //@field:SerializedName("repos_url")
    @SerializedName("repos_url")
    private String reposUrl;
    //@field:SerializedName("blog")
    @SerializedName("blog")
    private String blog;

    public User(String login,
                @Nullable String avatarUrl,
                @Nullable String name,
                @Nullable String company,
                @Nullable String reposUrl,
                @Nullable String blog) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.company = company;
        this.reposUrl = reposUrl;
        this.blog = blog;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getBlog() {
        return blog;
    }
}

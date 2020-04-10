package com.android.example.github.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import com.google.gson.annotations.SerializedName;

@Entity(
        indices = {@Index("id"), @Index("owner_login")},
        primaryKeys = {"name", "owner_login"}
)
public class Repo {
    public  static final int UNKNOWN_ID = -1;

    private Integer id;
    //@field:SerializedName("name")
    @NonNull
    private String name;
    //@field:SerializedName("full_name")
    private String fullName;
    //@field:SerializedName("description")
    @SerializedName("description")
    private String description;
    //@field:SerializedName("owner")
    //@field:Embedded(prefix = "owner_")
    @NonNull
    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    private  Owner owner;
    //@field:SerializedName("stargazers_count")
    @SerializedName("stargazers_count")
    private Integer stars;

    public Repo(Integer id,
                String name,
                String fullName,
                @Nullable String description,
                Owner owner,
                Integer stars) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.owner = owner;
        this.stars = stars;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public Owner getOwner() {
        return owner;
    }

    public Integer getStars() {
        return stars;
    }

    public static class Owner {
        //@field:SerializedName("login")
        @NonNull
        @SerializedName("login")
        private String login;
        //@field:SerializedName("url")
        @SerializedName("url")
        private String url;

        public Owner(String login, String url) {
            this.login = login;
            this.url = url;
        }

        public String getLogin() {
            return login;
        }

        public String getUrl() {
            return url;
        }
    }

}

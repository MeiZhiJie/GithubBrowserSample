/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @SerializedName("name")
    @NonNull
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("description")
    private String description;

    @NonNull
    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    private  Owner owner;

    @SerializedName("stargazers_count")
    private Integer stars;

    public Repo(Integer id,
                @NonNull String name,
                String fullName,
                @Nullable String description,
                @NonNull Owner owner,
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

    @NonNull
    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    public Owner getOwner() {
        return owner;
    }

    public Integer getStars() {
        return stars;
    }

    public static class Owner {
        @NonNull
        @SerializedName("login")
        private String login;
        @SerializedName("url")
        private String url;

        public Owner(@NonNull String login, @Nullable String url) {
            this.login = login;
            this.url = url;
        }

        @NonNull
        public String getLogin() {
            return login;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Owner)) {
                return false;
            }

            Owner owner = (Owner) obj;
            return owner != null
                    && login.equals(owner.getLogin())
                    && ((url == null && owner.getUrl() == null)
                    || (url != null && url.equals(owner.getUrl())));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Repo)) {
            return false;
        }

        Repo repo = (Repo) obj;
        return repo != null
                && id.equals(repo.getId())
                && name.equals(repo.getName())
                && ((fullName == null && repo.getFullName() == null)
                || (fullName != null && fullName.equals(repo.getFullName())))
                && ((description == null && repo.getDescription() == null)
                || (description != null && description.equals(repo.getDescription())))
                && owner.equals(repo.getOwner())
                && ((stars == null && repo.getStars() == null)
                ||(stars != null && stars.equals(repo.getStars())));
    }
}

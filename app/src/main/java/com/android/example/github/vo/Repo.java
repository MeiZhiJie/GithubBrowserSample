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
        @NonNull
        @SerializedName("login")
        private String login;
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

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
import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "login")
public class User {
    @SerializedName("login")
    @NonNull
    private String login;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("name")
    private String name;

    @SerializedName("company")
    private String company;

    @SerializedName("repos_url")
    private String reposUrl;

    @SerializedName("blog")
    private String blog;

    public User(@NonNull String login,
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

    @NonNull
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;
        return user != null
                && login.equals(user.getLogin())
                && ((avatarUrl == null && user.getAvatarUrl() == null)
                || (avatarUrl != null && avatarUrl.equals(user.getAvatarUrl())))
                && ((name == null && user.getName() == null)
                || (name != null && name.equals(user.getName())))
                && ((company == null && user.getCompany() == null)
                || (company != null && company.equals(user.getCompany())))
                && ((reposUrl == null && user.getReposUrl() == null)
                || (reposUrl != null && reposUrl.equals(user.getReposUrl())))
                && ((blog == null && user.getBlog() == null)
                || (blog != null && blog.equals(user.getBlog())));
    }
}

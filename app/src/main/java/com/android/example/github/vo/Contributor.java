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

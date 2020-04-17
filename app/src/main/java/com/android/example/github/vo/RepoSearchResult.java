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
import androidx.room.TypeConverters;

import com.android.example.github.db.GithubTypeConverters;

import java.util.List;

@Entity(primaryKeys = "query")
@TypeConverters(GithubTypeConverters.class)
public class RepoSearchResult {

    @NonNull
    private String query;
    private List<Integer> repoIds;
    private Integer totalCount;
    private Integer next;

    public RepoSearchResult(@NonNull String query,
                            List<Integer> repoIds,
                            Integer totalCount,
                            @Nullable Integer next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public List<Integer> getRepoIds() {
        return repoIds;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getNext() {
        return next;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RepoSearchResult)) {
            return false;
        }

        RepoSearchResult searchResult = (RepoSearchResult) obj;
        return searchResult != null
                && query.equals(searchResult.getQuery())
                && repoIds.equals(searchResult.getRepoIds())
                && totalCount.equals(searchResult.getTotalCount())
                && ((next == null && searchResult.getNext() == null)
                || (next != null && next.equals(searchResult.getNext())));
    }
}

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

package com.android.example.github.db;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.android.example.github.vo.Contributor;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.RepoSearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for database access on Repo related operations.
 */
@Dao
public abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Repo... repos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContributors(List<Contributor> contributors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRepos(List<Repo> repositories);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract Long createRepoIfNotExists(Repo repo);

    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin AND name = :name")
    public abstract LiveData<Repo> load(String ownerLogin, String name);

    @Query(
            "SELECT login, avatarUrl, repoName, repoOwner, contributions FROM contributor " +
                    "WHERE repoName = :name AND repoOwner = :owner " +
                    "ORDER BY contributions DESC"
    )
    public abstract LiveData<List<Contributor>> loadContributors(String owner, String name);

    @Query(
            "SELECT * FROM Repo " +
                    "WHERE owner_login = :owner " +
                    "ORDER BY stars DESC"
    )
    public abstract LiveData<List<Repo>> loadRepositories(String owner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RepoSearchResult result);

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    public abstract LiveData<RepoSearchResult> search(String query);

    public LiveData<List<Repo>> loadOrdered(List<Integer> repoIds) {
        Map<Integer, Integer> order = new HashMap<>();
        for (int i = 0; i < repoIds.size(); i++) {
            order.put(repoIds.get(i), i);
        }

        return Transformations.map(loadById(repoIds), repositories -> {
            Collections.sort(repositories, (r1, r2) -> {
                int pos1 = order.get(r1.getId());
                int pos2 = order.get(r2.getId());
                return pos1 - pos2;
            });
            return repositories;
        });
    }

    @Query("SELECT * FROM Repo WHERE id in (:repoIds)")
    protected abstract LiveData<List<Repo>> loadById(List<Integer> repoIds);

    @Query("SELECT * FROM RepoSearchResult WHERE `query` = :query")
    @Nullable
    public abstract RepoSearchResult findSearchResult(String query);
}

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

package com.android.example.github.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.android.example.github.AppExecutors;
import com.android.example.github.api.ApiResponse;
import com.android.example.github.api.ApiSuccessResponse;
import com.android.example.github.api.GithubService;
import com.android.example.github.api.RepoSearchResponse;
import com.android.example.github.db.GithubDb;
import com.android.example.github.db.RepoDao;
import com.android.example.github.util.AbsentLiveData;
import com.android.example.github.util.RateLimiter;
import com.android.example.github.vo.Contributor;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.RepoSearchResult;
import com.android.example.github.vo.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository that handles Repo instances.
 *
 * unfortunate naming :/ .
 * Repo - value object name
 * Repository - type of this class.
 */
@Singleton
public class RepoRepository {
    private AppExecutors appExecutors;
    private GithubDb db;
    private RepoDao repoDao;
    private GithubService githubService;
    private RateLimiter repoListRateLimit = new RateLimiter<String>(10, TimeUnit.MINUTES);

    @Inject
    public RepoRepository(
            AppExecutors appExecutors,
            GithubDb db,
            RepoDao repoDao,
            GithubService githubService) {
        this.appExecutors = appExecutors;
        this.db = db;
        this.repoDao = repoDao;
        this.githubService = githubService;
    }

    private class Repos extends NetworkBoundResource<List<Repo>, List<Repo>> {
        private String owner;
        Repos(AppExecutors appExecutors, String owner) {
            super(appExecutors);
            this.owner = owner;
        }

        @Override
        protected void saveCallResult(List<Repo> item) {
            repoDao.insertRepos(item);
        }

        @Override
        protected Boolean shouldFetch(@Nullable List<Repo> data) {
            return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner);
        }

        @Override
        protected LiveData<List<Repo>> loadFromDb() {
            return repoDao.loadRepositories(owner);
        }

        @Override
        protected LiveData<ApiResponse<List<Repo>>> createCall() {
            return githubService.getRepos(owner);
        }

        @Override
        protected void onFetchFailed() {
            repoListRateLimit.reset(owner);
        }
    }

    public LiveData<Resource<List<Repo>>> loadRepos(String owner) {
        return new Repos(appExecutors, owner).asLiveData();
    }

    private class OneRepo extends NetworkBoundResource<Repo, Repo> {
        private String owner;
        private String name;

        OneRepo(AppExecutors appExecutors, String owner, String name) {
            super(appExecutors);
            this.owner = owner;
            this.name= name;
        }

        @Override
        protected void saveCallResult(Repo item) {
            repoDao.insert(item);
        }

        @Override
        protected Boolean shouldFetch(@Nullable Repo data) {
            return data == null;
        }

        @Override
        protected LiveData<Repo> loadFromDb() {
            return repoDao.load(owner, name);
        }

        @Override
        protected LiveData<ApiResponse<Repo>> createCall() {
            return githubService.getRepo(owner, name);
        }
    }

    public LiveData<Resource<Repo>> loadRepo(String owner, String name) {
        return new OneRepo(appExecutors, owner, name).asLiveData();
    }

    private class Contributors extends NetworkBoundResource<List<Contributor>, List<Contributor>> {
        private String owner;
        private String name;

        Contributors(AppExecutors appExecutors, String owner, String name) {
            super(appExecutors);
            this.owner = owner;
            this.name = name;
        }

        @Override
        protected void saveCallResult(List<Contributor> item) {
            for (Contributor elem : item) {
                elem.setRepoName(name);
                elem.setRepoOwner(owner);
            }
            db.runInTransaction(() -> {
                repoDao.createRepoIfNotExists(new Repo(
                        Repo.UNKNOWN_ID,
                        name,
                        owner + "/" + name,
                        "",
                        new Repo.Owner(owner, null),
                        0
                ));
                repoDao.insertContributors(item);
            });
        }

        @Override
        protected Boolean shouldFetch(@Nullable List<Contributor> data) {
            return data == null || data.isEmpty();
        }

        @Override
        protected LiveData<List<Contributor>> loadFromDb() {
            return repoDao.loadContributors(owner, name);
        }

        @Override
        protected LiveData<ApiResponse<List<Contributor>>> createCall() {
            return githubService.getContributors(owner, name);
        }
    }

    public LiveData<Resource<List<Contributor>>> loadContributors(String owner, String name) {
        return new Contributors(appExecutors, owner, name).asLiveData();
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextSearchPageTask fetchNextSearchPageTask = new FetchNextSearchPageTask(
                query,
                githubService,
                db);
        appExecutors.networkIO().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    private class Serach extends NetworkBoundResource<List<Repo>, RepoSearchResponse> {
        private String query;

        Serach(AppExecutors appExecutors, String query) {
            super(appExecutors);
            this.query= query;
        }

        @Override
        protected void saveCallResult(RepoSearchResponse item) {
            List<Integer> repoIds = new ArrayList<>();
            for (Repo elem : item.getItems()) {
                repoIds.add(elem.getId());
            }

            RepoSearchResult repoSearchResult = new RepoSearchResult(
                    query,
                    repoIds,
                    item.getTotal(),
                    item.getNextPage());
            db.beginTransaction();
            try {
                repoDao.insertRepos(item.getItems());
                repoDao.insert(repoSearchResult);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        protected Boolean shouldFetch(@Nullable List<Repo> data) {
            return data == null;
        }

        @Override
        protected LiveData<List<Repo>> loadFromDb() {
            return Transformations.switchMap(repoDao.search(query), searchData -> {
                if (searchData == null) {
                    return AbsentLiveData.create();
                } else {
                    return repoDao.loadOrdered(searchData.getRepoIds());
                }
            });
        }

        @Override
        protected LiveData<ApiResponse<RepoSearchResponse>> createCall() {
            return githubService.searchRepos(query);
        }

        @Override
        protected RepoSearchResponse processResponse(ApiSuccessResponse<RepoSearchResponse> response) {
            RepoSearchResponse body = response.getBody();
            body.setNextPage(response.getNextPage());
            return body;
        }
    }

    public  LiveData<Resource<List<Repo>>> search(String query) {
        return new Serach(appExecutors, query).asLiveData();
    }
}

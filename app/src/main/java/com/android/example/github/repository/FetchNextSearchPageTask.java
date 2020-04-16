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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.example.github.api.ApiEmptyResponse;
import com.android.example.github.api.ApiErrorResponse;
import com.android.example.github.api.ApiResponse;
import com.android.example.github.api.ApiSuccessResponse;
import com.android.example.github.api.GithubService;
import com.android.example.github.api.RepoSearchResponse;
import com.android.example.github.db.GithubDb;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.RepoSearchResult;
import com.android.example.github.vo.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
public class FetchNextSearchPageTask implements Runnable {
    private String query;
    private GithubService githubService;
    private GithubDb db;

    private MutableLiveData<Resource<Boolean>> _liveData = new MutableLiveData<>();

    public FetchNextSearchPageTask(
            String query,
            GithubService githubService,
            GithubDb db) {
        this.query = query;
        this.githubService = githubService;
        this.db= db;
    }

    @Override
    public void run() {
        RepoSearchResult current = db.repoDao().findSearchResult(query);
        if (current == null) {
            _liveData.postValue(null);
            return;
        }
        Integer nextPage = current.getNext();
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false));
            return;
        }

        Resource<Boolean> newValue = null;
        try {
            Response response = githubService.searchRepos(query, nextPage).execute();
            ApiResponse apiResponse = ApiResponse.create(response);
            if (apiResponse instanceof ApiSuccessResponse) {
                // we merge all repo ids into 1 list so that it is easier to fetch the
                // result list.
                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.getRepoIds());
                RepoSearchResponse repoSearchResponse = (RepoSearchResponse)((ApiSuccessResponse) apiResponse).getBody();
                for (Repo repo : repoSearchResponse.getItems()) {
                    ids.add(repo.getId());
                }
                RepoSearchResult merged = new RepoSearchResult(query, ids,
                        repoSearchResponse.getTotal(), ((ApiSuccessResponse) apiResponse).getNextPage()
                );
                try {
                    db.beginTransaction();
                    db.repoDao().insert(merged);
                    db.repoDao().insertRepos(repoSearchResponse.getItems());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                newValue = Resource.success(((ApiSuccessResponse) apiResponse).getNextPage() != null);
            } else if (apiResponse instanceof ApiEmptyResponse) {
                newValue = Resource.success(false);
            } else if (apiResponse instanceof ApiErrorResponse) {
                newValue = Resource.error(((ApiErrorResponse) apiResponse).getErrorMessage(), true);
            }

        } catch (IOException e) {
            newValue = Resource.error(e.getMessage(), true);
        }

        _liveData.postValue(newValue);
    }

    public LiveData<Resource<Boolean>> getLiveData() {
        return _liveData;
    }
}

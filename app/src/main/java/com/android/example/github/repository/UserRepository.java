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

import com.android.example.github.AppExecutors;
import com.android.example.github.api.ApiResponse;
import com.android.example.github.api.GithubService;
import com.android.example.github.db.UserDao;
import com.android.example.github.vo.Resource;
import com.android.example.github.vo.User;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {
    private AppExecutors appExecutors;
    private UserDao userDao;
    private GithubService githubService;

    @Inject
    public UserRepository(AppExecutors appExecutors,
                          UserDao userDao,
                          GithubService githubService) {
        this.appExecutors = appExecutors;
        this.userDao = userDao;
        this.githubService = githubService;
    }

    public LiveData<Resource<User>> loadUser(String login) {
        return new UserResource(appExecutors, login).asLiveData();
    }

    private class UserResource extends NetworkBoundResource<User, User> {
        private String login;
        public UserResource(AppExecutors appExecutors, String login) {
            super(appExecutors);
            this.login = login;
        }

        @Override
        protected void saveCallResult(User item) {
            userDao.insert(item);
        }

        @Override
        protected Boolean shouldFetch(@Nullable User data) {
            return data == null;
        }

        @Override
        protected LiveData<User> loadFromDb() {
            return userDao.findByLogin(login);
        }

        @Override
        protected LiveData<ApiResponse<User>> createCall() {
            return githubService.getUser(login);
        }
    }
}

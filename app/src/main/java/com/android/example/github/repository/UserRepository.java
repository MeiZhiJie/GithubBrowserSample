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
//@OpenForTesting
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

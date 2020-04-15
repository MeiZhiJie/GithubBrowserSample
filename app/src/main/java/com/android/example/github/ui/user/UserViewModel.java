package com.android.example.github.ui.user;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.android.example.github.repository.RepoRepository;
import com.android.example.github.repository.UserRepository;
import com.android.example.github.util.AbsentLiveData;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.Resource;
import com.android.example.github.vo.User;

import java.util.List;

import javax.inject.Inject;

//@OpenForTesting
public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private RepoRepository repoRepository;
    private MutableLiveData<String> _login = new MutableLiveData<>();

    @Inject
    public UserViewModel(UserRepository userRepository, RepoRepository repoRepository) {
        super();
        this.repoRepository = repoRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Resource<List<Repo>>> getRepositories() {
        return Transformations.switchMap(_login, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return repoRepository.loadRepos(login);
            }
        });
    }

    public LiveData<Resource<User>> getUser() {
        return Transformations.switchMap(_login, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return userRepository.loadUser(login);
            }
        });
    }

    public void  setLogin(@Nullable String login) {
        if (_login.getValue() != login) {
            _login.setValue(login);
        }
    }

    public void retry() {
        if (_login.getValue() != null) {
            _login.setValue(_login.getValue());
        }
    }

    public LiveData<String> getLogin() {
        return _login;
    }
}

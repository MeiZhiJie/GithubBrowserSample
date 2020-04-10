package com.android.example.github.ui.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.android.example.github.repository.RepoRepository;
import com.android.example.github.util.AbsentLiveData;
import com.android.example.github.vo.Contributor;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.Resource;

import java.util.List;

import javax.inject.Inject;

//@OpenForTesting
public class RepoViewModel extends ViewModel {
    private RepoRepository repository;
    private MutableLiveData<RepoId> _repoId = new MutableLiveData<>();

    @Inject
    public RepoViewModel(RepoRepository repository) {
        super();
        this.repository = repository;
    }

    public LiveData<RepoId> getRepoId() {
        return _repoId;
    }

    public LiveData<Resource<Repo>> getRepo() {
        return Transformations.switchMap(_repoId, input ->
                input.ifExists((owner, name) -> repository.loadRepo(owner, name))
        );
    }

    public LiveData<Resource<List<Contributor>>> getContributors() {
        return Transformations.switchMap(_repoId, input ->
            input.ifExists((owner, name) -> repository.loadContributors(owner, name))
        );
    }

    interface Ifunc<T> {
         LiveData<T> f(String owner, String name);
    }

    public void retry() {
        String owner = _repoId.getValue() == null ? null : _repoId.getValue().getOwner();
        String name = _repoId.getValue() == null ? null : _repoId.getValue().getName();
        if (owner != null && name != null) {
            _repoId.setValue(new RepoId(owner, name));
        }
    }

    public void setId(String owner, String name) {
        RepoId update = new RepoId(owner, name);
        if (update.equals(_repoId.getValue())) {
            return;
        }
        _repoId.setValue(update);
    }

    class RepoId {
        private String owner;
        private String name;

        RepoId(String owner, String name) {
            this.owner = owner;
            this.name = name;
        }

        String getOwner() {
            return owner;
        }

        String getName() {
            return name;
        }

        <T> LiveData<T> ifExists(Ifunc fun) {
            if (owner == null || owner.isEmpty()
                    || name == null || name.isEmpty()) {
                return AbsentLiveData.create();
            } else  {
                return fun.f(owner, name);
            }
        }

        @Override
        public boolean equals(Object obj) {
            RepoId repoId = (RepoId) obj;
            return repoId.getOwner().equals(this.owner)
                    && repoId.getName().equals(this.name);
        }
    }
}

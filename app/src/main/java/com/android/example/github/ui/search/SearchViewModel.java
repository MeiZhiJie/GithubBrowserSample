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

package com.android.example.github.ui.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.android.example.github.repository.RepoRepository;
import com.android.example.github.util.AbsentLiveData;
import com.android.example.github.vo.Repo;
import com.android.example.github.vo.Resource;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {
    private RepoRepository repoRepository;
    private MutableLiveData<String> _query = new MutableLiveData<>();
    private NextPageHandler nextPageHandler;

    @Inject
    public SearchViewModel(RepoRepository repoRepository) {
        super();
        this.repoRepository = repoRepository;
        nextPageHandler = new NextPageHandler(repoRepository);
    }

    public LiveData<String> getQuery() {
        return _query;
    }

    public LiveData<Resource<List<Repo>>> getResults() {
        return Transformations.switchMap(_query, search -> {
            if (search == null || search.isEmpty()) {
                return AbsentLiveData.create();
            } else {
                return repoRepository.search(search);
            }
        });
    }

    public  LiveData<LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }

    public void setQuery(String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (input == _query.getValue()) {
            return;
        }
        nextPageHandler.reset();
        _query.setValue(input);
    }

    public void loadNextPage() {
        if (_query.getValue() != null && !"".equals(_query.getValue())) {
            nextPageHandler.queryNextPage(_query.getValue());
        }
    }

    public void refresh() {
        if (_query.getValue() != null) {
            _query.setValue(_query.getValue());
        }
    }
    class LoadMoreState {
        private Boolean isRunning;
        private String errorMessage;
        private boolean handledError = false;

        LoadMoreState(Boolean isRunning, @Nullable String errorMessage) {
            this.isRunning = isRunning;
            this.errorMessage = errorMessage;
        }

        @Nullable
        public String getErrorMessageIfNotHandled() {
            if (handledError) {
                return null;
            }
            handledError = true;
            return errorMessage;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    private class NextPageHandler implements Observer<Resource<Boolean>> {
        private RepoRepository repository;
        @Nullable
        private LiveData<Resource<Boolean>> nextPageLiveData = null;
        private MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        @Nullable
        private String query;
        private Boolean _hasMore = false;

        public boolean hasMore() {
            return _hasMore;
        }

        public MutableLiveData<LoadMoreState> getLoadMoreState() {
            return loadMoreState;
        }

        NextPageHandler(RepoRepository repository) {
            this.repository = repository;
            reset();
        }

        void queryNextPage(@NonNull String query) {
            if (query.equals(this.query)) {
                return;
            }
            unregister();
            this.query = query;
            nextPageLiveData = repository.searchNextPage(query);
            loadMoreState.setValue(new LoadMoreState(
                    true,
                    null
            ));
            if (nextPageLiveData != null) {
                nextPageLiveData.observeForever(this);
            }
        }

        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            if (result == null) {
                reset();
            } else {
                switch (result.getStatus()) {
                    case SUCCESS:
                        _hasMore = result.getData() == true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(
                                        false,
                                        null
                                )
                        );
                    case ERROR:
                        _hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(
                                        false,
                                        result.getMessage()
                                )
                        );
                    case LOADING:
                        // ignore
                }
            }
        }

        private void unregister() {
            if (nextPageLiveData != null) {
                nextPageLiveData.removeObserver(this);
            }
            nextPageLiveData = null;
            if (_hasMore) {
                query = null;
            }
        }

        void reset() {
            unregister();
            _hasMore = true;
            loadMoreState.setValue(new LoadMoreState(
                    false,
                    null));
        }
    }
}

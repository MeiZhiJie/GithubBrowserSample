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

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.android.example.github.AppExecutors;
import com.android.example.github.api.ApiEmptyResponse;
import com.android.example.github.api.ApiErrorResponse;
import com.android.example.github.api.ApiResponse;
import com.android.example.github.api.ApiSuccessResponse;
import com.android.example.github.vo.Resource;


/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private AppExecutors appExecutors;
    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {
        result.setValue(Resource.loading(null));

        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() == null || !result.getValue().equals(newValue)) {
        //if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response instanceof ApiSuccessResponse) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse((ApiSuccessResponse<RequestType>) response));
                    appExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)))
                    );
                });
            } else if (response instanceof ApiEmptyResponse) {
                appExecutors.mainThread().execute(() -> {
                    // reload from disk whatever we had
                    result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                });
            } else if (response instanceof ApiErrorResponse) {
                onFetchFailed();
                result.addSource(dbSource,  newData ->
                        setValue(Resource.error(((ApiErrorResponse)response).getErrorMessage(), newData)));
            }
        });
    }

    protected void onFetchFailed() {}

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiSuccessResponse<RequestType> response) {
        return response.getBody();
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @MainThread
    protected abstract Boolean shouldFetch(@Nullable ResultType data);

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}

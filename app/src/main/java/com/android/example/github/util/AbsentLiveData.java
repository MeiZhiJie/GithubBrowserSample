package com.android.example.github.util;

import androidx.lifecycle.LiveData;

/**
 * A LiveData class that has `null` value.
 */
public class AbsentLiveData<T> extends LiveData<T> {
    private AbsentLiveData() {
        super();
        initialize();
    }
    private void initialize() {
        // use post instead of set since this can be created on any thread
        postValue(null);
    }

    public static <T> LiveData<T> create()  {
        return new AbsentLiveData();
    }
}

package com.android.example.github.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;


@Singleton
public class GithubViewModelFactory implements ViewModelProvider.Factory {
    private Map<Class<ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public GithubViewModelFactory(Map<Class<ViewModel>, Provider<ViewModel>> creators) {
        super();
        this.creators = creators;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //val creator = creators[modelClass] ?: creators.entries.firstOrNull {
        Provider<ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            for (Map.Entry elem : creators.entrySet()) {
                if (modelClass.isAssignableFrom(elem.getKey().getClass())) {
                    if (elem.getValue() == null) {
                        throw new IllegalArgumentException("unknown model class " + modelClass);
                    }
                    creator = (Provider<ViewModel>) elem.getValue();
                    break;
                }
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }

        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

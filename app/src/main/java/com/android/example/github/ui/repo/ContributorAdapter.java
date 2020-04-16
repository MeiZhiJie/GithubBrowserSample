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

package com.android.example.github.ui.repo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import com.android.example.github.AppExecutors;
import com.android.example.github.databinding.ContributorItemBinding;
import com.android.example.github.ui.common.DataBoundListAdapter;
import com.android.example.github.vo.Contributor;
import com.android.example.github.R;

public class ContributorAdapter extends DataBoundListAdapter<Contributor, ContributorItemBinding> {
    private DataBindingComponent dataBindingComponent;
    private ContributorCallback callback;

    public ContributorAdapter(
            DataBindingComponent dataBindingComponent,
            AppExecutors appExecutors,
            @Nullable ContributorCallback callback) {
        super(appExecutors, new ContributorDiffCallbak());
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }


    @Override
    protected ContributorItemBinding createBinding(ViewGroup parent) {
        ContributorItemBinding binding = DataBindingUtil
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.contributor_item,
                        parent,
                        false,
                        dataBindingComponent
                );
        binding.getRoot().setOnClickListener(view -> {
            if (binding.getContributor() != null && callback != null) {
                    callback.callback(binding.getContributor(), binding.imageView);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ContributorItemBinding binding, Contributor item) {
        binding.setContributor(item);
    }

    private static class ContributorDiffCallbak extends DiffUtil.ItemCallback<Contributor> {

        @Override
        public boolean areItemsTheSame(@NonNull Contributor oldItem, @NonNull Contributor newItem) {
            return oldItem.getLogin().equals(newItem.getLogin());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contributor oldItem, @NonNull Contributor newItem) {
            return ((oldItem.getAvatarUrl() == null && newItem.getAvatarUrl() == null)
                    || (oldItem.getAvatarUrl() != null && oldItem.getAvatarUrl().equals(newItem.getAvatarUrl())))
                    && ((oldItem.getContributions() == null && newItem.getContributions() == null)
                    || (oldItem.getContributions() != null && oldItem.getContributions().equals(newItem.getContributions())));
        }
    }
}

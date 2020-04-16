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

package com.android.example.github.ui.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import com.android.example.github.R;

import com.android.example.github.AppExecutors;
import com.android.example.github.databinding.RepoItemBinding;
import com.android.example.github.vo.Repo;

/**
 * A RecyclerView adapter for [Repo] class.
 */
public class RepoListAdapter extends DataBoundListAdapter<Repo, RepoItemBinding> {
    private DataBindingComponent dataBindingComponent;
    private Boolean showFullName;
    private RepoClickCallback repoClickCallback;

    public RepoListAdapter(
            DataBindingComponent dataBindingComponent,
            AppExecutors appExecutors,
            Boolean showFullName,
            RepoClickCallback repoClickCallback) {
        super(appExecutors, new RepoDiffCallbak());
        this.dataBindingComponent = dataBindingComponent;
        this.showFullName = showFullName;
        this.repoClickCallback = repoClickCallback;
    }

    @Override
    protected RepoItemBinding createBinding(ViewGroup parent) {
        RepoItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.repo_item,
                parent,
                false,
                dataBindingComponent
        );

        binding.setShowFullName(showFullName);
        binding.getRoot().setOnClickListener((view) -> {
            if (binding.getRepo() != null && repoClickCallback != null) {
                repoClickCallback.onClick(binding.getRepo());
            }
        });
        return binding;
    }

    @Override
    protected void bind(RepoItemBinding binding, Repo item) {
        binding.setRepo(item);
    }

    private static class RepoDiffCallbak extends DiffUtil.ItemCallback<Repo> {
        @Override
        public boolean areItemsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return oldItem.getOwner().equals(newItem.getOwner())
                    && oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return ((oldItem.getDescription() == null && newItem.getDescription() == null)
                    || (oldItem.getDescription() != null && oldItem.getDescription().equals(newItem.getDescription())))
                    && ((oldItem.getStars() == null && newItem.getStars() == null)
                    || (oldItem.getStars() != null && oldItem.getStars().equals(newItem.getStars())));
        }
    }
}

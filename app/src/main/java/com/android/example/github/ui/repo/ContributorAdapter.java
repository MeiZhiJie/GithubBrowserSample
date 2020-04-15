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
    private AppExecutors appExecutors;
    private ContributorCallback callback;

    public ContributorAdapter(
            DataBindingComponent dataBindingComponent,
            AppExecutors appExecutors,
            @Nullable ContributorCallback callback) {
        super(appExecutors, new ContributorDiffCallbak());
        this.dataBindingComponent = dataBindingComponent;
        this.appExecutors = appExecutors;
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
            return oldItem.getAvatarUrl().equals(newItem.getAvatarUrl())
                    && oldItem.getContributions().equals(newItem.getContributions());
        }
    }
}

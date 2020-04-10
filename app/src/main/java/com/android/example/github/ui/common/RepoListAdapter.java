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
    private AppExecutors appExecutors;
    private Boolean showFullName;
    private RepoClickCallback repoClickCallback;

    public RepoListAdapter(
            DataBindingComponent dataBindingComponent,
            AppExecutors appExecutors,
            Boolean showFullName,
            RepoClickCallback repoClickCallback) {
        super(appExecutors, new RepoDiffCallbak());
        this.dataBindingComponent = dataBindingComponent;
        this.appExecutors = appExecutors;
        this.showFullName = showFullName;
        this.repoClickCallback = repoClickCallback;
    }

    @Override
    protected RepoItemBinding createBinding(ViewGroup parent) {
        RepoItemBinding binding = (RepoItemBinding) DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.repo_item,
                parent,
                false,
                dataBindingComponent
        );

        binding.setShowFullName(showFullName);
        binding.getRoot().setOnClickListener((view) -> {
            if (binding.getRepo() != null) {
                if (repoClickCallback != null) {
                    repoClickCallback.onClick(view);
                }
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
            return oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getStars().equals(newItem.getStars());
        }
    }
}

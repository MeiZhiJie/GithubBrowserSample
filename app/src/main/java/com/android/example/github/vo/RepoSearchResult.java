package com.android.example.github.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.android.example.github.db.GithubTypeConverters;

import java.util.List;

@Entity(primaryKeys = "query")
@TypeConverters(GithubTypeConverters.class)
public class RepoSearchResult {

    @NonNull
    private String query;
    private List<Integer> repoIds;
    private Integer totalCount;
    private Integer next;

    public RepoSearchResult(String query,
                            List<Integer> repoIds,
                            Integer totalCount,
                            @Nullable Integer next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }

    public String getQuery() {
        return query;
    }

    public List<Integer> getRepoIds() {
        return repoIds;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getNext() {
        return next;
    }
}

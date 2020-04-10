package com.android.example.github.api;

import androidx.annotation.Nullable;

import com.android.example.github.vo.Repo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
public class RepoSearchResponse {
    @SerializedName("total_count")
    private Integer total = 0;

    @SerializedName("items")
    private List<Repo> items;

    @Nullable
    private Integer nextPage = null;

    public RepoSearchResponse(
            Integer total,
            List<Repo> items) {
        this.total = total;
        this.items = items;
    }

    public Integer getTotal() {
        return total;
    }

    public List<Repo> getItems() {
        return items;
    }

    @Nullable
    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(@Nullable Integer nextPage) {
        this.nextPage = nextPage;
    }
}

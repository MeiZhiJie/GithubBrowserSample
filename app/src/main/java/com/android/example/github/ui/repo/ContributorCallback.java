package com.android.example.github.ui.repo;

import android.widget.ImageView;

import com.android.example.github.vo.Contributor;

public interface ContributorCallback {
    void callback(Contributor view, ImageView imageView);
}

package com.android.example.github.binding;

import androidx.databinding.BindingAdapter;
import android.view.View;


public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, Boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);

    }
}

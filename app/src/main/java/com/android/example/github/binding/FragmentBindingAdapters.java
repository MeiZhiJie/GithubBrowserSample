package com.android.example.github.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;

import javax.inject.Inject;

/**
 * Binding adapters that work with a fragment instance.
 */
//@OpenForTesting
public class FragmentBindingAdapters {
    private Fragment fragment;

    @Inject
    public FragmentBindingAdapters(Fragment fragment) {
        this.fragment = fragment;
    }

    @BindingAdapter(value = {"imageUrl", "imageRequestListener"}, requireAll = false)
    public void bindImage(ImageView imageView, @Nullable String url, @Nullable RequestListener<Drawable> listener) {
        Glide.with(fragment).load(url).listener(listener).into(imageView);
    }
}

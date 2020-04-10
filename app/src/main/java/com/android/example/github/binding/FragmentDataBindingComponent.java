package com.android.example.github.binding;

import android.widget.Adapter;

import androidx.databinding.DataBindingComponent;
import androidx.fragment.app.Fragment;

/**
 * A Data Binding Component implementation for fragments.
 */
public class FragmentDataBindingComponent implements DataBindingComponent {
    private Fragment fragment;

    public FragmentDataBindingComponent(Fragment fragment) {
        super();
        this.fragment = fragment;
    }
    @Override
    public FragmentBindingAdapters getFragmentBindingAdapters() {
        return new FragmentBindingAdapters(fragment);
    }
}

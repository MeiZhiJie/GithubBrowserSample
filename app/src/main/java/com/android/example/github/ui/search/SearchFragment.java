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

package com.android.example.github.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.github.AppExecutors;
import com.android.example.github.R;
import com.android.example.github.binding.FragmentDataBindingComponent;
import com.android.example.github.databinding.SearchFragmentBinding;
import com.android.example.github.di.Injectable;
import com.android.example.github.ui.common.RepoListAdapter;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class SearchFragment extends Fragment implements Injectable {
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    @Inject
    public AppExecutors appExecutors;

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private SearchFragmentBinding binding;
    private RepoListAdapter adapter;
    private SearchViewModel searchViewModel;

    @Override
    @Nullable
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.search_fragment,
                container,
                false,
                dataBindingComponent
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SearchViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        initRecyclerView();
        adapter = new RepoListAdapter(
                dataBindingComponent,
                appExecutors,
                true,
                repo ->
                        navController().navigate(
                                SearchFragmentDirections.showRepo(repo.getOwner().getLogin(), repo.getName())
                        )
        );
        binding.setQuery(searchViewModel.getQuery());
        binding.repoList.setAdapter(adapter);

        initSearchInputListener();

        binding.setCallback(() -> searchViewModel.refresh());
    }

    private void initSearchInputListener() {
        binding.input.setOnEditorActionListener((view, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view);
                return true;
            } else {
                return false;
            }
        });
        binding.input.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view);
                return true;
            } else {
                return false;
            }
        });
    }

    private void doSearch(View v) {
        String query = binding.input.getText().toString();
        // Dismiss keyboard
        dismissKeyboard(v.getWindowToken());
        searchViewModel.setQuery(query);
    }

    private void initRecyclerView() {

        binding.repoList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (lastPosition == adapter.getItemCount() - 1) {
                    searchViewModel.loadNextPage();
                }
            }
        });
        binding.setSearchResult(searchViewModel.getResults());
        searchViewModel.getResults().observe(getViewLifecycleOwner(),
                result ->  adapter.submitList((result != null) ? result.getData() : null));

        searchViewModel.getLoadMoreStatus().observe(getViewLifecycleOwner(), loadingMore -> {
            if (loadingMore == null) {
                binding.setLoadingMore(false);
            } else {
                binding.setLoadingMore(loadingMore.isRunning());
                String error = loadingMore.getErrorMessageIfNotHandled();
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        adapter = null;
    }

    private void dismissKeyboard(IBinder windowToken) {
        InputMethodManager imm;
        if (getActivity() != null) {
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * Created to be able to override in tests
     */
    protected NavController navController() {
        return Navigation.findNavController(getView());
    }
}

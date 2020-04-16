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

package com.android.example.github.ui.user;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.android.example.github.AppExecutors;
import com.android.example.github.R;
import com.android.example.github.binding.FragmentDataBindingComponent;
import com.android.example.github.databinding.UserFragmentBinding;
import com.android.example.github.di.Injectable;
import com.android.example.github.ui.common.RepoListAdapter;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

public class UserFragment extends Fragment implements Injectable {
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    @Inject
    public AppExecutors appExecutors;

    private UserFragmentBinding binding;
    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private UserViewModel userViewModel;
    private RepoListAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    @Nullable
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.user_fragment,
                container,
                false,
                dataBindingComponent
        );
        binding.setRetryCallback(() -> userViewModel.retry());

        Transition sharedElementEnterTransition = TransitionInflater.from(getContext()).inflateTransition(R.transition.move);

        // When the image is loaded, set the image request listener to start the transaction
        binding.setImageRequestListener(new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }
        });
        // Animation Watchdog - Make sure we don't wait longer than a second for the Glide image
        handler.postDelayed(()->startPostponedEnterTransition(), 1000);
        postponeEnterTransition();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserViewModel.class);
        UserFragmentArgs params = UserFragmentArgs.fromBundle(getArguments());
        userViewModel.setLogin(params.getLogin());
        binding.setArgs(params);

        binding.setUser(userViewModel.getUser());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        adapter = new RepoListAdapter(
                dataBindingComponent,
                appExecutors,
                false,
                repo -> navController().navigate(UserFragmentDirections.showRepo(
                        repo.getOwner().getLogin(),
                        repo.getName()))
        );
        binding.repoList.setAdapter(adapter);
        initRepoList();
    }

    private void initRepoList() {
        userViewModel.getRepositories().observe(getViewLifecycleOwner(), repos ->
                adapter.submitList((repos != null) ? repos.getData() : null));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        adapter = null;
    }
    /**
     * Created to be able to override in tests
     */
    protected NavController navController() {
        return Navigation.findNavController(getView());
    }

}

package com.android.example.github.ui.repo;

import android.os.Bundle;
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
import androidx.navigation.fragment.FragmentNavigator;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.android.example.github.AppExecutors;
import com.android.example.github.databinding.RepoFragmentBinding;
import com.android.example.github.di.Injectable;
import com.android.example.github.binding.FragmentDataBindingComponent;
import com.android.example.github.R;
import com.android.example.github.ui.common.RetryCallback;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * The UI Controller for displaying a Github Repo's information with its contributors.
 */
//@OpenForTesting
public class RepoFragment extends Fragment implements Injectable {
    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private RepoViewModel repoViewModel;

    @Inject
    public AppExecutors appExecutors;

    // mutable for testing
    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private RepoFragmentBinding binding;

    private ContributorAdapter adapter;

    private void initContributorList(RepoViewModel viewModel) {
        viewModel.getContributors().observe(getViewLifecycleOwner(), listResource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.getData() != null) {
                adapter.submitList(listResource.getData());
            } else{
                adapter.submitList(new ArrayList<>());
            }
        });
    }

    @Override
    @Nullable
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.repo_fragment,
                container,
                false
        );

        binding.setRetryCallback(new RetryCallback() {
            @Override
            public void  retry() {
                repoViewModel.retry();
            }
        });

        Transition sharedElementReturnTransition =
                TransitionInflater.from(getContext()).inflateTransition(R.transition.move);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RepoViewModel repoViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(RepoViewModel.class);
        RepoFragmentArgs params = RepoFragmentArgs.fromBundle(getArguments());
        repoViewModel.setId(params.getOwner(), params.getName());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setRepo( repoViewModel.getRepo());

        adapter = new ContributorAdapter(dataBindingComponent,
                appExecutors,
                (contributor, imageView) -> {
                    //FragmentNavigatorExtras()
                    FragmentNavigator.Extras.Builder extras =
                            new FragmentNavigator.Extras.Builder().addSharedElement(imageView, contributor.getLogin());
                    navController().navigate(
                            RepoFragmentDirections.showUser(contributor.getLogin()));
                }
        );

        binding.contributorList.setAdapter(adapter);
        postponeEnterTransition();
        binding.contributorList.getViewTreeObserver()
                .addOnPreDrawListener(() -> {
                    startPostponedEnterTransition();
                    return true;
                });
        initContributorList(repoViewModel);
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

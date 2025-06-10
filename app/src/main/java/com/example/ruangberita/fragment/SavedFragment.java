package com.example.ruangberita.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.ruangberita.R;
import com.example.ruangberita.adapters.NewsAdapter;
import com.example.ruangberita.database.DatabaseHelper;
import com.example.ruangberita.databinding.FragmentSavedBinding;
import com.example.ruangberita.models.News;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SavedFragment extends Fragment implements NewsAdapter.OnNewsClickListener {
    private FragmentSavedBinding binding;
    private NewsAdapter newsAdapter;
    private List<News> savedNewsList;
    private DatabaseHelper databaseHelper;
    private Executor executor;
    private Handler mainHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);

        initializeComponents();
        setupRecyclerView();
        setupSwipeRefresh();

        loadSavedNews();

        return binding.getRoot();
    }

    private void initializeComponents() {
        databaseHelper = new DatabaseHelper(getContext());
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        savedNewsList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(getContext(), savedNewsList, this);
        binding.recyclerViewSaved.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSaved.setAdapter(newsAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSavedNews();
            }
        });

        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorSecondary
        );
    }

    private void loadSavedNews() {
        showLoadingLayout();

        executor.execute(() -> {
            List<News> newsData = databaseHelper.getAllSavedNews();

            mainHandler.post(() -> {
                binding.swipeRefreshLayout.setRefreshing(false);

                savedNewsList.clear();
                savedNewsList.addAll(newsData);
                newsAdapter.notifyDataSetChanged();

                if (savedNewsList.isEmpty()) {
                    showEmptyLayout();
                } else {
                    showContentLayout();
                }

                // Update count in title if needed
                updateSavedCount();
            });
        });
    }

    private void updateSavedCount() {
        if (getActivity() != null && getActivity().getActionBar() != null) {
            String title = getString(R.string.saved) + " (" + savedNewsList.size() + ")";
            getActivity().getActionBar().setTitle(title);
        }
    }

    private void showLoadingLayout() {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showContentLayout() {
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmptyLayout() {
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNewsClick(News news) {
        // Navigate to detail activity
        // Intent will be implemented in DetailActivity
    }

    @Override
    public void onSaveClick(News news) {
        // This will always be unsave for saved news
        executor.execute(() -> {
            int result = databaseHelper.deleteNews(news.getLink());
            mainHandler.post(() -> {
                if (result > 0) {
                    // Remove from list
                    savedNewsList.remove(news);
                    newsAdapter.notifyDataSetChanged();

                    if (savedNewsList.isEmpty()) {
                        showEmptyLayout();
                    }

                    updateSavedCount();
                    Toast.makeText(getContext(), R.string.news_removed, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onShareClick(News news) {
        // Implementation for sharing news
        // This will be implemented with Intent
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh saved news when fragment becomes visible
        loadSavedNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        binding = null;
    }
}
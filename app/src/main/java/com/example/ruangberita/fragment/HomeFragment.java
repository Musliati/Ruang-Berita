package com.example.ruangberita.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.ruangberita.R;
import com.example.ruangberita.adapters.NewsAdapter;
import com.example.ruangberita.adapters.CategoryAdapter;
import com.example.ruangberita.api.ApiClient;
import com.example.ruangberita.database.DatabaseHelper;
import com.example.ruangberita.databinding.FragmentHomeBinding;
import com.example.ruangberita.models.News;
import com.example.ruangberita.models.NewsResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements NewsAdapter.OnNewsClickListener {
    private FragmentHomeBinding binding;
    private NewsAdapter newsAdapter;
    private CategoryAdapter categoryAdapter;
    private List<News> newsList;
    private List<News> filteredNewsList;
    private DatabaseHelper databaseHelper;
    private Executor executor;
    private Handler mainHandler;
    private String selectedCategory = "";

    // Categories
    private final String[] categories = {"", "nasional", "internasional", "ekonomi", "olahraga", "teknologi", "hiburan", "gaya-hidup"};
    private final String[] categoryNames = {"Semua", "Nasional", "Internasional", "Ekonomi", "Olahraga", "Teknologi", "Hiburan", "Gaya Hidup"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initializeComponents();
        setupRecyclerView();
        setupSearchView();
        setupCategoryChips();
        setupSwipeRefresh();
        setupRetryButton();

        loadNews();

        return binding.getRoot();
    }

    private void initializeComponents() {
        databaseHelper = new DatabaseHelper(getContext());
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        newsList = new ArrayList<>();
        filteredNewsList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(getContext(), filteredNewsList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewNews.setLayoutManager(layoutManager);
        binding.recyclerViewNews.setAdapter(newsAdapter);
        binding.recyclerViewNews.setHasFixedSize(true);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filterNews("");
                }
                return true;
            }
        });
    }

    private void setupCategoryChips() {
        for (int i = 0; i < categoryNames.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(categoryNames[i]);
            chip.setCheckable(true);
            chip.setTag(categories[i]);

            if (i == 0) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    selectedCategory = (String) chip.getTag();
                    loadNews();
                }
            });

            binding.chipGroupCategories.addView(chip);
        }

        binding.chipGroupCategories.setSingleSelection(true);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews();
            }
        });

        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorSecondary
        );
    }

    private void setupRetryButton() {
        if (binding.buttonRetry != null) {
            binding.buttonRetry.setOnClickListener(v -> loadNews());
        }
    }

    private void loadNews() {
        if (!isNetworkAvailable()) {
            showNoInternetLayout();
            return;
        }

        showLoadingLayout();

        Call<NewsResponse> call;
        if (selectedCategory.isEmpty()) {
            call = ApiClient.getApiService().getAllNews();
        } else {
            call = ApiClient.getApiService().getNewsByCategory(selectedCategory);
        }

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                // Check if binding is null or fragment is detached
                if (binding == null || !isAdded()) {
                    return;
                }

                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    if (newsResponse.getData() != null && !newsResponse.getData().isEmpty()) {
                        executor.execute(() -> {
                            List<News> newsData = newsResponse.getData();
                            for (News news : newsData) {
                                news.setCategory(selectedCategory);
                                news.setSaved(databaseHelper.isNewsSaved(news.getLink()));
                            }

                            mainHandler.post(() -> {
                                // Check binding again before updating UI
                                if (binding == null || !isAdded()) {
                                    return;
                                }
                                newsList.clear();
                                newsList.addAll(newsData);
                                filterNews(binding.searchView.getQuery().toString());
                                showContentLayout();
                            });
                        });
                    } else {
                        showEmptyLayout();
                    }
                } else {
                    showErrorLayout();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                // Check if binding is null or fragment is detached
                if (binding == null || !isAdded()) {
                    return;
                }
                binding.swipeRefreshLayout.setRefreshing(false);
                showErrorLayout();
            }
        });
    }

    private void filterNews(String query) {
        filteredNewsList.clear();

        if (query.isEmpty()) {
            filteredNewsList.addAll(newsList);
        } else {
            for (News news : newsList) {
                if (news.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        news.getContentSnippet().toLowerCase().contains(query.toLowerCase())) {
                    filteredNewsList.add(news);
                }
            }
        }

        newsAdapter.notifyDataSetChanged();

        if (filteredNewsList.isEmpty() && !query.isEmpty()) {
            binding.textEmptySearch.setText("Tidak ada berita yang ditemukan untuk \"" + query + "\"");
            showEmptyLayout();
        } else if (!filteredNewsList.isEmpty()) {
            showContentLayout();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showLoadingLayout() {
        if (binding == null) return;
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showContentLayout() {
        if (binding == null) return;
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        if (binding == null) return;
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmptyLayout() {
        if (binding == null) return;
        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showNoInternetLayout() {
        binding.swipeRefreshLayout.setRefreshing(false);
        showErrorLayout();
        if (binding.textError != null) {
            binding.textError.setText(R.string.no_internet);
        }
    }

    @Override
    public void onNewsClick(News news) {
        // Navigate to detail activity
        // Intent will be implemented in DetailActivity
    }

    @Override
    public void onSaveClick(News news) {
        executor.execute(() -> {
            if (news.isSaved()) {
                // Remove from saved
                int result = databaseHelper.deleteNews(news.getLink());
                mainHandler.post(() -> {
                    if (result > 0) {
                        news.setSaved(false);
                        newsAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), R.string.news_removed, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Save news
                long result = databaseHelper.saveNews(news);
                mainHandler.post(() -> {
                    if (result != -1) {
                        news.setSaved(true);
                        newsAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), R.string.news_saved, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onShareClick(News news) {
        // Implementation for sharing news
        // This will be implemented with Intent
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
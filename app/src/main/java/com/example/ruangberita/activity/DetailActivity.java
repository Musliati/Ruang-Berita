package com.example.ruangberita.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ruangberita.R;
import com.example.ruangberita.database.DatabaseHelper;
import com.example.ruangberita.databinding.ActivityDetailBinding;
import com.example.ruangberita.models.News;
import com.example.ruangberita.models.NewsImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private Executor executor;
    private Handler mainHandler;
    private News currentNews;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupToolbar();
        loadDataFromIntent();
        setupClickListeners();
    }

    private void initializeComponents() {
        databaseHelper = new DatabaseHelper(this);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Detail Berita");
        }
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("news_title");
            String content = intent.getStringExtra("news_content");
            String link = intent.getStringExtra("news_link");
            String date = intent.getStringExtra("news_date");
            String imageUrl = intent.getStringExtra("news_image");
            String category = intent.getStringExtra("news_category");
            isSaved = intent.getBooleanExtra("is_saved", false);

            // Create News object
            currentNews = new News();
            currentNews.setTitle(title);
            currentNews.setContentSnippet(content);
            currentNews.setLink(link);
            currentNews.setIsoDate(date);
            currentNews.setCategory(category);
            currentNews.setSaved(isSaved);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                NewsImage newsImage = new NewsImage();
                newsImage.setLarge(imageUrl);
                currentNews.setImage(newsImage);
            }

            displayNewsData();
        }
    }

    private void displayNewsData() {
        if (currentNews == null) return;

        binding.textTitle.setText(currentNews.getTitle());
        binding.textContent.setText(currentNews.getContentSnippet());
        binding.textDate.setText(formatDate(currentNews.getIsoDate()));

        if (currentNews.getCategory() != null && !currentNews.getCategory().isEmpty()) {
            binding.textCategory.setText(getCategoryDisplayName(currentNews.getCategory()));
        } else {
            binding.textCategory.setText("Berita");
        }

        // Load image
        String imageUrl = currentNews.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Glide.with(this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(binding.imageNews);
            } catch (Exception e) {
                binding.imageNews.setBackgroundColor(getResources().getColor(R.color.light_gray));
            }
        } else {
            binding.imageNews.setBackgroundColor(getResources().getColor(R.color.light_gray));
        }

        // Check if news is saved
        executor.execute(() -> {
            boolean saved = databaseHelper.isNewsSaved(currentNews.getLink());
            mainHandler.post(() -> {
                isSaved = saved;
                currentNews.setSaved(saved);
                invalidateOptionsMenu(); // Refresh menu
            });
        });
    }

    private void setupClickListeners() {
        binding.fabReadMore.setOnClickListener(v -> {
            if (currentNews != null && currentNews.getLink() != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getLink()));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        MenuItem saveItem = menu.findItem(R.id.action_save);
        MenuItem shareItem = menu.findItem(R.id.action_share);

        if (isSaved) {
            saveItem.setIcon(R.drawable.ic_bookmark_filled);
            saveItem.setTitle(R.string.unsave);
        } else {
            saveItem.setIcon(R.drawable.ic_bookmark_border);
            saveItem.setTitle(R.string.save);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_save) {
            toggleSaveNews();
            return true;
        } else if (itemId == R.id.action_share) {
            shareNews();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void toggleSaveNews() {
        if (currentNews == null) return;

        executor.execute(() -> {
            if (isSaved) {
                // Remove from saved
                int result = databaseHelper.deleteNews(currentNews.getLink());
                mainHandler.post(() -> {
                    if (result > 0) {
                        isSaved = false;
                        currentNews.setSaved(false);
                        invalidateOptionsMenu();
                        Toast.makeText(this, R.string.news_removed, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Save news
                long result = databaseHelper.saveNews(currentNews);
                mainHandler.post(() -> {
                    if (result != -1) {
                        isSaved = true;
                        currentNews.setSaved(true);
                        invalidateOptionsMenu();
                        Toast.makeText(this, R.string.news_saved, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void shareNews() {
        if (currentNews == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String shareText = currentNews.getTitle() + "\n\n" +
                currentNews.getContentSnippet() + "\n\n" +
                "Baca selengkapnya: " + currentNews.getLink() + "\n\n" +
                "Dibagikan dari Ruang Berita";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentNews.getTitle());

        Intent chooser = Intent.createChooser(shareIntent, "Bagikan berita melalui:");
        startActivity(chooser);
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Tidak diketahui";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(isoDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                return isoDate.substring(0, 10);
            } catch (Exception ex) {
                return "Tidak diketahui";
            }
        }
    }

    private String getCategoryDisplayName(String category) {
        switch (category) {
            case "nasional": return "Nasional";
            case "internasional": return "Internasional";
            case "ekonomi": return "Ekonomi";
            case "olahraga": return "Olahraga";
            case "teknologi": return "Teknologi";
            case "hiburan": return "Hiburan";
            case "gaya-hidup": return "Gaya Hidup";
            default: return "Berita";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
package com.example.ruangberita.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.ruangberita.R;
import com.example.ruangberita.database.DatabaseHelper;
import com.example.ruangberita.databinding.ActivityMainBinding;
import com.example.ruangberita.fragment.HomeFragment;
import com.example.ruangberita.fragment.SavedFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DatabaseHelper databaseHelper;
    private HomeFragment homeFragment;
    private SavedFragment savedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "Starting MainActivity");

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            databaseHelper = new DatabaseHelper(this);

            // Inisialisai fragment
            homeFragment = new HomeFragment();
            savedFragment = new SavedFragment();

            setSupportActionBar(binding.toolbar);

            // Set up bottom navigation WITHOUT Navigation Component
            setupBottomNavigation();

            // Load default fragment (Home)
            loadFragment(homeFragment);

            Log.d(TAG, "MainActivity initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity onCreate", e);
        }
    }

    private void setupBottomNavigation() {
        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                loadFragment(homeFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Beranda");
                }
                return true;
            } else if (itemId == R.id.navigation_saved) {
                loadFragment(savedFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Tersimpan");
                }
                return true;
            }
            return false;
        });

        // Set default selection
        binding.navView.setSelectedItemId(R.id.navigation_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Beranda");
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
            transaction.commit();
            Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.main_menu, menu);

            // Tetapkan item menu tema berdasarkan tema saat ini
            MenuItem themeItem = menu.findItem(R.id.action_theme);
            if (themeItem != null && databaseHelper != null) {
                String currentTheme = databaseHelper.getSetting("theme", "light");
                if ("dark".equals(currentTheme)) {
                    themeItem.setTitle(R.string.light_theme);
                } else {
                    themeItem.setTitle(R.string.dark_theme);
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating options menu", e);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_theme) {
                toggleTheme();
                return true;
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            Log.e(TAG, "Error handling menu selection", e);
            return false;
        }
    }

    private void toggleTheme() {
        try {
            String currentTheme = databaseHelper.getSetting("theme", "light");
            String newTheme = "light".equals(currentTheme) ? "dark" : "light";

            databaseHelper.saveSetting("theme", newTheme);

            if ("dark".equals(newTheme)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Buat ulang aktivitas untuk menerapkan tema
            recreate();
        } catch (Exception e) {
            Log.e(TAG, "Error toggling theme", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
    }
}
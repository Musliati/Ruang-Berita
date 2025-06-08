package com.example.ruangberita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.ruangberita.R;
import com.example.ruangberita.database.DatabaseHelper;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "Starting SplashActivity");
            setContentView(R.layout.activity_splash);

            // Initialize database
            initDatabase();

            // Apply saved theme
            applySavedTheme();

            // Navigate to MainActivity after delay
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting MainActivity", e);
                    }
                }
            }, SPLASH_DELAY);

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            // Fallback: langsung ke MainActivity
            startMainActivity();
        }
    }

    private void initDatabase() {
        try {
            databaseHelper = new DatabaseHelper(this);
            Log.d(TAG, "Database initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
            databaseHelper = null;
        }
    }

    private void applySavedTheme() {
        try {
            if (databaseHelper != null) {
                String theme = databaseHelper.getSetting("theme", "light");
                if ("dark".equals(theme)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                Log.d(TAG, "Theme applied: " + theme);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying theme", e);
            // Use default light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void startMainActivity() {
        try {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Fatal error starting MainActivity", e);
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
            Log.e(TAG, "Error closing database", e);
        }
    }
}
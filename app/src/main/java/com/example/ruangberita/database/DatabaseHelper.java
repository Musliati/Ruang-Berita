package com.example.ruangberita.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ruangberita.models.News;
import com.example.ruangberita.models.NewsImage;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ruang_berita.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_SAVED_NEWS = "saved_news";
    private static final String TABLE_SETTINGS = "settings";

    // Saved news table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_CONTENT_SNIPPET = "content_snippet";
    private static final String COLUMN_ISO_DATE = "iso_date";
    private static final String COLUMN_IMAGE_SMALL = "image_small";
    private static final String COLUMN_IMAGE_LARGE = "image_large";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_SAVED_TIMESTAMP = "saved_timestamp";

    // Settings table columns
    private static final String COLUMN_SETTING_KEY = "setting_key";
    private static final String COLUMN_SETTING_VALUE = "setting_value";

    // Create table statements
    private static final String CREATE_SAVED_NEWS_TABLE = "CREATE TABLE " + TABLE_SAVED_NEWS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_LINK + " TEXT UNIQUE NOT NULL,"
            + COLUMN_CONTENT_SNIPPET + " TEXT,"
            + COLUMN_ISO_DATE + " TEXT,"
            + COLUMN_IMAGE_SMALL + " TEXT,"
            + COLUMN_IMAGE_LARGE + " TEXT,"
            + COLUMN_CATEGORY + " TEXT,"
            + COLUMN_SAVED_TIMESTAMP + " INTEGER"
            + ")";

    private static final String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
            + COLUMN_SETTING_KEY + " TEXT PRIMARY KEY,"
            + COLUMN_SETTING_VALUE + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_SAVED_NEWS_TABLE);
            db.execSQL(CREATE_SETTINGS_TABLE);

            // Insert default theme setting
            ContentValues values = new ContentValues();
            values.put(COLUMN_SETTING_KEY, "theme");
            values.put(COLUMN_SETTING_VALUE, "light");
            db.insert(TABLE_SETTINGS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_NEWS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Saved News operations
    public long saveNews(News news) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_TITLE, news.getTitle());
            values.put(COLUMN_LINK, news.getLink());
            values.put(COLUMN_CONTENT_SNIPPET, news.getContentSnippet());
            values.put(COLUMN_ISO_DATE, news.getIsoDate());
            values.put(COLUMN_IMAGE_SMALL, news.getImage() != null ? news.getImage().getSmall() : "");
            values.put(COLUMN_IMAGE_LARGE, news.getImage() != null ? news.getImage().getLarge() : "");
            values.put(COLUMN_CATEGORY, news.getCategory());
            values.put(COLUMN_SAVED_TIMESTAMP, System.currentTimeMillis());

            long result = db.insert(TABLE_SAVED_NEWS, null, values);
            db.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<News> getAllSavedNews() {
        List<News> newsList = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_SAVED_NEWS + " ORDER BY " + COLUMN_SAVED_TIMESTAMP + " DESC";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    News news = new News();
                    news.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    news.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                    news.setLink(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK)));
                    news.setContentSnippet(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT_SNIPPET)));
                    news.setIsoDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ISO_DATE)));
                    news.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                    news.setSavedTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SAVED_TIMESTAMP)));
                    news.setSaved(true);

                    // Create image object
                    String imageSmall = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SMALL));
                    String imageLarge = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_LARGE));
                    NewsImage image = new NewsImage(imageSmall, imageLarge);
                    news.setImage(image);

                    newsList.add(news);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }

    public boolean isNewsSaved(String link) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_SAVED_NEWS + " WHERE " + COLUMN_LINK + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{link});

            boolean exists = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int deleteNews(String link) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete(TABLE_SAVED_NEWS, COLUMN_LINK + " = ?", new String[]{link});
            db.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getSavedNewsCount() {
        try {
            String countQuery = "SELECT * FROM " + TABLE_SAVED_NEWS;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Settings operations
    public void saveSetting(String key, String value) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_SETTING_KEY, key);
            values.put(COLUMN_SETTING_VALUE, value);

            db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key, String defaultValue) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + COLUMN_SETTING_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTING_KEY + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{key});

            String value = defaultValue;
            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
            }

            cursor.close();
            db.close();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
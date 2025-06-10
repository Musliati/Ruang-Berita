package com.example.ruangberita.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ruangberita.R;
import com.example.ruangberita.models.News;
import com.example.ruangberita.activity.DetailActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<News> newsList;
    private OnNewsClickListener listener;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
        void onSaveClick(News news);
        void onShareClick(News news);
    }

    public NewsAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        holder.textTitle.setText(news.getTitle());
        holder.textContent.setText(news.getContentSnippet());
        holder.textDate.setText(formatDate(news.getIsoDate()));

        // Load image with Glide
        String imageUrl = news.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.light_gray)
                        .error(R.color.light_gray)
                        .into(holder.imageNews);
            } catch (Exception e) {
                holder.imageNews.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
            }
        } else {
            holder.imageNews.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }

        // Set save button state
        if (news.isSaved()) {
            holder.buttonSave.setImageResource(R.drawable.ic_bookmark_filled);
            holder.buttonSave.setContentDescription(context.getString(R.string.unsave));
        } else {
            holder.buttonSave.setImageResource(R.drawable.ic_bookmark_border);
            holder.buttonSave.setContentDescription(context.getString(R.string.save));
        }

        // Set category if available
        if (news.getCategory() != null && !news.getCategory().isEmpty()) {
            holder.textCategory.setText(getCategoryDisplayName(news.getCategory()));
            holder.textCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textCategory.setVisibility(View.GONE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            // Navigate to DetailActivity
            Intent intent = new Intent(context, com.example.ruangberita.activity.DetailActivity.class);
            intent.putExtra("news_title", news.getTitle());
            intent.putExtra("news_content", news.getContentSnippet());
            intent.putExtra("news_link", news.getLink());
            intent.putExtra("news_date", news.getIsoDate());
            intent.putExtra("news_image", news.getImageUrl());
            intent.putExtra("news_category", news.getCategory());
            intent.putExtra("is_saved", news.isSaved());
            context.startActivity(intent);

            if (listener != null) {
                listener.onNewsClick(news);
            }
        });

        holder.buttonSave.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveClick(news);
            }
        });

        holder.buttonShare.setOnClickListener(v -> {
            shareNews(news);
            if (listener != null) {
                listener.onShareClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private void shareNews(News news) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String shareText = news.getTitle() + "\n\n" +
                news.getContentSnippet() + "\n\n" +
                "Baca selengkapnya: " + news.getLink() + "\n\n" +
                "Dibagikan dari Ruang Berita";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getTitle());

        Intent chooser = Intent.createChooser(shareIntent, "Bagikan berita melalui:");
        context.startActivity(chooser);
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Tidak diketahui";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(isoDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            // Fallback to simple date extraction
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

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageNews;
        TextView textTitle;
        TextView textContent;
        TextView textDate;
        TextView textCategory;
        ImageButton buttonSave;
        ImageButton buttonShare;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageNews = itemView.findViewById(R.id.image_news);
            textTitle = itemView.findViewById(R.id.text_title);
            textContent = itemView.findViewById(R.id.text_content);
            textDate = itemView.findViewById(R.id.text_date);
            textCategory = itemView.findViewById(R.id.text_category);
            buttonSave = itemView.findViewById(R.id.button_save);
            buttonShare = itemView.findViewById(R.id.button_share);
        }
    }
}
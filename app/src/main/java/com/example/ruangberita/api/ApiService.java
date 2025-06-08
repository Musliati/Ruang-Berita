package com.example.ruangberita.api;

import com.example.ruangberita.models.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface ApiService {
    @GET("cnn-news/")
    Call<NewsResponse> getAllNews();

    @GET("cnn-news/{category}")
    Call<NewsResponse> getNewsByCategory(@Path("category") String category);
}

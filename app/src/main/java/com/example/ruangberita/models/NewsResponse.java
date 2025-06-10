package com.example.ruangberita.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class NewsResponse {

    @SerializedName("data")
    private List<News> data;

    public List<News> getData() {
        return data;
    }

}

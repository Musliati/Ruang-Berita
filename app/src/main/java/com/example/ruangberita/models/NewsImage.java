package com.example.ruangberita.models;

import com.google.gson.annotations.SerializedName;
public class NewsImage {
    @SerializedName("small")
    private String small;

    @SerializedName("large")
    private String large;

    // Construktor
    public NewsImage() {}

    public NewsImage(String small, String large) {
        this.small = small;
        this.large = large;
    }

    //  gettters and setters
    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}

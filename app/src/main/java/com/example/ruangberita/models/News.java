package com.example.ruangberita.models;

import com.google.gson.annotations.SerializedName;
public class News {
    @SerializedName("title")
    private  String title;

    @SerializedName("link")
    private String link;

    @SerializedName("contentSnippet")
    private String contentSnippet;

    @SerializedName("isoDate")
    private String isoDate;

    @SerializedName("image")
    private NewsImage image;

    // Database fields
    private int id;
    private boolean isSaved;
    private String category;
    private long savedTimestamp;

    // Constructors
    public News() {}


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContentSnippet() {
        return contentSnippet;
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getIsoDate() {
        return isoDate;
    }

    public void setIsoDate(String isoDate) {
        this.isoDate = isoDate;
    }

    public NewsImage getImage() {
        return image;
    }

    public void setImage(NewsImage image) {
        this.image = image;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getSavedTimestamp() {
        return savedTimestamp;
    }

    public void setSavedTimestamp(long savedTimestamp) {
        this.savedTimestamp = savedTimestamp;
    }

    // Utility methods

    public String getImageUrl() {
        if (image != null && image.getLarge() != null) {
            return image.getLarge();
        } else if (image != null && image.getSmall() != null) {
            return image.getSmall();
        }
        return "";
    }
}

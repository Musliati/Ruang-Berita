package com.example.ruangberita.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class NewsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("messages")
    private String messages;

    @SerializedName("total")
    private  int total;

    @SerializedName("data")
    private List<News> data;

    // Construktor
    public NewsResponse() {}

    public NewsResponse(String message, int total, List<News> data) {
        this.message = message;
        this.total = total;
        this.data = data;
    }

    // Getter and setter
    public String getMessage() {
        return message != null ? message : messages ;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String  getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<News> getData() {
        return data;
    }

    public void setData(List<News> data) {
        this.data = data;
    }


}

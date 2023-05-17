package com.example.watchdog.network;

import java.util.Date;

public class Video {
    private String url;
    private String dateTime;

    public Video(String url, String dateTime) {
        this.url = url;
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

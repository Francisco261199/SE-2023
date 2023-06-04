package com.se.ding.network;

// Video data structure for retrofit
public class Video {
    private String id;
    private String path;
    private String datetime;

    public Video(String id, String path, String datetime) {
        this.id = id;
        this.path = path;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String dateTime) {
        this.datetime = datetime;
    }
}

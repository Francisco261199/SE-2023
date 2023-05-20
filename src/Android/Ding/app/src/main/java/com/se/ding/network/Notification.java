package com.se.ding.network;

public class Notification {
    private String videoID;
    private String dateTime;

    public Notification(String videoID, String dateTime) {
        this.videoID = videoID;
        this.dateTime = dateTime;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

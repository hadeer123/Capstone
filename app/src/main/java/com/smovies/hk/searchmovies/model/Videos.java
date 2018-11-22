package com.smovies.hk.searchmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Videos {

    @SerializedName("results")
    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}

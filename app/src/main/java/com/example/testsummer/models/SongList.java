package com.example.testsummer.models;

public class SongList {
    private String title;
    private String subTitle;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SongList(String title, String subTitle, String path) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }
}

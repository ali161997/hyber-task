package com.example.hyber_task.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("name")
    @Expose
    private String name;
    private boolean downloaded = false;
    private boolean downloading = false;
    private int downloadPercentage;
    private Long downloadID;

    public Item() {
        downloadID = 0L;
    }
    public Item(int id, String type, String url, String name) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.name = name;
        this.downloaded = false;
        this.downloading = false;
        this.downloadID = 0L;
    }

    public int getDownloadPercentage() {
        return downloadPercentage;
    }

    public void setDownloadPercentage(int downloadPercentage) {
        this.downloadPercentage = downloadPercentage;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public Long getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(Long downloadID) {
        this.downloadID = downloadID;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

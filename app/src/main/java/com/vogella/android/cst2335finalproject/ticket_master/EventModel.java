package com.vogella.android.cst2335finalproject.ticket_master;

/**
 * pojo class for event
 */
public class EventModel {

    long id;
    String name;
    String startingDate;
    String priceRange;
    String url;
    String bannerUrl;

    public EventModel() {
    }

    public EventModel(long id, String name, String startingDate, String priceRange, String url, String bannerUrl) {
        this.id = id;
        this.name = name;
        this.startingDate = startingDate;
        this.priceRange = priceRange;
        this.url = url;
        this.bannerUrl = bannerUrl;
    }

    public EventModel(String name, String startingDate, String priceRange, String url, String banner) {
        this.name = name;
        this.startingDate = startingDate;
        this.priceRange = priceRange;
        this.url = url;
        this.bannerUrl = banner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}


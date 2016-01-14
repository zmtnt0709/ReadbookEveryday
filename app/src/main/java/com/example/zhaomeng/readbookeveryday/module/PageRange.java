package com.example.zhaomeng.readbookeveryday.module;

/**
 * Created by zhaomeng on 2015/11/24.
 */
public class PageRange {
    public static final int LARGER = 1;
    public static final int ERROR = 2;
    public static final int SMALLER = 3;

    private int startPage;
    private int stopPage;
    private int totalPage;
    private int createDate;

    public PageRange() {

    }

    public PageRange(int startPage, int stopPage) {
        this.startPage = startPage;
        this.stopPage = stopPage;
        this.totalPage = stopPage - startPage + 1;
        this.createDate = timeMillToDate(System.currentTimeMillis());
    }

    public PageRange(int startPage, int stopPage, long createTime) {
        this.startPage = startPage;
        this.stopPage = stopPage;
        this.totalPage = stopPage - startPage + 1;
        this.createDate = timeMillToDate(createTime);
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getStopPage() {
        return stopPage;
    }

    public void setStopPage(int stopPage) {
        this.stopPage = stopPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCreateDate() {
        return createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }

    public long getCreateTime() {
        return dateToTimeMill(createDate);
    }

    public void setCreateDateWithTimeMill(long createTime) {
        this.createDate = timeMillToDate(createTime);
    }

    public int compare(PageRange pageRange) {
        if (startPage > pageRange.stopPage) {
            return LARGER;
        } else if (stopPage < pageRange.startPage) {
            return SMALLER;
        } else {
            return ERROR;
        }
    }

    public int timeMillToDate(long timeMill) {
        return (int) (timeMill / 1000 / 3600 / 24);
    }

    public long dateToTimeMill(int date) {
        return (long) date * 24 * 3600 * 1000;
    }
}

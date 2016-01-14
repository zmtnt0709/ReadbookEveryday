package com.example.zhaomeng.readbookeveryday.sqlite.dto;

import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.BookDao;
import com.example.zhaomeng.readbookeveryday.util.JacksonConverterUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
@DatabaseTable(daoClass = BookDao.class)
public class BookDto {
    @DatabaseField(generatedId = true)
    private int id;             //图书id自动生成
    @DatabaseField
    private String title;       //图书标题
    @DatabaseField
    private int hasReadPageNum; //已阅读页数
    @DatabaseField
    private int totalPageNum;   //总页数
    @DatabaseField
    private long createTime;    //创建时间
    @DatabaseField
    private long updateTime;    //最终修改时间
    @DatabaseField
    private String totalPageRangeList; //阅读区间列表
    @DatabaseField
    private String hasReadPageRangeList;//已读区间
    @DatabaseField
    private String shouldReadPageRangeList;//未读区间

    public BookDto() {

    }

    public BookDto(String title, int pageNum, List<PageRange> pageRangeList) {
        this.title = title;
        this.hasReadPageNum = 0;
        this.totalPageNum = pageNum;
        long currentTime = System.currentTimeMillis();
        createTime = currentTime;
        updateTime = currentTime;
        JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
        this.totalPageRangeList = jacksonConverterUtil.toJsonString(pageRangeList);
        this.hasReadPageRangeList = "";
        this.shouldReadPageRangeList = jacksonConverterUtil.toJsonString(pageRangeList);
    }

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

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public int getHasReadPageNum() {
        return hasReadPageNum;
    }

    public void setHasReadPageNum(int hasReadPageNum) {
        this.hasReadPageNum = hasReadPageNum;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTotalPageRangeList() {
        return totalPageRangeList;
    }

    public void setTotalPageRangeList(String totalPageRangeList) {
        this.totalPageRangeList = totalPageRangeList;
    }

    public String getHasReadPageRangeList() {
        return hasReadPageRangeList;
    }

    public void setHasReadPageRangeList(String hasReadPageRangeList) {
        this.hasReadPageRangeList = hasReadPageRangeList;
    }

    public String getShouldReadPageRangeList() {
        return shouldReadPageRangeList;
    }

    public void setShouldReadPageRangeList(String shouldReadPageRangeList) {
        this.shouldReadPageRangeList = shouldReadPageRangeList;
    }
}

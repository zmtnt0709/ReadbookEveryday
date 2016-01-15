package com.example.zhaomeng.readbookeveryday.sqlite.dto;

import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.ReadProgressDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zhaomeng on 2015/11/30.
 */
@DatabaseTable(daoClass = ReadProgressDao.class)
public class ReadProgressDto {
    @DatabaseField(generatedId = true)
    private int id;             //进度自动生成的id;
    @DatabaseField
    private int bookId;         //对应图书的id
    @DatabaseField
    private String title;       //阅读图书标题
    @DatabaseField
    private int readPageNum;    //已阅读页数
    @DatabaseField
    private int createDate;    //阅读时间

    public ReadProgressDto() {

    }

    public ReadProgressDto(BookModule bookModule, PageRange pageRange) {
        BookDto bookDto = bookModule.getBookDto();
        this.bookId = bookDto.getId();
        this.title = bookDto.getTitle();
        this.readPageNum = pageRange.getTotalPage();
        this.createDate = pageRange.getCreateDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReadPageNum() {
        return readPageNum;
    }

    public void setReadPageNum(int readPageNum) {
        this.readPageNum = readPageNum;
    }

    public int getCreateDate() {
        return createDate;
    }

    public void setCreateDate(int createDate) {
        this.createDate = createDate;
    }
}

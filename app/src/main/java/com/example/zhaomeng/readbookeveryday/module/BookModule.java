package com.example.zhaomeng.readbookeveryday.module;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.util.JacksonConverterUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaomeng on 2015/11/26.
 */
public class BookModule {
    private BookDto bookDto;
    private List<PageRange> totalPageRangeList;
    private List<PageRange> shouldReadPageList;
    private List<PageRange> hasReadPageList;

    public BookModule(BookDto bookDto) {
        this.bookDto = bookDto;
        JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
        totalPageRangeList = jacksonConverterUtil.jsonStringToCollection(bookDto.getTotalPageRangeList(), LinkedList.class, PageRange.class);
        shouldReadPageList = jacksonConverterUtil.jsonStringToCollection(bookDto.getShouldReadPageRangeList(), LinkedList.class, PageRange.class);
        hasReadPageList = jacksonConverterUtil.jsonStringToCollection(bookDto.getHasReadPageRangeList(), LinkedList.class, PageRange.class);
        if (totalPageRangeList == null) {
            totalPageRangeList = new LinkedList<>();
        }
        if (shouldReadPageList == null) {
            shouldReadPageList = new LinkedList<>();
        }
        if (hasReadPageList == null) {
            hasReadPageList = new LinkedList<>();
        }
    }

    public List<PageRange> getTotalPageRangeList() {
        return totalPageRangeList;
    }

    public void setTotalPageRangeList(List<PageRange> totalPageRangeList) {
        this.totalPageRangeList = totalPageRangeList;
    }

    public List<PageRange> getShouldReadPageList() {
        return shouldReadPageList;
    }

    public void setShouldReadPageList(List<PageRange> shouldReadPageList) {
        this.shouldReadPageList = shouldReadPageList;
    }

    public List<PageRange> getHasReadPageList() {
        return hasReadPageList;
    }

    public void setHasReadPageList(List<PageRange> hasReadPageList) {
        this.hasReadPageList = hasReadPageList;
    }

    public BookDto getBookDto() {
        return bookDto;
    }

    public void setBookDto(BookDto bookDto) {
        this.bookDto = bookDto;
    }

    public void notifyAddedHasReadPageRange() {
        bookDto.setHasReadPageRangeList(JacksonConverterUtil.getInstance().toJsonString(hasReadPageList));
        bookDto.setShouldReadPageRangeList(JacksonConverterUtil.getInstance().toJsonString(shouldReadPageList));
        setHasReadPageNum();
        setUpdateTime();
    }

    private void setHasReadPageNum() {
        if (hasReadPageList == null || hasReadPageList.isEmpty()) {
            bookDto.setHasReadPageNum(0);
            return;
        }
        int tempHasReadPageNum = 0;
        for (int i = 0; i < hasReadPageList.size(); i++) {
            tempHasReadPageNum += hasReadPageList.get(i).getTotalPage();
        }
        bookDto.setHasReadPageNum(tempHasReadPageNum);
    }

    private void setUpdateTime() {
        bookDto.setUpdateTime(System.currentTimeMillis());
    }
}

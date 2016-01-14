package com.example.zhaomeng.readbookeveryday.module;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaomeng on 2015/12/25.
 */
public class ReadProgressToShow {

    private int readPageNumTotal;    //已阅读页数
    private int readDate;    //阅读时间
    private List<ReadProgressDto> readProgress;

    public ReadProgressToShow(ReadProgressDto readProgressDto) {
        readPageNumTotal = readProgressDto.getReadPageNum();
        readDate = readProgressDto.getCreateDate();
        readProgress = new ArrayList<>();
        readProgress.add(readProgressDto);
    }

    public void addReadProgress(ReadProgressDto readProgressDto) {
        readProgress.add(readProgressDto);
        readPageNumTotal += readProgressDto.getReadPageNum();
    }

    public int getReadPageNumTotal() {
        return readPageNumTotal;
    }

    public void setReadPageNumTotal(int readPageNumTotal) {
        this.readPageNumTotal = readPageNumTotal;
    }

    public int getReadDate() {
        return readDate;
    }

    public void setReadDate(int readDate) {
        this.readDate = readDate;
    }

    public List<ReadProgressDto> getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(List<ReadProgressDto> readProgress) {
        this.readProgress = readProgress;
    }
}

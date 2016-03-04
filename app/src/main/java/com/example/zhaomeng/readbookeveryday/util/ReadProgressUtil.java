package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;

import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.module.ReadProgressToShow;
import com.example.zhaomeng.readbookeveryday.sqlite.ReadBookSQLiteOpenHelper;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.ReadProgressDao;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhaomeng on 2016/3/4.
 */
public class ReadProgressUtil {
    private static ReadProgressUtil instance;
    private ReadProgressDao readProgressDao;
    private ReadBookSQLiteOpenHelper bookSQLiteOpenHelper;

    public static ReadProgressUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (ReadProgressUtil.class) {
                if (instance == null) {
                    instance = new ReadProgressUtil(context);
                }
            }
        }
        return instance;
    }

    private ReadProgressUtil(Context context) {
        bookSQLiteOpenHelper = new ReadBookSQLiteOpenHelper(context);
    }

    private ReadProgressDao getReadProgressDao() {
        if (readProgressDao == null) {
            try {
                readProgressDao = bookSQLiteOpenHelper.getDao(ReadProgressDto.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return readProgressDao;
    }

    public void updateReadProgress(BookModule bookModule, PageRange hasReadPageRange) {
        ReadProgressDao readProgressDao = getReadProgressDao();
        ReadProgressDto readProgressDto = new ReadProgressDto(bookModule, hasReadPageRange);
        try {
            readProgressDao.createOrUpdate(readProgressDto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ReadProgressToShow> getAllReadProgressToShow() {
        ReadProgressDao readProgressDao = getReadProgressDao();
        List<ReadProgressDto> readProgressDtoList = readProgressDao.getAllReadProgress();

        List<ReadProgressToShow> readProgressToShowList = new ArrayList<>();

        boolean isReadDateExist;
        for (ReadProgressDto readProgressDto : readProgressDtoList) {
            isReadDateExist = false;
            for (ReadProgressToShow readProgressToShow : readProgressToShowList) {
                if (readProgressDto.getCreateDate() == readProgressToShow.getReadDate()) {
                    isReadDateExist = true;
                    readProgressToShow.addReadProgress(readProgressDto);
                    break;
                }
            }

            if (!isReadDateExist) {
                ReadProgressToShow readProgressToShow = new ReadProgressToShow(readProgressDto);
                readProgressToShowList.add(readProgressToShow);
            }
        }
        SortReadProgress sortReadProgress = new SortReadProgress();
        Collections.sort(readProgressToShowList, sortReadProgress);

        return readProgressToShowList;
    }

    public void deleteReadProgress(PageRange deleteReadPageRange, BookModule bookModule) {
        ReadProgressDao readProgressDao = getReadProgressDao();
        List<ReadProgressDto> readProgressDtoList = readProgressDao.getAllReadProgress();
        int deleteProgressId = -1;
        for (ReadProgressDto readProgressDto : readProgressDtoList) {
            if (readProgressDto.getCreateDate() == deleteReadPageRange.getCreateDate()
                    && readProgressDto.getBookId() == bookModule.getBookDto().getId()
                    && readProgressDto.getReadPageNum() == deleteReadPageRange.getTotalPage()) {
                deleteProgressId = readProgressDto.getId();
                break;
            }
        }
        if (deleteProgressId == -1) return;

        readProgressDao.deleteProgressById(deleteProgressId);
    }

    class SortReadProgress implements Comparator<ReadProgressToShow> {

        @Override
        public int compare(ReadProgressToShow left, ReadProgressToShow right) {
            if (left.getReadDate() < right.getReadDate()) {
                return 1;
            } else if (left.getReadDate() > right.getReadDate()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}

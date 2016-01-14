package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;
import android.widget.DatePicker;

import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.module.ReadProgressToShow;
import com.example.zhaomeng.readbookeveryday.sqlite.ReadBookSQLiteOpenHelper;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.BookDao;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.ReadProgressDao;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class BookUtil {
    private static BookUtil instance;
    private Context context;
    private ReadBookSQLiteOpenHelper bookSQLiteOpenHelper;
    private BookDao bookDao;
    private ReadProgressDao readProgressDao;

    public static BookUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (BookUtil.class) {
                if (instance == null) {
                    instance = new BookUtil(context);
                }
            }
        }
        return instance;
    }

    private BookUtil(Context context) {
        this.context = context;
        bookSQLiteOpenHelper = new ReadBookSQLiteOpenHelper(context);
    }

    private BookDao getBookDao() {
        if (bookDao == null) {
            try {
                bookDao = bookSQLiteOpenHelper.getDao(BookDto.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookDao;
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

    public void createBook(String bookNameString, List<PageRange> pageRangeList) {
        BookDao dao = getBookDao();
        if (dao == null) return;

        int totalPages = getTotalPages(pageRangeList);
        BookDto bookDto = new BookDto(bookNameString, totalPages, pageRangeList);
        try {
            dao.createOrUpdate(bookDto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BookDto> getAllBookList() {
        BookDao dao = getBookDao();
        if (dao == null) return null;

        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPageRangeList(List<PageRange> pageRangeList, PageRange pageRange) {

        //如果列表为空，直接加入列表
        if (pageRangeList.isEmpty()) {
            pageRangeList.add(pageRange);
            return true;
        }
        //如果列表不为空，则插入到列表中，列表按从小到大排列
        for (int i = 0; i < pageRangeList.size(); i++) {
            int result = pageRangeList.get(i).compare(pageRange);
            if (result == PageRange.LARGER) {
                pageRangeList.add(i, pageRange);
                return true;
            } else if (result == PageRange.ERROR) {
                return false;
            }
        }
        //如果页面区间，比列表中的所有区间都大，则插入到列表最后。
        pageRangeList.add(pageRange);
        return true;
    }

    public int getTotalPages(List<PageRange> pageRangeList) {
        if (pageRangeList == null || pageRangeList.isEmpty())
            return 0;

        int totalPages = 0;
        for (PageRange pageRange : pageRangeList) {
            totalPages += pageRange.getTotalPage();
        }
        return totalPages;
    }

    public BookDto getBookById(int id) {
        if (id == -1) return null;

        BookDao dao = getBookDao();
        return dao.getBookById(id);
    }

    public void addHasReadPageRange(BookModule bookModule, int pageStartInt, int pageStopInt, DatePicker datePicker) {
        //获取设置的阅读时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),16,0,0);
        //更新bookModule
        PageRange hasReadPageRange = new PageRange(pageStartInt, pageStopInt, calendar.getTimeInMillis());
        addPageRangeList(bookModule.getHasReadPageList(), hasReadPageRange);
        removeFromPageRangeList(bookModule.getShouldReadPageList(), hasReadPageRange);
        bookModule.notifyAddedHasReadPageRange();
        //更新数据库
        BookDao bookDao = getBookDao();
        ReadProgressDao readProgressDao = getReadProgressDao();
        ReadProgressDto readProgressDto = new ReadProgressDto(bookModule, hasReadPageRange);
        try {
            bookDao.createOrUpdate(bookModule.getBookDto());
            readProgressDao.createOrUpdate(readProgressDto);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean removeFromPageRangeList(List<PageRange> pageRangeList, PageRange pageRange) {
        PageRange pageRangeTemp = null;
        int i;
        for (i = 0; i < pageRangeList.size(); i++) {
            pageRangeTemp = pageRangeList.get(i);
            if (pageRangeTemp.getStartPage() <= pageRange.getStartPage() && pageRangeTemp.getStopPage() >= pageRange.getStopPage()) {
                pageRangeList.remove(i);
                break;
            }
        }
        if (pageRangeTemp == null) return false;

        if (pageRangeTemp.getStopPage() > pageRange.getStopPage()) {
            pageRangeList.add(i, new PageRange(pageRange.getStopPage() + 1, pageRangeTemp.getStopPage()));
        }
        if (pageRangeTemp.getStartPage() < pageRange.getStartPage()) {
            pageRangeList.add(i, new PageRange(pageRangeTemp.getStartPage(), pageRange.getStartPage() - 1));
        }
        return true;
    }


    public List<ReadProgressToShow> getAllReadProgress() {
        ReadProgressDao readProgressDao = getReadProgressDao();
        List<ReadProgressDto> readProgressDtoList = readProgressDao.getAllReadProgress();

        List<ReadProgressToShow> readProgressToShowList = new ArrayList<>();

        boolean isReadDateExist;
        for(ReadProgressDto readProgressDto:readProgressDtoList) {
            isReadDateExist = false;
            for(ReadProgressToShow readProgressToShow:readProgressToShowList){
                if(readProgressDto.getCreateDate() == readProgressToShow.getReadDate()){
                    isReadDateExist = true;
                    readProgressToShow.addReadProgress(readProgressDto);
                    break;
                }
            }

            if(!isReadDateExist){
                ReadProgressToShow readProgressToShow = new ReadProgressToShow(readProgressDto);
                readProgressToShowList.add(readProgressToShow);
            }
        }
        SortReadProgress sortReadProgress = new SortReadProgress();
        Collections.sort(readProgressToShowList,sortReadProgress);

        return readProgressToShowList;
    }

    class SortReadProgress implements Comparator<ReadProgressToShow>{

        @Override
        public int compare(ReadProgressToShow left, ReadProgressToShow right) {
            if(left.getReadDate() < right.getReadDate()){
                return -1;
            }else if(left.getReadDate()>right.getReadDate()){
                return 1;
            }else {
                return 0;
            }
        }
    }

}

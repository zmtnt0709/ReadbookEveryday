package com.example.zhaomeng.readbookeveryday.util;

import android.content.Context;
import android.widget.DatePicker;

import com.example.zhaomeng.readbookeveryday.module.BookModule;
import com.example.zhaomeng.readbookeveryday.module.PageRange;
import com.example.zhaomeng.readbookeveryday.sqlite.ReadBookSQLiteOpenHelper;
import com.example.zhaomeng.readbookeveryday.sqlite.dao.BookDao;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class BookUtil {
    private static BookUtil instance;
    private Context context;
    private ReadBookSQLiteOpenHelper bookSQLiteOpenHelper;
    private BookDao bookDao;
    private ReadProgressUtil readProgressUtil;

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
        readProgressUtil = ReadProgressUtil.getInstance(context);
    }

    public BookDao getBookDao() {
        if (bookDao == null) {
            try {
                bookDao = bookSQLiteOpenHelper.getDao(BookDto.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookDao;
    }

    /**
     * 新建book实例
     *
     * @param bookNameString book的名字
     * @param pageRangeList  book的需要阅读区间
     * @param imagePath      book封面图片的地址
     */
    public void createBook(String bookNameString, List<PageRange> pageRangeList, String imagePath) {
        int totalPages = getTotalPages(pageRangeList);
        BookDto bookDto = new BookDto(bookNameString, totalPages, pageRangeList, imagePath);
        createOrUpdateBookDto(bookDto);
    }

    /**
     * 升级或创建book表实例
     *
     * @param bookDto 需要升级的book类
     */
    public void createOrUpdateBookDto(BookDto bookDto) {
        BookDao dao = getBookDao();
        if (dao == null) return;

        try {
            dao.createOrUpdate(bookDto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BookDto> getAllBookListToShow() {
        BookDao dao = getBookDao();
        if (dao == null) return null;

        List<BookDto> bookList = dao.getAllBookList();
        if(bookList == null) return null;

        Collections.sort(bookList, new SortBookList());
        return bookList;
    }

    public void deleteBook(BookDto bookDto) {
        //删除readProgress表实例
        if (!bookDto.getHasReadPageRangeList().isEmpty() && !bookDto.getHasReadPageRangeList().equals("")) {
            JacksonConverterUtil jacksonConverterUtil = JacksonConverterUtil.getInstance();
            List<PageRange> hasReadPageList = jacksonConverterUtil.jsonStringToCollection(bookDto.getHasReadPageRangeList(), LinkedList.class, PageRange.class);
            for (PageRange deletePageRange : hasReadPageList) {
                readProgressUtil.deleteReadProgress(deletePageRange, bookDto.getId());
            }
        }
        //删除book表
        BookDao dao = getBookDao();
        if (dao == null)
            return;

        dao.deleteBookById(bookDto.getId());
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
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 16, 0, 0);
        //更新bookModule
        PageRange hasReadPageRange = new PageRange(pageStartInt, pageStopInt, timeMillToDate(calendar.getTimeInMillis()));
        addPageRangeList(bookModule.getHasReadPageList(), hasReadPageRange);
        removeFromPageRangeList(bookModule.getShouldReadPageList(), hasReadPageRange);
        bookModule.notifyAddedHasReadPageRange();
        //更新数据库book表;
        BookDao bookDao = getBookDao();
        try {
            bookDao.createOrUpdate(bookModule.getBookDto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //更新数据库readProgress表
        readProgressUtil.updateReadProgress(bookModule, hasReadPageRange);
    }

    public void addTotalPageRange(int pageStartInt, int pageStopInt, BookModule bookModule) {
        PageRange addPageRange = new PageRange(pageStartInt, pageStopInt, timeMillToDate(System.currentTimeMillis()));
        //更新totalPageRange
        List<PageRange> totalPageRangeList = bookModule.getTotalPageRangeList();
        addPageRangeList(totalPageRangeList, addPageRange);
        //更新shouldReadPageRange
        List<PageRange> shouldReadPageRangeList = bookModule.getShouldReadPageList();
        addPageRangeList(shouldReadPageRangeList, addPageRange);
        bookModule.notifyAddedTotalPageRange();
        //更新数据库
        BookDao bookDao = getBookDao();
        try {
            bookDao.createOrUpdate(bookModule.getBookDto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            pageRangeList.add(i, new PageRange(pageRange.getStopPage() + 1, pageRangeTemp.getStopPage(), timeMillToDate(System.currentTimeMillis())));
        }
        if (pageRangeTemp.getStartPage() < pageRange.getStartPage()) {
            pageRangeList.add(i, new PageRange(pageRangeTemp.getStartPage(), pageRange.getStartPage() - 1, timeMillToDate(System.currentTimeMillis())));
        }
        return true;
    }

    public void deleteHasReadPageRange(PageRange deleteReadPageRange, BookModule bookModule) {
        //删除已读列表
        bookModule.getHasReadPageList().remove(deleteReadPageRange);
        //恢复未读列表
        resetShowReadPageRangeList(deleteReadPageRange, bookModule);
        //更新数据库
        bookModule.notifyAddedHasReadPageRange();
        BookDao bookDao = getBookDao();
        try {
            bookDao.createOrUpdate(bookModule.getBookDto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //删除读书进度表
        readProgressUtil.deleteReadProgress(deleteReadPageRange, bookModule.getBookDto().getId());
    }

    private void resetShowReadPageRangeList(PageRange deleteReadPageRange, BookModule bookModule) {
        //取出删除区间所在的总区间
        PageRange totalPageRange = null;
        for (PageRange pageRange : bookModule.getTotalPageRangeList()) {
            if (pageRange.getStartPage() <= deleteReadPageRange.getStartPage() && pageRange.getStopPage() >= deleteReadPageRange.getStopPage()) {
                totalPageRange = pageRange;
                break;
            }
        }
        if (totalPageRange == null) return;
        //取出在总区间内的所有未读区间
        int i = 0;
        List<PageRange> shouldReadPageList = bookModule.getShouldReadPageList();
        while (i < shouldReadPageList.size() && shouldReadPageList.get(i).getStopPage() < deleteReadPageRange.getStartPage()) {
            i++;
        }
        if (i < shouldReadPageList.size()) {
            PageRange nextPageRange = shouldReadPageList.get(i);
            if (nextPageRange.getStartPage() == deleteReadPageRange.getStopPage() + 1
                    && nextPageRange.getStartPage() >= totalPageRange.getStartPage()
                    && nextPageRange.getStopPage() <= totalPageRange.getStopPage()) {
                deleteReadPageRange.setStopPage(nextPageRange.getStopPage());
                shouldReadPageList.remove(i);
            }
        }
        if (i - 1 >= 0) {
            PageRange prePageRange = shouldReadPageList.get(i - 1);
            if (prePageRange.getStopPage() == deleteReadPageRange.getStartPage() - 1
                    && prePageRange.getStartPage() >= totalPageRange.getStartPage()
                    && prePageRange.getStopPage() <= totalPageRange.getStopPage()) {
                deleteReadPageRange.setStartPage(prePageRange.getStartPage());
                shouldReadPageList.remove(i - 1);
                i--;
            }
        }
        shouldReadPageList.add(i, deleteReadPageRange);
    }

    public int timeMillToDate(long timeMill) {
        return (int) (timeMill / 1000 / 3600 / 24);
    }

    public long dateToTimeMill(int date) {
        return (long) date * 24 * 3600 * 1000;
    }

    private class SortBookList implements Comparator<BookDto> {

        @Override
        public int compare(BookDto left, BookDto right) {
            if (left.getUpdateTime() < right.getUpdateTime()) {
                return 1;
            } else if (left.getUpdateTime() > right.getUpdateTime()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}

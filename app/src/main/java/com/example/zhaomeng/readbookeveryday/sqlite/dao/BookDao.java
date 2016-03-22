package com.example.zhaomeng.readbookeveryday.sqlite.dao;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class BookDao extends BaseDaoImpl<BookDto,String>{
    public BookDao(Class<BookDto> dataClass) throws SQLException {
        super(dataClass);
    }

    public BookDao(ConnectionSource connectionSource, Class<BookDto> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public BookDao(ConnectionSource connectionSource, DatabaseTableConfig<BookDto> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public List<BookDto> getAllBookList() {
        List<BookDto> bookList = null;
        try {
            bookList = queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public BookDto getBookById(int id) {
        QueryBuilder<BookDto, String> builder = queryBuilder();
        try {
            builder.where().eq("id",id);
            return queryForFirst(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteBookById(int id){
        DeleteBuilder<BookDto,String> builder = deleteBuilder();
        try {
            builder.where().eq("id",id);
            delete(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

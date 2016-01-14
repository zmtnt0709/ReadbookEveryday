package com.example.zhaomeng.readbookeveryday.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.BookDto;
import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by zhaomeng on 2015/10/20.
 */
public class ReadBookSQLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATA_BASE_NAME = "readBook";
    private static final int DATA_BASE_VERSION = 2;

    public ReadBookSQLiteOpenHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BookDto.class);
            TableUtils.createTable(connectionSource, ReadProgressDto.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                try {
                    TableUtils.createTable(connectionSource, ReadProgressDto.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }
}

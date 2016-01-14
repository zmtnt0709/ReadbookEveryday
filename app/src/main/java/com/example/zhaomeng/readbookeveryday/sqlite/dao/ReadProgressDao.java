package com.example.zhaomeng.readbookeveryday.sqlite.dao;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhaomeng on 2015/11/30.
 */
public class ReadProgressDao extends BaseDaoImpl<ReadProgressDto,String> {
    protected ReadProgressDao(Class<ReadProgressDto> dataClass) throws SQLException {
        super(dataClass);
    }

    protected ReadProgressDao(ConnectionSource connectionSource, Class<ReadProgressDto> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    protected ReadProgressDao(ConnectionSource connectionSource, DatabaseTableConfig<ReadProgressDto> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    public List<ReadProgressDto> getAllReadProgress() {
        try {
            return queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.zhaomeng.readbookeveryday.sqlite.dao;

import com.example.zhaomeng.readbookeveryday.sqlite.dto.ReadProgressDto;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhaomeng on 2015/11/30.
 */
public class ReadProgressDao extends BaseDaoImpl<ReadProgressDto,String> {
    public ReadProgressDao(Class<ReadProgressDto> dataClass) throws SQLException {
        super(dataClass);
    }

    public ReadProgressDao(ConnectionSource connectionSource, Class<ReadProgressDto> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public ReadProgressDao(ConnectionSource connectionSource, DatabaseTableConfig<ReadProgressDto> tableConfig) throws SQLException {
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

    public void deleteProgressById(int deleteProgressId) {
        DeleteBuilder<ReadProgressDto,String> deleteBuilder = deleteBuilder();
        try{
            deleteBuilder.where().eq("id",deleteProgressId);
            delete(deleteBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

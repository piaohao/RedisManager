package org.piaohao.redisManager.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    public abstract T mapRow(ResultSet rs, int index) throws SQLException;
}
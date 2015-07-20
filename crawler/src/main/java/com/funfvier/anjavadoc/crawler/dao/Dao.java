package com.funfvier.anjavadoc.crawler.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by lshi on 17.07.2015.
 */
public interface Dao {
    public Connection getConnection() throws SQLException;
}

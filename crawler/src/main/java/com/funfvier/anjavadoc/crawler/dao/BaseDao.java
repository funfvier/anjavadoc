package com.funfvier.anjavadoc.crawler.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by lshi on 17.07.2015.
 */
public abstract class BaseDao implements Dao {
    private final String DB_PATH;

    public BaseDao() {
        DB_PATH = "jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb";
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_PATH);
        return connection;
    }
}

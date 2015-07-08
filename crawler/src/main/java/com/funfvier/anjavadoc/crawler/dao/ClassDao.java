package com.funfvier.anjavadoc.crawler.dao;

import com.funfvier.anjavadoc.crawler.entity.JDClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

/**
 * Created by lshi on 03.07.2015.
 */
public class ClassDao {
    private static final Logger log = LogManager.getLogger(ClassDao.class);

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            Statement delSt = connection.createStatement();
            delSt.executeUpdate("delete from jd_classes");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public void saveToDb(JDClass jdClass) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");

            PreparedStatement pst = connection.prepareStatement("insert into jd_classes " +
                    "(_id, name, desc_short, desc_long, package_id, is_interface, is_class, is_enum, is_exception, is_annotation) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pst.setInt(1, jdClass.getId());
            pst.setString(2, jdClass.getName());
            pst.setString(3, jdClass.getShortDescription());
            pst.setString(4, jdClass.getLongDescription());
            pst.setInt(5, jdClass.getPackageId());
            pst.setBoolean(6, jdClass.getClassType().isInterface());
            pst.setBoolean(7, jdClass.getClassType().isClass());
            pst.setBoolean(8, jdClass.getClassType().isEnum());
            pst.setBoolean(9, jdClass.getClassType().isException());
            pst.setBoolean(10, jdClass.getClassType().isAnnotation());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public void saveToDb(Collection<JDClass> classes) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");

            PreparedStatement pst = connection.prepareStatement("insert into jd_classes " +
                    "(_id, name, desc_short, desc_long, package_id, is_interface, is_class, is_enum, is_exception, is_annotation, is_error) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (JDClass jdClass : classes) {
                pst.setInt(1, jdClass.getId());
                pst.setString(2, jdClass.getName());
                pst.setString(3, jdClass.getShortDescription());
                pst.setString(4, jdClass.getLongDescription());
                pst.setInt(5, jdClass.getPackageId());
                pst.setBoolean(6, jdClass.getClassType().isInterface());
                pst.setBoolean(7, jdClass.getClassType().isClass());
                pst.setBoolean(8, jdClass.getClassType().isEnum());
                pst.setBoolean(9, jdClass.getClassType().isException());
                pst.setBoolean(10, jdClass.getClassType().isAnnotation());
                pst.setBoolean(11, jdClass.getClassType().isError());
                pst.addBatch();
            }
            pst.executeBatch();
            pst.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}

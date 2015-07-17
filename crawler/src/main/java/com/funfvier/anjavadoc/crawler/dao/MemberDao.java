package com.funfvier.anjavadoc.crawler.dao;

import com.funfvier.anjavadoc.crawler.entity.JDClass;
import com.funfvier.anjavadoc.crawler.entity.JDMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lshi on 03.07.2015.
 */
public class MemberDao {
    private static final Logger log = LogManager.getLogger(MemberDao.class);

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
            delSt.executeUpdate("delete from jd_members");
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

    public void saveToDb(JDMethod jdMethod) {
        Set<String> set = new HashSet();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            PreparedStatement pst = connection.prepareStatement("insert into jd_members (_id, name, desc_short, desc_long, class_id, type_id) values (?, ?, ?, ?, ?, ?)");
            if (set.contains(jdMethod.getName())) return;
            set.add(jdMethod.getName());
            pst.setInt(1, jdMethod.getId());
            pst.setString(2, jdMethod.getName());
            pst.setString(3, jdMethod.getShortDescription());
            pst.setString(4, jdMethod.getLongDescription());
            pst.setInt(5, jdMethod.getClassId());
            pst.setInt(6, jdMethod.getType().getId());
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

    public void saveToDb(Collection<JDMethod> members) {
        Set<String> set = new HashSet();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            PreparedStatement pst = connection.prepareStatement("insert into jd_members (_id, name, desc_short, desc_long, class_id, type_id) values (?, ?, ?, ?, ?, ?)");
            for(JDMethod jdMethod : members) {
                if (set.contains(jdMethod.getName())) continue;
                set.add(jdMethod.getName());
                pst.setInt(1, jdMethod.getId());
                pst.setString(2, jdMethod.getName());
                pst.setString(3, jdMethod.getShortDescription());
                pst.setString(4, jdMethod.getLongDescription());
                pst.setInt(5, jdMethod.getClassId());
                pst.setInt(6, jdMethod.getType().getId());
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

package com.funfvier.anjavadoc.crawler.dao;

import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.server.LoaderHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

/**
 * Created by lshi on 03.07.2015.
 */
public class PackageDao extends BaseDao {
    private static final Logger log = LogManager.getLogger(PackageDao.class);

    public void deleteAll() {
        Connection connection = null;
        try {
            connection = getConnection();
            Statement delSt = connection.createStatement();
            delSt.executeUpdate("delete from jd_packages");
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

    public void saveToDb(JDPackage jdPackage) {
        Connection connection = null;
        try {
            connection = getConnection();

            PreparedStatement pst = connection.prepareStatement("insert into jd_packages (_id,name,description,full_description) values (?, ?, ?, ?)");
            pst.setInt(1, jdPackage.getId());
            pst.setString(2, jdPackage.getName());
            pst.setString(3, jdPackage.getShortDescription());
            pst.setString(4, jdPackage.getLongDescription());
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

    public void saveToDb(Collection<JDPackage> packages) {
        Connection connection = null;
        try {
            connection = getConnection();

            PreparedStatement pst = connection.prepareStatement("insert into jd_packages (_id,name,description,full_description) values (?, ?, ?, ?)");
            for(JDPackage jdPackage : packages) {
                pst.setInt(1, jdPackage.getId());
                pst.setString(2, jdPackage.getName());
                pst.setString(3, jdPackage.getShortDescription());
                pst.setString(4, jdPackage.getLongDescription());
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

    public void updateLongDescription(int packageId, String text) {
        Connection connection = null;
        try {
            connection = getConnection();

            PreparedStatement pst = connection.prepareStatement("update jd_packages set values full_description = ? where id = ?");
            pst.setInt(2, packageId);
            pst.setString(1, text);
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
}

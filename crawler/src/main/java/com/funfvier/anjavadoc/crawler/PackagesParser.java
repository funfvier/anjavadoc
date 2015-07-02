package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lshi on 01.07.2015.
 */
public class PackagesParser {
    private static final Logger log = LogManager.getLogger(PackagesParser.class);
    private File path;
    private List<JDPackage> packages;
    Document doc;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public PackagesParser(String filePath) {
        path = new File(filePath);
        if(!path.exists() ||
                !path.isFile()||
                !path.isFile()) {
            throw new IllegalArgumentException("Not a valid path: " + filePath);
        }
    }

    public void parse() throws Exception {
        packages = new ArrayList<>();
        doc = Jsoup.parse(path, "UTF-8");

        Element packagesTableElement = getPackagesTableElement();
        handleRows(packagesTableElement);
    }

    private void handleRows(Element packagesTableElement) {
        Elements rows = packagesTableElement.getElementsByTag("tr");
        JDPackage jdPackage;
        int id = 1;
        for (Element row : rows) {
            jdPackage = new JDPackage();
            Element colFirst = row.getElementsByClass("colFirst").first();
            if (colFirst.getElementsByTag("a").size() < 1) continue;
            jdPackage.setId(id++);
            jdPackage.setName(getPackageName(colFirst));
            jdPackage.setHref(getPackageHref(colFirst));
            Element colLast = row.getElementsByClass("colLast").first();
            jdPackage.setShortDescription(getShortDescription(colLast));
            log.debug("Next package: " + jdPackage);
            packages.add(jdPackage);
        }
    }

    private String getPackageName(Element col) {
        Element link = col.getElementsByTag("a").first();
        return link.text();
    }

    private String getPackageHref(Element col) {
        Element link = col.getElementsByTag("a").first();
        return link.attributes().get("href");
    }

    private String getShortDescription(Element col) {
        return col.text();
    }

    private Element getPackagesTableElement() {
        Elements tablesElements = doc.getElementsByTag("table");
        for (Element tableElt : tablesElements) {
            if (tableElt.className().equalsIgnoreCase("overviewSummary")) {
                return tableElt;
            }
        }
        return new Element(Tag.valueOf("table"), "");
    }

    public void saveToDb() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            Statement delSt = connection.createStatement();
            delSt.executeUpdate("delete from jd_packages");

            PreparedStatement pst = connection.prepareStatement("insert into jd_packages (_id,name,description,full_description) values (?, ?, ?, ?)");
            for (JDPackage jdPackage : packages) {
                pst.setInt(1, jdPackage.getId());
                pst.setString(2, jdPackage.getName());
                pst.setString(3, jdPackage.getShortDescription());
                pst.setString(4, jdPackage.getLongDescription());
                pst.executeUpdate();
            }
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

    public List<JDPackage> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public static void main(String[] args) throws Exception {
        PackagesParser packagesParser = new PackagesParser("C:\\downloads\\jdk-8u45-docs-all\\docs\\api\\overview-summary.html");
        packagesParser.parse();
        packagesParser.saveToDb();
    }
}

package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.entity.JDClass;
import com.funfvier.anjavadoc.crawler.entity.JDMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lshi on 01.07.2015.
 */
public class ClassesParser {
    private static final Logger log = LogManager.getLogger(MembersParser.class);
    private File path;
    private List<JDClass> classes;
    private String packageName;
    private String packageLongDescription;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassesParser(File path, String packageName) {
        this.path = path;
        this.packageName = packageName;
    }

    public List<JDClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public String getPackageLongDescription() {
        return packageLongDescription;
    }

    public static void main(String[] args) throws Exception {
        ClassesParser classesParser = new ClassesParser(new File("C:/downloads/jdk-8u45-docs-all/docs/api/java/applet/package-summary.html"), "java.applet");
        classesParser.parse();
        classesParser.saveToDb();
    }

    public void parse() throws Exception {
        classes = new ArrayList<>();
        Document doc = Jsoup.parse(path, "UTF-8");
        Element containerElt = doc.getElementsByClass("contentContainer").first();
        if(containerElt != null) {
            Elements summaryLiElts = containerElt.select("ul.blockList > li.blockList");
            for(Element summaryLi : summaryLiElts ) {
                handleSummaryLi(summaryLi);
            }
            handleLongDescription(containerElt);
        }
    }

    public void saveToDb() {
        Set<String> set = new HashSet();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            Statement delSt = connection.createStatement();
            delSt.executeUpdate("delete from jd_classes");

            PreparedStatement pst = connection.prepareStatement("insert into jd_classes (_id, name, desc_short, desc_long, package_id) values (?, ?, ?, ?, ?)");
            int id = 1;
            for (JDClass jdClass : classes) {
                if(set.contains(jdClass.getName())) continue;
                set.add(jdClass.getName());
                pst.setInt(1, id++);
                pst.setString(2, jdClass.getName());
                pst.setString(3, jdClass.getShortDescription());
                pst.setString(4, jdClass.getLongDescription());
                pst.setInt(5, 1);
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

    private void handleSummaryLi(Element e) {
        Element headerElt = e.select("table.typeSummary > caption > span").first();
        if(headerElt != null) {
            log.debug("Next summary: " + headerElt.text());
        }
        Elements rows = e.getElementsByTag("tr");
        for(Element row : rows) {
            handleRow(row);
        }
    }

    private void handleRow(Element row) {
        JDClass jdClass = new JDClass();
        Element colFirst = row.getElementsByClass("colFirst").first();
        if(colFirst.tagName().equalsIgnoreCase("th")) return;
        String className = null;
        String description = null;
        if(colFirst != null) {
            className = colFirst.text();
            Element hrefElt = colFirst.getElementsByTag("a").first();
            if(hrefElt != null) {
                jdClass.setHref(hrefElt.attributes().get("href"));
            }
        }
        Element colLast = row.getElementsByClass("colLast").first();
        if(colLast != null) {
            description = colLast.html();
        }
        log.debug("Next class: " + className + ", description: " + description);
        jdClass.setName(packageName + "." + className);
        jdClass.setShortDescription(description);
        classes.add(jdClass);
    }

    private void handleLongDescription(Element containerElt) {
        packageLongDescription = "";
        Element descriptionElt = containerElt.select("div.block").first();
        if(descriptionElt != null) {
            packageLongDescription = descriptionElt.html();
        }

        Element sinceElt = containerElt.select("dl").first();
        if(sinceElt != null) {
            packageLongDescription += sinceElt.html();
        }
    }
}

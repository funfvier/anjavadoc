package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.entity.JDMethod;
import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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
 * Created by lshi on 30.06.2015.
 */
public class MembersParser {
    private static final Logger log = LogManager.getLogger(MembersParser.class);
    private File path;
    private List<JDMethod> methods;
    private String longClassDescription;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MembersParser(File path) {
        this.path = path;
    }

    public String getLongClassDescription() {
        return longClassDescription;
    }

    public void parse() throws IOException {
        methods = new ArrayList<>();
        Document doc = Jsoup.parse(path, "UTF-8");
        Elements details = doc.getElementsByClass("details");
        Element detailsElement = details.first();
        if(detailsElement == null) {
            log.warn("detailsElement is null: " + path);
        }
        Elements methodDetailElements = detailsElement.getElementsByAttributeValue("name", "method.detail");
        if(detailsElement != null && methodDetailElements != null) {
            log.warn("methodDetailElements is null: "  + path);
            Element methodsLiElement = methodDetailElements.parents().first();
            if(methodsLiElement == null) {
                log.warn("methodsLiElement is null: " + path);
                return;
            }
            Elements methodLiElements = methodsLiElement.getElementsByTag("li");
            for (Element menthodLiElement : methodLiElements) {
                if (menthodLiElement.className().equalsIgnoreCase("blockList")) {
                    Element signatureElement = menthodLiElement.getElementsByTag("pre").first();
                    log.info("Next method " + signatureElement.text());
                    String methodDescription = signatureElement.siblingElements().html();
                    log.info("  Description: " + methodDescription);

                    JDMethod jdMethod = new JDMethod();
                    jdMethod.setName(signatureElement.text());
                    jdMethod.setHtmlName(signatureElement.html());
                    jdMethod.setShortDescription(null);
                    jdMethod.setLongDescription(signatureElement.siblingElements().html());
                    methods.add(jdMethod);
                }
            }
        }
        Element contentContainerElt = doc.getElementsByClass("contentContainer").first();
        if(contentContainerElt != null) {
            processClassDescription(contentContainerElt);
        }
    }

    public List<JDMethod> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    private void processClassDescription(Element contentContainerElt) {
        longClassDescription = "";
        Element inheritanceElt = contentContainerElt.getElementsByClass("inheritance").first();
        if(inheritanceElt != null) {
            longClassDescription += inheritanceElt.html();
        }
        Element descriptionElt = contentContainerElt.getElementsByClass("description").first();
        if(descriptionElt != null) {
            longClassDescription += descriptionElt.html();
        }
    }

    public void saveToDb() {
        Set<String> set = new HashSet();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\projects\\funfvier\\anjavadoc\\anjavadoc\\app\\src\\main\\assets\\docdb");
            Statement delSt = connection.createStatement();
            delSt.executeUpdate("delete from jd_members");

            PreparedStatement pst = connection.prepareStatement("insert into jd_members (_id, name, desc_short, desc_long, class_id) values (?, ?, ?, ?, ?)");
            int id = 1;
            for (JDMethod jdMethod : methods) {
                if(set.contains(jdMethod.getName())) continue;
                set.add(jdMethod.getName());
                pst.setInt(1, id++);
                pst.setString(2, jdMethod.getName());
                pst.setString(3, jdMethod.getShortDescription());
                pst.setString(4, jdMethod.getLongDescription());
                pst.setInt(5, 4);
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

    public static void main(String[] args) throws Exception {
        MembersParser classParser = new MembersParser(new File("C:/downloads/jdk-8u45-docs-all/docs/api/java/awt/dnd/DnDConstants.html"));
        classParser.parse();
//        classParser.saveToDb();
    }
}

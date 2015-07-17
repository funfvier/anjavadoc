package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.entity.JDMethod;
import com.funfvier.anjavadoc.crawler.entity.JDPackage;
import com.funfvier.anjavadoc.crawler.entity.MemberType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryType;
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
    private JDMethod currentMember;

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

        Elements detailLiElts = doc.select("div.details > ul.blockList > li.blockList > ul.blockList");
        for(Element detailElt : detailLiElts) {
            MemberType type = getType(detailElt);
            log.debug("Type: " + type);
            parseDetail(type, detailElt);
        }

        Element contentContainerElt = doc.getElementsByClass("contentContainer").first();
        if(contentContainerElt != null) {
            processClassDescription(contentContainerElt);
        }
    }

    public List<JDMethod> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    private void parseDetail(MemberType type, Element detailElt) {
//        if(type == MemberType.METHOD ||
//                type == MemberType.FIELD) {
//            parseMethods(type, detailElt);
//        }
        parseMethods(type, detailElt);
    }

    private void parseMethods(MemberType type, Element detailElt) {
        Elements methodsLiEtls = detailElt.select("ul.blockList,ul.blockListLast");
        for(Element methodElt : methodsLiEtls) {
            currentMember = new JDMethod();
            Element signatureElement = methodElt.getElementsByTag("pre").first();
            if(signatureElement != null) {
                currentMember.setName(signatureElement.text());
                currentMember.setHtmlName(signatureElement.html());
            }
            Element shortDescriptionElt = methodElt.select("div.block").first();
            if(shortDescriptionElt != null) {
                currentMember.setShortDescription(shortDescriptionElt.text());
            }
            Element descriptionElt = methodElt.getElementsByTag("dl").first();
            if(descriptionElt != null) {
                currentMember.setLongDescription(shortDescriptionElt.html() + descriptionElt.html());
            } else {
                currentMember.setLongDescription(shortDescriptionElt.html());
            }
            currentMember.setType(type);
            log.debug("Current member: " + currentMember);
            methods.add(currentMember);
        }
    }

    private MemberType getType(Element ulDetailsElt) {
        Element typeElt = ulDetailsElt.select("li.blockList > h3").first();
        if(typeElt != null) {
            String type = typeElt.text().trim().toLowerCase();
            if(type.startsWith("Field Detail".toLowerCase())) {
                return MemberType.FIELD;
            }
            if(type.startsWith("Method Detail".toLowerCase())) {
                return MemberType.METHOD;
            }
            if(type.startsWith("Constructor Detail".toLowerCase())) {
                return MemberType.CONSTRUCTOR;
            }
            if(type.startsWith("Enum Constant Detail".toLowerCase())) {
                return MemberType.ENUM_CONSTANT;
            }
            if(type.startsWith("Element Detail".toLowerCase())) {
                return MemberType.ELEMENT;
            }
        }
        return MemberType.METHOD;
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
        MembersParser classParser = new MembersParser(new File("C:/downloads/jdk-8u45-docs-all/docs/api/java/lang/annotation/RetentionPolicy.html"));
        classParser.parse();
//        classParser.saveToDb();
    }
}

package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.dao.ClassDao;
import com.funfvier.anjavadoc.crawler.dao.MemberDao;
import com.funfvier.anjavadoc.crawler.dao.PackageDao;
import com.funfvier.anjavadoc.crawler.entity.JDClass;
import com.funfvier.anjavadoc.crawler.entity.JDMethod;
import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler {
    private static final Logger log = LogManager.getLogger(Crawler.class);
    private static final String PACKAGES_PAGE = "C:\\downloads\\jdk-8u45-docs-all\\docs\\api\\overview-summary.html";
    private String basePath;
    private List<JDPackage> packages;
    private PackageDao packageDao;
    private ClassDao classDao;
    private MemberDao memberDao;
    private AtomicInteger classId;
    private AtomicInteger memberId;

    public Crawler() {
        packageDao = new PackageDao();
        classDao = new ClassDao();
        memberDao = new MemberDao();
        classId = new AtomicInteger(0);
        memberId = new AtomicInteger(0);
    }

    public void process() throws Exception {
        PackagesParser packagesParser = new PackagesParser(PACKAGES_PAGE);
        packagesParser.parse();
        packages = packagesParser.getPackages();
        basePath = new File(PACKAGES_PAGE).getParent();
        classDao.deleteAll();
        packageDao.deleteAll();
        memberDao.deleteAll();
        processPackages();
    }

    private void processPackages() throws Exception {
        for(JDPackage jdPackage : packages) {
            processPackage(jdPackage);
        }
    }

    private void processPackage(JDPackage jdPackage) throws Exception {
        log.debug("Next package: " + jdPackage);
        String classFilePath = FilenameUtils.concat(basePath, jdPackage.getHref());
        File classFile = new File(classFilePath);
        if(!classFile.isFile() ||
                !classFile.exists() ||
                !classFile.canRead()) {
            log.warn("Class file is not valid: " + classFilePath);
            return;
        }
        packageDao.saveToDb(jdPackage);
        ClassesParser classParser = new ClassesParser(classFile, jdPackage.getName());
        try{
            classParser.parse();
        } catch(Exception e) {
            log.error("Error parsing path: " + classFilePath, e);
            return;
        }
        for(JDClass jdClass : classParser.getClasses()) {
            jdClass.setPackageId(jdPackage.getId());
            processClass(jdPackage, jdClass);
        }
    }

    private void processClass(JDPackage jdPackage, JDClass jdClass) throws Exception {
        jdClass.setId(classId.incrementAndGet());
        log.debug("Next class: " + jdClass);
        classDao.saveToDb(jdClass);

        if(jdClass.getHref() == null) {
            log.warn("jdClass have null href: " + jdClass);
            return;
        }
        File packageFile = new File(jdPackage.getHref());
        String membersFilePath = FilenameUtils.concat(FilenameUtils.concat(basePath, packageFile.getParent()), jdClass.getHref());
        if(membersFilePath == null) {
            log.warn("jdClass have null membersFilePath: " + jdClass);
            return;
        }
        File memberFile = new File(membersFilePath);
        if(!memberFile.isFile() ||
                !memberFile.exists() ||
                !memberFile.canRead()) {
            log.warn("Member file is not valid: " + memberFile);
            return;
        }

        MembersParser membersParser = new MembersParser(memberFile);
        membersParser.parse();

        for(JDMethod jdMethod : membersParser.getMethods()) {
            jdMethod.setClassId(jdClass.getId());
            jdMethod.setId(memberId.incrementAndGet());
            log.debug("Next member: " + jdMethod);
            memberDao.saveToDb(jdMethod);
        }
    }

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        crawler.process();
    }
}

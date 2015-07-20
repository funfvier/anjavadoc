package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.dao.ClassDao;
import com.funfvier.anjavadoc.crawler.dao.MemberDao;
import com.funfvier.anjavadoc.crawler.dao.PackageDao;
import com.funfvier.anjavadoc.crawler.entity.JDClass;
import com.funfvier.anjavadoc.crawler.entity.JDMethod;
import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lshi on 06.07.2015.
 */
public class ParallelCrawler {
    private static final Logger log = LogManager.getLogger(ParallelCrawler.class);
    private static final String PACKAGES_PAGE = "C:\\downloads\\jdk-8u45-docs-all\\docs\\api\\overview-summary.html";
    private final String basePath;
    private Collection<JDPackage> packages;
    private Collection<JDClass> classes;
    private Collection<JDMethod> members;
    private PackageDao packageDao;
    private ClassDao classDao;
    private MemberDao memberDao;
    private AtomicInteger classId;
    private AtomicInteger memberId;
    private final JDThreadPoolExecutor executor;
    private final static AtomicLong threadNumber = new AtomicLong(1);

    public ParallelCrawler() {
        packageDao = new PackageDao();
        classDao = new ClassDao();
        memberDao = new MemberDao();
        classId = new AtomicInteger(0);
        memberId = new AtomicInteger(0);
        basePath = new File(PACKAGES_PAGE).getParent();
        classes = new LinkedBlockingQueue<>();
        members = new LinkedBlockingQueue<>();

        BlockingQueue q = new LinkedBlockingQueue();
        executor = new JDThreadPoolExecutor(4, 4, 10, TimeUnit.MINUTES, q, new CrawlerThreadFactory(), new JDRejectedExecutionHandler());
    }

    public static void main(String[] args) throws Exception {
        ParallelCrawler parallelCrawler = new ParallelCrawler();
        parallelCrawler.process();
    }

    public void process() throws Exception {
        PackagesParser packagesParser = new PackagesParser(PACKAGES_PAGE);
        packagesParser.parse();
        packages = packagesParser.getPackages();
        classDao.deleteAll();
        packageDao.deleteAll();
        memberDao.deleteAll();
        processPackages();
    }

    private void processPackages() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        for (JDPackage jdPackage : packages) {
            Worker worker = new Worker(jdPackage);
            executor.submit(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
            sw.stop();
            log.info("Time parsing: " + sw.getTime());
        } catch (InterruptedException e) {
            log.error(e);
        }
        sw.reset();
        sw.start();
        packageDao.saveToDb(packages);
        log.debug("Packages saved");
        classDao.saveToDb(classes);
        log.debug("Classes saved");
        memberDao.saveToDb(members);
        log.debug("Members saved");
        sw.stop();
        log.info("Time in db: " + sw.getTime());
        log.info("Work completed");
    }


    class Worker implements Runnable {
        private JDPackage jdPackage;

        public Worker(JDPackage jdPackage) {
            this.jdPackage = jdPackage;
        }

        @Override
        public void run() {
            log.debug("Process package: " + jdPackage);
            try {
                processPackage(jdPackage);
            } catch (Exception e) {
                log.error(e);
            }
        }

        private void processPackage(JDPackage jdPackage) throws Exception {
            log.debug("Next package: " + jdPackage);
            String classFilePath = FilenameUtils.concat(basePath, jdPackage.getHref());
            File classFile = new File(classFilePath);
            if (!classFile.isFile() ||
                    !classFile.exists() ||
                    !classFile.canRead()) {
                log.warn("Class file is not valid: " + classFilePath);
                return;
            }
            ClassesParser classParser = new ClassesParser(classFile, jdPackage.getName());
            classParser.parse();

            jdPackage.setLongDescription(classParser.getPackageLongDescription());

            for (JDClass jdClass : classParser.getClasses()) {
                jdClass.setPackageId(jdPackage.getId());
                processClass(jdPackage, jdClass);
            }
        }

        private void processClass(JDPackage jdPackage, JDClass jdClass) throws Exception {
            jdClass.setId(classId.incrementAndGet());
            log.debug("Next class: " + jdClass);

            if (jdClass.getHref() == null) {
                log.warn("jdClass have null href: " + jdClass);
                return;
            }
            File packageFile = new File(jdPackage.getHref());
            String membersFilePath = FilenameUtils.concat(FilenameUtils.concat(basePath, packageFile.getParent()), jdClass.getHref());
            if (membersFilePath == null) {
                log.warn("jdClass have null membersFilePath: " + jdClass);
                return;
            }
            File memberFile = new File(membersFilePath);
            if (!memberFile.isFile() ||
                    !memberFile.exists() ||
                    !memberFile.canRead()) {
                log.warn("Member file is not valid: " + memberFile);
                return;
            }

            MembersParser membersParser = new MembersParser(memberFile);
            membersParser.parse();

            jdClass.setLongDescription(membersParser.getLongClassDescription());
            classes.add(jdClass);

            for (JDMethod jdMethod : membersParser.getMethods()) {
                jdMethod.setClassId(jdClass.getId());
                jdMethod.setId(memberId.incrementAndGet());
                log.debug("Next member: " + jdMethod);
                members.add(jdMethod);
            }
        }
    }

    static class JDRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.debug("Rejected: " + r);
        }
    }

    static class CrawlerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("CrawlerUncaughtExceptionHandler exception: ", e);
        }
    }

    static class CrawlerThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("Thread-" + threadNumber.getAndIncrement());
            t.setUncaughtExceptionHandler(new CrawlerUncaughtExceptionHandler());
            return t;
        }
    }
}

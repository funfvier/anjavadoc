package com.funfvier.anjavadoc.crawler;

import com.funfvier.anjavadoc.crawler.entity.JDPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class Crawler {
    private static final Logger log = LogManager.getLogger(Crawler.class);
    private static final String PACKAGES_PAGE = "C:\\downloads\\jdk-8u45-docs-all\\docs\\api\\overview-summary.html";
    private List<JDPackage> packages;

    public Crawler() {

    }

    public void process() throws Exception {
        PackagesParser packagesParser = new PackagesParser(PACKAGES_PAGE);
        packagesParser.parse();
        packages = packagesParser.getPackages();
        processPackages();
    }

    private void processPackages() {
        for(JDPackage jdPackage : packages) {
            processPackage(jdPackage);
        }
    }

    private void processPackage(JDPackage jdPackage) {
        log.debug("Next package: " + jdPackage);
    }

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        crawler.process();
    }
}

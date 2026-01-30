package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/** Scraper for mainline OpenJDK releases */
public class OpenJdkScraper extends OpenJdkBaseScraper {
    public OpenJdkScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "openjdk";
    }

    @Override
    protected List<String> getIndexUrls() {
        return Arrays.asList(
                "http://jdk.java.net/archive/",
                "http://jdk.java.net/21/",
                "http://jdk.java.net/22/",
                "http://jdk.java.net/23/",
                "http://jdk.java.net/24/",
                "http://jdk.java.net/25/",
                "http://jdk.java.net/26/");
    }
}

package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

/** Scraper for OpenJDK Project Leyden early access builds */
public class OpenJdkLeydenScraper extends OpenJdkBaseScraper {
    
    public OpenJdkLeydenScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "openjdk-leyden";
    }

    @Override
    protected List<String> getIndexUrls() {
        return Collections.singletonList("http://jdk.java.net/leyden/");
    }

    @Override
    protected String getFeature() {
        return "leyden";
    }
}

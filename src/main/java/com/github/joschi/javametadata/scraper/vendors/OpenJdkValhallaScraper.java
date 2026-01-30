package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/** Scraper for OpenJDK Project Valhalla releases */
public class OpenJdkValhallaScraper extends OpenJdkBaseScraper {
    public OpenJdkValhallaScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "openjdk-valhalla";
    }

    @Override
    protected List<String> getIndexUrls() {
        return Collections.singletonList("http://jdk.java.net/valhalla/");
    }

    @Override
    protected String getFeature() {
        return "valhalla";
    }
}

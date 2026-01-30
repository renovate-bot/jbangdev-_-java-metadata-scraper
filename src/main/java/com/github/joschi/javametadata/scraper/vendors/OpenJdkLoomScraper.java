package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/** Scraper for OpenJDK Project Loom releases */
public class OpenJdkLoomScraper extends OpenJdkBaseScraper {
    public OpenJdkLoomScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "openjdk-loom";
    }

    @Override
    protected List<String> getIndexUrls() {
        return Collections.singletonList("https://jdk.java.net/loom/");
    }

    @Override
    protected String getFeature() {
        return "loom";
    }
}

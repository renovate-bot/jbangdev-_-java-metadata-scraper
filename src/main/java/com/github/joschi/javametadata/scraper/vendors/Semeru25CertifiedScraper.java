package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.logging.Logger;

public class Semeru25CertifiedScraper extends SemeruBaseScraper {
    public Semeru25CertifiedScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    protected String getJavaVersion() {
        return "25-certified";
    }

    @Override
    public String getScraperId() {
        return "semeru-25-certified";
    }
}

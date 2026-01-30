package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.logging.Logger;

public class Semeru11CertifiedScraper extends SemeruBaseScraper {
    public Semeru11CertifiedScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    protected String getJavaVersion() {
        return "11-certified";
    }

    @Override
    public String getScraperId() {
        return "semeru-11-certified";
    }
}

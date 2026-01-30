package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.logging.Logger;

public class Semeru21CertifiedScraper extends SemeruBaseScraper {
    public Semeru21CertifiedScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    protected String getJavaVersion() {
        return "21-certified";
    }

    @Override
    public String getScraperId() {
        return "semeru-21-certified";
    }
}

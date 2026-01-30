package com.github.joschi.javametadata.scraper;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Interface for vendor scrapers that collect JDK metadata. Scrapers implement {@link Callable} to
 * support parallel execution.
 */
public interface Scraper extends Callable<ScraperResult> {

    /**
     * Factory interface for scraper discovery
     */
    interface Discovery {
        String name();
        String vendor();
        Scraper create(Path metadataDir, Path checksumDir, Logger logger);
    }
}

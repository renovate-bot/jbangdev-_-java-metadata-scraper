package com.github.joschi.javametadata.scraper;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Configuration record for scraper instances. Encapsulates the metadata directory,
 * checksum directory, and logger that scrapers need to operate.
 */
public record ScraperConfig(Path metadataDir, Path checksumDir, Logger logger) {
}

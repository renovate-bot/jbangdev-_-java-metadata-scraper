package com.github.joschi.javametadata.scraper;

import com.github.joschi.javametadata.reporting.ProgressReporter;
import com.github.joschi.javametadata.reporting.ProgressReporterLogger;
import java.nio.file.Path;
import java.util.*;

/** Factory for creating scraper instances using ServiceLoader */
public class ScraperFactory {
    private final Path metadataDir;
    private final Path checksumDir;
    private final ProgressReporter reporter;

    public static ScraperFactory create(
            Path metadataDir, Path checksumDir, ProgressReporter reporter) {
        return new ScraperFactory(metadataDir, checksumDir, reporter);
    }

    private ScraperFactory(
            Path metadataDir, Path checksumDir, ProgressReporter reporter) {
        this.metadataDir = metadataDir;
        this.checksumDir = checksumDir;
        this.reporter = reporter;
    }

    /** Create all available scrapers using ServiceLoader */
    public List<Scraper> createAllScrapers() {
        List<Scraper> scrapers = new ArrayList<>();
        Path metadataVendorDir = metadataDir.resolve("vendor");

        ServiceLoader<Scraper.Discovery> loader = ServiceLoader.load(Scraper.Discovery.class);

        for (Scraper.Discovery discovery : loader) {
            String vendor = discovery.vendor();
            String name = discovery.name();

            Scraper scraper = discovery.create(
                    metadataVendorDir.resolve(vendor),
                    checksumDir.resolve(vendor),
                    ProgressReporterLogger.forScraper(name, reporter));

            scrapers.add(scraper);
        }

        return scrapers;
    }

    /** Create specific scraper by name */
    public Scraper createScraper(String scraperName) {
        Map<String, Scraper.Discovery> allDiscoveries = getAvailableScraperDiscoveries();

        Scraper.Discovery discovery = allDiscoveries.get(scraperName);
        if (discovery != null) {
            String vendor = discovery.vendor();
            Scraper scraper = discovery.create(
                    metadataDir.resolve("vendor").resolve(vendor),
                    checksumDir.resolve(vendor),
                    ProgressReporterLogger.forScraper(scraperName, reporter));
            return scraper;
        } else {
            throw new IllegalArgumentException("Unknown scraper ID: " + scraperName);
        }
    }

    /** Get all available scraper discoveries */
    public static Map<String, Scraper.Discovery> getAvailableScraperDiscoveries() {
        Map<String, Scraper.Discovery> discs = new HashMap<>();

        ServiceLoader<Scraper.Discovery> loader = ServiceLoader.load(Scraper.Discovery.class);
        for (Scraper.Discovery discovery : loader) {
            discs.put(discovery.name(), discovery);
        }

        return discs;
    }
}

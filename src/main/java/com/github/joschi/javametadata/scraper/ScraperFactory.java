package com.github.joschi.javametadata.scraper;

import com.github.joschi.javametadata.reporting.ProgressReporter;
import com.github.joschi.javametadata.reporting.ProgressReporterLogger;
import com.github.joschi.javametadata.scraper.vendors.*;
import java.nio.file.Path;
import java.util.*;

/** Factory for creating scraper instances */
public class ScraperFactory {

    /** Create all available scrapers */
    public static List<Scraper> createAllScrapers(
            Path metadataDir, Path checksumDir, ProgressReporter reporter) {
        List<Scraper> scrapers = new ArrayList<>();

        Path metadataVendorDir = metadataDir.resolve("vendor");

        // Temurin (Adoptium Eclipse Temurin)
        scrapers.add(
                new TemurinScraper(
                        metadataVendorDir.resolve("temurin"),
                        checksumDir.resolve("temurin"),
                        ProgressReporterLogger.forScraper("temurin", reporter)));

        // Zulu (Azul)
        scrapers.add(
                new ZuluScraper(
                        metadataVendorDir.resolve("zulu"),
                        checksumDir.resolve("zulu"),
                        ProgressReporterLogger.forScraper("zulu", reporter)));

        // Liberica (BellSoft)
        scrapers.add(
                new LibericaScraper(
                        metadataVendorDir.resolve("liberica"),
                        checksumDir.resolve("liberica"),
                        ProgressReporterLogger.forScraper("liberica", reporter)));

        // Corretto (Amazon)
        scrapers.add(
                new CorrettoScraper(
                        metadataVendorDir.resolve("corretto"),
                        checksumDir.resolve("corretto"),
                        ProgressReporterLogger.forScraper("corretto", reporter)));

        // SapMachine (SAP)
        scrapers.add(
                new SapMachineScraper(
                        metadataVendorDir.resolve("sapmachine"),
                        checksumDir.resolve("sapmachine"),
                        ProgressReporterLogger.forScraper("sapmachine", reporter)));

        // Microsoft
        scrapers.add(
                new MicrosoftScraper(
                        metadataVendorDir.resolve("microsoft"),
                        checksumDir.resolve("microsoft"),
                        ProgressReporterLogger.forScraper("microsoft", reporter)));

        // OpenJDK and project variants
        scrapers.add(
                new OpenJdkScraper(
                        metadataVendorDir.resolve("openjdk"),
                        checksumDir.resolve("openjdk"),
                        ProgressReporterLogger.forScraper("openjdk", reporter)));
        scrapers.add(
                new OpenJdkLeydenScraper(
                        metadataVendorDir.resolve("openjdk"),
                        checksumDir.resolve("openjdk"),
                        ProgressReporterLogger.forScraper("openjdk-leyden", reporter)));
        scrapers.add(
                new OpenJdkLoomScraper(
                        metadataVendorDir.resolve("openjdk"),
                        checksumDir.resolve("openjdk"),
                        ProgressReporterLogger.forScraper("openjdk-loom", reporter)));
        scrapers.add(
                new OpenJdkValhallaScraper(
                        metadataVendorDir.resolve("openjdk"),
                        checksumDir.resolve("openjdk"),
                        ProgressReporterLogger.forScraper("openjdk-valhalla", reporter)));

        // Dragonwell (Alibaba)
        scrapers.add(
                new Dragonwell8Scraper(
                        metadataVendorDir.resolve("dragonwell"),
                        checksumDir.resolve("dragonwell"),
                        ProgressReporterLogger.forScraper("dragonwell-8", reporter)));
        scrapers.add(
                new Dragonwell11Scraper(
                        metadataVendorDir.resolve("dragonwell"),
                        checksumDir.resolve("dragonwell"),
                        ProgressReporterLogger.forScraper("dragonwell-11", reporter)));
        scrapers.add(
                new Dragonwell17Scraper(
                        metadataVendorDir.resolve("dragonwell"),
                        checksumDir.resolve("dragonwell"),
                        ProgressReporterLogger.forScraper("dragonwell-17", reporter)));
        scrapers.add(
                new Dragonwell21Scraper(
                        metadataVendorDir.resolve("dragonwell"),
                        checksumDir.resolve("dragonwell"),
                        ProgressReporterLogger.forScraper("dragonwell-21", reporter)));

        // Kona (Tencent)
        scrapers.add(
                new Kona8Scraper(
                        metadataVendorDir.resolve("kona"),
                        checksumDir.resolve("kona"),
                        ProgressReporterLogger.forScraper("kona-8", reporter)));
        scrapers.add(
                new Kona11Scraper(
                        metadataVendorDir.resolve("kona"),
                        checksumDir.resolve("kona"),
                        ProgressReporterLogger.forScraper("kona-11", reporter)));
        scrapers.add(
                new Kona17Scraper(
                        metadataVendorDir.resolve("kona"),
                        checksumDir.resolve("kona"),
                        ProgressReporterLogger.forScraper("kona-17", reporter)));
        scrapers.add(
                new Kona21Scraper(
                        metadataVendorDir.resolve("kona"),
                        checksumDir.resolve("kona"),
                        ProgressReporterLogger.forScraper("kona-21", reporter)));

        // Oracle
        scrapers.add(
                new OracleScraper(
                        metadataVendorDir.resolve("oracle"),
                        checksumDir.resolve("oracle"),
                        ProgressReporterLogger.forScraper("oracle", reporter)));
        scrapers.add(
                new OracleGraalVmScraper(
                        metadataVendorDir.resolve("oracle-graalvm"),
                        checksumDir.resolve("oracle-graalvm"),
                        ProgressReporterLogger.forScraper("oracle-graalvm", reporter)));
        scrapers.add(
                new OracleGraalVmEaScraper(
                        metadataVendorDir.resolve("oracle-graalvm"),
                        checksumDir.resolve("oracle-graalvm"),
                        ProgressReporterLogger.forScraper("oracle-graalvm-ea", reporter)));

        // Semeru versions
        scrapers.add(
                new Semeru8Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-8", reporter)));
        scrapers.add(
                new Semeru11Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-11", reporter)));
        scrapers.add(
                new Semeru11CertifiedScraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-11-certified", reporter)));
        scrapers.add(
                new Semeru16Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-16", reporter)));
        scrapers.add(
                new Semeru17Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-17", reporter)));
        scrapers.add(
                new Semeru17CertifiedScraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-17-certified", reporter)));
        scrapers.add(
                new Semeru18Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-18", reporter)));
        scrapers.add(
                new Semeru19Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-19", reporter)));
        scrapers.add(
                new Semeru20Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-20", reporter)));
        scrapers.add(
                new Semeru21Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-21", reporter)));
        scrapers.add(
                new Semeru21CertifiedScraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-21-certified", reporter)));
        scrapers.add(
                new Semeru22Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-22", reporter)));
        scrapers.add(
                new Semeru23Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-23", reporter)));
        scrapers.add(
                new Semeru24Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-24", reporter)));
        scrapers.add(
                new Semeru25Scraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-25", reporter)));
        scrapers.add(
                new Semeru25CertifiedScraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru-25-certified", reporter)));

        // Add more scrapers here as they are implemented

        return scrapers;
    }

    /** Create specific scrapers by ID */
    public static List<Scraper> createScrapers(
            List<String> scraperIds,
            Path metadataDir,
            Path checksumDir,
            ProgressReporter reporter) {
        Map<String, Scraper> allScrapers = new HashMap<>();

        // Build map of all available scrapers
        for (Scraper scraper : createAllScrapers(metadataDir, checksumDir, reporter)) {
            allScrapers.put(scraper.getScraperId(), scraper);
        }

        // Select requested scrapers
        List<Scraper> selectedScrapers = new ArrayList<>();
        for (String id : scraperIds) {
            Scraper scraper = allScrapers.get(id);
            if (scraper != null) {
                selectedScrapers.add(scraper);
            } else {
                throw new IllegalArgumentException("Unknown scraper ID: " + id);
            }
        }

        return selectedScrapers;
    }

    /** Get all available scraper IDs */
    public static List<String> getAvailableScraperIds(
            Path metadataDir, Path checksumDir, ProgressReporter reporter) {
        List<String> ids = new ArrayList<>();
        for (Scraper scraper : createAllScrapers(metadataDir, checksumDir, reporter)) {
            ids.add(scraper.getScraperId());
        }
        Collections.sort(ids);
        return ids;
    }
}

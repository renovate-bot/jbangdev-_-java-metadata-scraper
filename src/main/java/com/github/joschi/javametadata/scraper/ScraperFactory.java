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

        // OpenJDK mainline
        scrapers.add(
                new OpenJdkScraper(
                        metadataVendorDir.resolve("openjdk"),
                        checksumDir.resolve("openjdk"),
                        ProgressReporterLogger.forScraper("openjdk", reporter)));
        
        // OpenJDK project variants
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

        // Dragonwell (Alibaba) - unified scraper for all versions
        scrapers.add(
                new DragonwellScraper(
                        metadataVendorDir.resolve("dragonwell"),
                        checksumDir.resolve("dragonwell"),
                        ProgressReporterLogger.forScraper("dragonwell", reporter)));

        // Kona (Tencent) - unified scraper for all versions
        scrapers.add(
                new KonaScraper(
                        metadataVendorDir.resolve("kona"),
                        checksumDir.resolve("kona"),
                        ProgressReporterLogger.forScraper("kona", reporter)));

        // GraalVM variants
        scrapers.add(
                new GraalVmLegacyScraper(
                        metadataVendorDir.resolve("graalvm"),
                        checksumDir.resolve("graalvm"),
                        ProgressReporterLogger.forScraper("graalvm-legacy", reporter)));
        scrapers.add(
                new GraalVmCeScraper(
                        metadataVendorDir.resolve("graalvm"),
                        checksumDir.resolve("graalvm"),
                        ProgressReporterLogger.forScraper("graalvm-ce", reporter)));
        scrapers.add(
                new GraalVmCommunityScraper(
                        metadataVendorDir.resolve("graalvm"),
                        checksumDir.resolve("graalvm"),
                        ProgressReporterLogger.forScraper("graalvm-community", reporter)));

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

        // Semeru - unified scraper for all versions
        scrapers.add(
                new SemeruScraper(
                        metadataVendorDir.resolve("semeru"),
                        checksumDir.resolve("semeru"),
                        ProgressReporterLogger.forScraper("semeru", reporter)));

        // Trava - unified scraper for all versions
        scrapers.add(
                new TravaScraper(
                        metadataVendorDir.resolve("trava"),
                        checksumDir.resolve("trava"),
                        ProgressReporterLogger.forScraper("trava", reporter)));

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

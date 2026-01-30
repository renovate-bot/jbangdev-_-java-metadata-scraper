package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scraper for Trava OpenJDK 11 with DCEVM releases */
public class Trava11Scraper extends TravaBaseScraper {
    // Tag pattern: dcevm-11.0.9+1 -> 11.0.9+1
    private static final Pattern TAG_PATTERN = Pattern.compile("^dcevm-(11\\.[\\d.+]+)$");

    // Filename patterns: java11-openjdk-dcevm-linux.tar.gz or
    // Openjdk11u-dcevm-linux-amd64.tar.gz
    private static final Pattern FILENAME_PATTERN =
            Pattern.compile(
                    "^(?:java11-openjdk|Openjdk11u)-dcevm-(linux|osx|mac|windows)-?(amd64|arm64|x64)?\\.(.*?)$");

    public Trava11Scraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "trava-11";
    }

    @Override
    protected String getGithubRepo() {
        return "trava-jdk-11-dcevm";
    }

    @Override
    protected Pattern getTagPattern() {
        return TAG_PATTERN;
    }

    @Override
    protected Pattern getFilenamePattern() {
        return FILENAME_PATTERN;
    }

    @Override
    protected String extractVersionFromTag(Matcher tagMatcher) {
        return tagMatcher.group(1);
    }
}

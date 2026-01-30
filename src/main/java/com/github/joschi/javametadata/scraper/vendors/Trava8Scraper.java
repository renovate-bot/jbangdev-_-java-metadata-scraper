package com.github.joschi.javametadata.scraper.vendors;

import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scraper for Trava OpenJDK 8 with DCEVM releases */
public class Trava8Scraper extends TravaBaseScraper {
    // Tag pattern: dcevm8u181b02 -> 8.0.181+02
    private static final Pattern TAG_PATTERN = Pattern.compile("^dcevm8u(\\d+)b(\\d+)$");

    // Filename pattern: java8-openjdk-dcevm-linux.tar.gz
    private static final Pattern FILENAME_PATTERN =
            Pattern.compile("^java8-openjdk-dcevm-(linux|osx|windows)\\.(.*)$");

    public Trava8Scraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "trava-8";
    }

    @Override
    protected String getGithubRepo() {
        return "trava-jdk-8-dcevm";
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
        String update = tagMatcher.group(1);
        String build = tagMatcher.group(2);
        return "8.0." + update + "+" + build;
    }
}

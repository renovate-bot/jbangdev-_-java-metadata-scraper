package com.github.joschi.javametadata.scraper.vendors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.joschi.javametadata.model.JdkMetadata;
import com.github.joschi.javametadata.scraper.AdoptiumMarketplaceScraper;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/** Scraper for Red Hat JDK releases */
public class RedhatScraper extends AdoptiumMarketplaceScraper {
    private static final String VENDOR = "redhat";

    public RedhatScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "redhat";
    }

    @Override
    public String getVendorName() {
        return VENDOR;
    }

    @Override
    protected String getApiBase() {
        return "https://marketplace-api.adoptium.net/v1";
    }

    @Override
    protected String getAvailableReleasesPath() {
        return "/info/available_releases/redhat";
    }

    @Override
    protected String getAssetsPathTemplate() {
        return "/assets/feature_releases/redhat/%d";
    }

    @Override
    protected String extractJavaVersion(JsonNode asset) {
        return asset.path("openjdk_version_data").path("openjdk_version").asText();
    }

    @Override
    protected String extractVersion(JsonNode asset) {
        String releaseName = asset.path("release_name").asText();
        // Version is release_name with first 4 characters removed (e.g., "jdk-11.0.10" -> "11.0.10")
        return releaseName.length() > 4 ? releaseName.substring(4) : releaseName;
    }

    @Override
    protected void processBinary(
            JsonNode binary, String version, String javaVersion, List<JdkMetadata> allMetadata)
            throws Exception {
        createStandardMetadata(binary, version, javaVersion, allMetadata, List.of());
    }
}

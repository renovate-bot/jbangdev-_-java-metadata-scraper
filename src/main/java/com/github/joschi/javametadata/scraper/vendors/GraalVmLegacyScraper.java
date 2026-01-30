package com.github.joschi.javametadata.scraper.vendors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.joschi.javametadata.model.JdkMetadata;
import com.github.joschi.javametadata.scraper.BaseScraper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scraper for GraalVM Legacy releases (old releases from oracle/graal repo) */
public class GraalVmLegacyScraper extends BaseScraper {
    private static final String VENDOR = "graalvm";
    private static final String GITHUB_ORG = "oracle";
    private static final String GITHUB_REPO = "graal";
    private static final String GITHUB_API_BASE = "https://api.github.com/repos";
    
    // RC releases: graalvm-ce-1.0.0-rc1-linux-amd64.tar.gz
    private static final Pattern RC_PATTERN = Pattern.compile(
            "^graalvm-ce-([\\d+.]{2,}-rc\\d+)-(linux|macos)-amd64\\.(zip|tar\\.gz)$");
    
    // Regular releases: graalvm-ce-linux-aarch64-19.3.1.tar.gz
    private static final Pattern REGULAR_PATTERN = Pattern.compile(
            "^graalvm-ce-(linux|darwin|windows)-(aarch64|amd64)-([\\d+.]{2,}[^.]*)\\.(zip|tar\\.gz)$");

    public GraalVmLegacyScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    @Override
    public String getScraperId() {
        return "graalvm-legacy";
    }

    @Override
    public String getVendorName() {
        return VENDOR;
    }

    @Override
    protected List<JdkMetadata> scrape() throws Exception {
        List<JdkMetadata> allMetadata = new ArrayList<>();

        log("Fetching releases from GitHub");
        String releasesUrl =
                String.format(
                        "%s/%s/%s/releases?per_page=100",
                        GITHUB_API_BASE, GITHUB_ORG, GITHUB_REPO);
        String json = httpUtils.downloadString(releasesUrl);
        JsonNode releases = objectMapper.readTree(json);

        if (!releases.isArray()) {
            log("No releases found");
            return allMetadata;
        }

        for (JsonNode release : releases) {
            String tagName = release.get("tag_name").asText();
            
            // Only process releases starting with "vm-"
            if (!tagName.startsWith("vm-")) {
                continue;
            }
            
            log("Processing release: " + tagName);

            JsonNode assets = release.get("assets");
            if (assets != null && assets.isArray()) {
                for (JsonNode asset : assets) {
                    String assetName = asset.get("name").asText();
                    
                    // Only process graalvm-ce files
                    if (assetName.startsWith("graalvm-ce")) {
                        try {
                            processAsset(tagName, assetName, allMetadata);
                        } catch (Exception e) {
                            log("Failed to process " + assetName + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        return allMetadata;
    }

    private void processAsset(String tagName, String assetName, List<JdkMetadata> allMetadata)
            throws Exception {
        
        if (metadataExists(assetName)) {
            log("Skipping " + assetName + " (already exists)");
            return;
        }

        String releaseType;
        String os;
        String arch = "amd64"; // default
        String version;
        String ext;

        // Try RC pattern first
        Matcher rcMatcher = RC_PATTERN.matcher(assetName);
        if (rcMatcher.matches()) {
            releaseType = "ea";
            version = rcMatcher.group(1);
            os = rcMatcher.group(2);
            ext = rcMatcher.group(3);
        } else {
            // Try regular pattern
            Matcher regularMatcher = REGULAR_PATTERN.matcher(assetName);
            if (!regularMatcher.matches()) {
                log("Skipping " + assetName + " (does not match pattern)");
                return;
            }
            
            // Check if it's a dev build
            releaseType = assetName.contains("dev-b") ? "ea" : "ga";
            os = regularMatcher.group(1);
            arch = regularMatcher.group(2);
            version = regularMatcher.group(3);
            ext = regularMatcher.group(4);
        }

        String url =
                String.format(
                        "https://github.com/%s/%s/releases/download/%s/%s",
                        GITHUB_ORG, GITHUB_REPO, tagName, assetName);

        // Download and compute hashes
        DownloadResult download = downloadFile(url, assetName);

        // Create metadata
        JdkMetadata metadata = new JdkMetadata();
        metadata.setVendor(VENDOR);
        metadata.setFilename(assetName);
        metadata.setReleaseType(releaseType);
        metadata.setVersion(version);
        metadata.setJavaVersion("8"); // Legacy GraalVM was based on Java 8
        metadata.setJvmImpl("graalvm");
        metadata.setOs(normalizeOs(os));
        metadata.setArchitecture(normalizeArch(arch));
        metadata.setFileType(ext);
        metadata.setImageType("jdk");
        metadata.setFeatures(new ArrayList<>());
        metadata.setUrl(url);
        metadata.setMd5(download.md5());
        metadata.setMd5File(assetName + ".md5");
        metadata.setSha1(download.sha1());
        metadata.setSha1File(assetName + ".sha1");
        metadata.setSha256(download.sha256());
        metadata.setSha256File(assetName + ".sha256");
        metadata.setSha512(download.sha512());
        metadata.setSha512File(assetName + ".sha512");
        metadata.setSize(download.size());

        saveMetadataFile(metadata);
        allMetadata.add(metadata);
        log("Processed " + assetName);
    }
}

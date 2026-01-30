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

/** Base class for Trava OpenJDK scrapers with DCEVM */
public abstract class TravaBaseScraper extends BaseScraper {
    private static final String VENDOR = "trava";
    private static final String GITHUB_ORG = "TravaOpenJDK";
    private static final String GITHUB_API_BASE = "https://api.github.com/repos";

    public TravaBaseScraper(Path metadataDir, Path checksumDir, Logger logger) {
        super(metadataDir, checksumDir, logger);
    }

    /** Get the GitHub repository name */
    protected abstract String getGithubRepo();

    /** Get the tag pattern for parsing version from release tags */
    protected abstract Pattern getTagPattern();

    /** Get the filename pattern for parsing asset names */
    protected abstract Pattern getFilenamePattern();

    /** Extract version from tag matcher */
    protected abstract String extractVersionFromTag(Matcher tagMatcher);

    /** Get the default architecture when not specified in filename */
    protected String getDefaultArchitecture() {
        return "x86_64";
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
                        GITHUB_API_BASE, GITHUB_ORG, getGithubRepo());
        String json = httpUtils.downloadString(releasesUrl);
        JsonNode releases = objectMapper.readTree(json);

        if (!releases.isArray()) {
            log("No releases found");
            return allMetadata;
        }

        for (JsonNode release : releases) {
            String tagName = release.get("tag_name").asText();
            log("Processing release: " + tagName);

            // Parse version from tag
            Matcher tagMatcher = getTagPattern().matcher(tagName);
            if (!tagMatcher.matches()) {
                log("Skipping tag " + tagName + " (does not match pattern)");
                continue;
            }

            String version = extractVersionFromTag(tagMatcher);

            JsonNode assets = release.get("assets");
            if (assets != null && assets.isArray()) {
                for (JsonNode asset : assets) {
                    String contentType = asset.path("content_type").asText("");
                    String assetName = asset.get("name").asText();

                    // Skip source files and jar files
                    if (assetName.contains("_source") || assetName.endsWith(".jar")) {
                        continue;
                    }

                    // Only process application files
                    if (contentType.startsWith("application")) {
                        try {
                            processAsset(tagName, assetName, version, allMetadata);
                        } catch (Exception e) {
                            log("Failed to process " + assetName + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        return allMetadata;
    }

    protected void processAsset(
            String tagName, String assetName, String version, List<JdkMetadata> allMetadata)
            throws Exception {

        Matcher filenameMatcher = getFilenamePattern().matcher(assetName);
        if (!filenameMatcher.matches()) {
            log("Skipping " + assetName + " (does not match pattern)");
            return;
        }

        String os = filenameMatcher.group(1);
        String arch = filenameMatcher.groupCount() >= 2 ? filenameMatcher.group(2) : null;
        String ext = filenameMatcher.group(filenameMatcher.groupCount());

        // Default to configured default architecture if arch is not specified
        if (arch == null || arch.isEmpty()) {
            arch = getDefaultArchitecture();
        }

        String metadataFilename = VENDOR + "-" + version + "-" + os + "-" + arch + "." + ext;

        if (metadataExists(metadataFilename)) {
            log("Skipping " + metadataFilename + " (already exists)");
            return;
        }

        String url =
                String.format(
                        "https://github.com/%s/%s/releases/download/%s/%s",
                        GITHUB_ORG, getGithubRepo(), tagName, assetName);

        // Download and compute hashes
        DownloadResult download = downloadFile(url, metadataFilename);

        // Create metadata
        JdkMetadata metadata = new JdkMetadata();
        metadata.setVendor(VENDOR);
        metadata.setFilename(assetName);
        metadata.setReleaseType("ga");
        metadata.setVersion(version);
        metadata.setJavaVersion(version);
        metadata.setJvmImpl("hotspot");
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

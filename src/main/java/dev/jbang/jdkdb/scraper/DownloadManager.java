package dev.jbang.jdkdb.scraper;

import dev.jbang.jdkdb.model.JdkMetadata;

/**
 * Interface for managing parallel downloads of JDK files. Implementations receive JdkMetadata from
 * scrapers and handle the downloading of files.
 */
public interface DownloadManager {
	/**
	 * Submit a metadata item for download.
	 *
	 * @param metadata The JDK metadata containing the URL to download
	 * @param scraper The scraper that submitted this download (for progress reporting)
	 */
	void submit(JdkMetadata metadata, BaseScraper scraper);
}

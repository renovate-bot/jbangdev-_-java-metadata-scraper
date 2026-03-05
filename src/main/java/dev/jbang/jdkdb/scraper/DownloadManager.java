package dev.jbang.jdkdb.scraper;

import dev.jbang.jdkdb.model.JdkMetadata;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Interface for managing parallel downloads of JDK files. Implementations
 * receive JdkMetadata from
 * scrapers and handle the downloading of files.
 */
public interface DownloadManager {
	/**
	 * Submit a metadata item for download.
	 *
	 * @param metadata The JDK metadata containing the URL to download
	 * @param distro   The distro name
	 * @param logger   The logger to use for logging download progress and errors
	 */
	void submit(JdkMetadata metadata, String distro, Logger logger);

	/**
	 * Start the download manager. Should be called once after construction.
	 */
	void start();

	/**
	 * Signal that no more downloads will be submitted. Call this after all scrapers
	 * have finished.
	 */
	void shutdown();

	/**
	 * Wait for all queued downloads to complete. This method blocks until all
	 * downloads are
	 * finished.
	 *
	 * @throws InterruptedException if interrupted while waiting
	 */
	void awaitCompletion() throws InterruptedException;

	/**
	 * Get the number of completed downloads.
	 *
	 * @return Number of successfully completed downloads
	 */
	int getCompletedCount();

	/**
	 * Get the number of failed downloads.
	 *
	 * @return Number of failed downloads
	 */
	int getFailedCount();

	/**
	 * Get per-distro download statistics.
	 *
	 * @return Map of distro name to statistics
	 */
	Map<String, DistroStats> getDistroStats();

	/**
	 * Statistics for a single distro's downloads.
	 */
	public record DistroStats(String distro, int submitted, int completed, int failed) {
		/**
		 * Get the number of downloads that were submitted.
		 *
		 * @return Number of downloads submitted for this distro
		 */
		public int submitted() {
			return submitted;
		}

		/**
		 * Get the number of downloads that completed successfully.
		 *
		 * @return Number of successful downloads for this distro
		 */
		public int completed() {
			return completed;
		}

		/**
		 * Get the number of downloads that failed.
		 *
		 * @return Number of failed downloads for this distro
		 */
		public int failed() {
			return failed;
		}

		/**
		 * Get the number of downloads still pending (submitted but not completed or
		 * failed).
		 *
		 * @return Number of pending downloads for this distro
		 */
		public int pending() {
			return submitted - completed - failed;
		}
	}
}

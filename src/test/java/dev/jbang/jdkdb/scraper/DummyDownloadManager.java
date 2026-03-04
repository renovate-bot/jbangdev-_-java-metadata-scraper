package dev.jbang.jdkdb.scraper;

import dev.jbang.jdkdb.model.JdkMetadata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Dummy implementation of DownloadManager for testing. Does not actually download files, but
 * tracks submitted downloads for verification in tests.
 */
public class DummyDownloadManager implements DownloadManager {
	private final List<SubmittedDownload> submittedDownloads = new ArrayList<>();

	/**
	 * Create a new DummyDownloadManager.
	 */
	public DummyDownloadManager() {}

	@Override
	public void submit(JdkMetadata metadata, String vendor, Logger downloadLogger) {
		submittedDownloads.add(new SubmittedDownload(metadata, vendor, downloadLogger));
	}

	@Override
	public void start() {
		// No-op for testing
	}

	@Override
	public void shutdown() {
		// No-op for testing
	}

	@Override
	public void awaitCompletion() {
		// No-op for testing
	}

	@Override
	public int getCompletedCount() {
		return 0;
	}

	@Override
	public int getFailedCount() {
		return 0;
	}

	/**
	 * Get all submitted downloads for verification in tests.
	 *
	 * @return List of all submitted downloads
	 */
	public List<SubmittedDownload> getSubmittedDownloads() {
		return new ArrayList<>(submittedDownloads);
	}

	/**
	 * Get the count of submitted downloads.
	 *
	 * @return Number of downloads submitted
	 */
	public int getSubmittedCount() {
		return submittedDownloads.size();
	}

	/**
	 * Get per-vendor download statistics.
	 *
	 * @return Map of vendor name to statistics
	 */
	@Override
	public Map<String, VendorStats> getVendorStats() {
		Map<String, VendorStats> stats = new HashMap<>();
		Map<String, Integer> vendorCounts = new HashMap<>();

		// Count submissions per vendor
		for (SubmittedDownload download : submittedDownloads) {
			vendorCounts.merge(download.vendor, 1, Integer::sum);
		}

		// Create stats (all submissions are treated as completed in dummy mode)
		for (Map.Entry<String, Integer> entry : vendorCounts.entrySet()) {
			stats.put(entry.getKey(), new VendorStats(entry.getKey(), entry.getValue(), entry.getValue(), 0));
		}

		return stats;
	}

	/** Record of a submitted download */
	public record SubmittedDownload(JdkMetadata metadata, String vendor, Logger downloadLogger) {}
}

package dev.jbang.jdkdb;

import dev.jbang.jdkdb.model.JdkMetadata;
import dev.jbang.jdkdb.util.MetadataUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Clean command to remove incomplete metadata files and prune old EA releases */
@Command(
		name = "clean",
		description = "Clean up metadata by removing incomplete files and pruning old EA releases",
		mixinStandardHelpOptions = true)
public class CleanCommand implements Callable<Integer> {
	private static final Logger logger = LoggerFactory.getLogger("command");

	/** Enum for incomplete metadata types */
	public enum IncompleteType {
		checksums,
		release_info,
		all
	}

	@Option(
			names = {"-m", "--metadata-dir"},
			description = "Directory containing metadata files (default: docs/metadata)",
			defaultValue = "docs/metadata")
	private Path metadataDir;

	@Option(
			names = {"--remove-incomplete"},
			description =
					"Remove metadata files with incomplete data. Options: checksums (missing checksums), release-info (missing release info), all (either missing checksums or release info) (default: all)")
	private IncompleteType removeIncomplete;

	@Option(
			names = {"--remove-invalid"},
			description = "Remove metadata files that fail validation (MetadataUtils.isValidMetadata)")
	private boolean removeInvalid;

	@Option(
			names = {"--prune-ea"},
			description =
					"Prune EA releases older than specified duration (e.g., 30d, 3w, 6m, 1y). Duration format: [number][d|w|m|y]")
	private String pruneEa;

	@Option(
			names = {"--dry-run"},
			description = "Show statistics without actually deleting files")
	private boolean dryRun;

	@Override
	public Integer call() throws Exception {
		logger.info("Java Metadata Scraper - Clean");
		logger.info("=============================");
		logger.info("Metadata directory: {}", metadataDir.toAbsolutePath());

		// Apply default values if no options specified
		if (removeIncomplete == null && !removeInvalid && pruneEa == null && !dryRun) {
			logger.info("No options specified, using defaults: --remove-incomplete=all --prune-ea=6m --dry-run");
			logger.info("");
			removeIncomplete = IncompleteType.all;
			pruneEa = "6m";
			dryRun = true;
		}

		logger.info("Configuration:");
		logger.info(
				"  Remove incomplete: {}",
				(removeIncomplete != null
						? removeIncomplete.toString().toLowerCase().replace("_", "-")
						: "disabled"));
		logger.info("  Remove invalid: {}", removeInvalid);
		logger.info("  Prune EA: {}", (pruneEa != null ? pruneEa : "disabled"));
		logger.info("  Dry run: {}", dryRun);
		logger.info("");

		Path vendorDir = metadataDir.resolve("vendor");
		if (!Files.exists(vendorDir) || !Files.isDirectory(vendorDir)) {
			logger.error("Error: Vendor directory not found: {}", vendorDir.toAbsolutePath());
			return 1;
		}

		// Parse prune-ea duration if specified
		Instant pruneThreshold = null;
		if (pruneEa != null) {
			Duration duration = MetadataUtils.parseDuration(pruneEa);
			if (duration == null) {
				logger.error("Error: Invalid duration format: {}", pruneEa);
				logger.error("Expected format: [number][d|w|m|y] (e.g., 30d, 3w, 6m, 1y)");
				return 1;
			}
			pruneThreshold = Instant.now().minus(duration);
			logger.info("Pruning EA releases older than: {} (before {})", pruneEa, pruneThreshold);
			logger.info("");
		}

		// Collect files to delete
		final CleanStats stats = new CleanStats();
		final List<Path> filesToDelete = new ArrayList<>();
		final Instant finalPruneThreshold = pruneThreshold;

		List<JdkMetadata> metadataList = MetadataUtils.collectAllMetadata(vendorDir, 2, true, true);
		for (JdkMetadata metadata : metadataList) {
			try {
				processMetadataFile(metadata, stats, filesToDelete, finalPruneThreshold);
			} catch (IOException e) {
				logger.error("Failed to process {}: {}", metadata.metadataFile().getFileName(), e.getMessage());
				stats.errors++;
			}
		}

		// Print summary
		logger.info("");
		logger.info("Summary:");
		logger.info("========");
		logger.info("Total files scanned: {}", stats.totalFiles);
		logger.info("Incomplete files (total): {}", stats.incompleteChecksums + stats.incompleteReleaseInfo);
		logger.info("   - missing checksums): {}", stats.incompleteChecksums);
		logger.info("   - missing release info): {}", stats.incompleteReleaseInfo);
		logger.info("Invalid files: {}", stats.invalidFiles);
		logger.info("Old EA releases: {}", stats.oldEaReleases);
		logger.info("Errors: {}", stats.errors);
		logger.info("");

		if (filesToDelete.isEmpty()) {
			logger.info("No files to delete.");
			return 0;
		}

		logger.info("Files to delete: {}", filesToDelete.size());

		if (dryRun) {
			logger.info("");
			logger.info("DRY RUN - No files were actually deleted.");
			logger.info("Run without --dry-run to perform actual deletion.");
		} else {
			logger.info("");
			logger.info("Deleting files...");
			int deletedCount = 0;
			int failedCount = 0;

			for (Path file : filesToDelete) {
				try {
					Files.delete(file);
					deletedCount++;
					logger.info("  Deleted: {}", file.getFileName());
				} catch (IOException e) {
					logger.error("  Failed to delete {}: {}", file.getFileName(), e.getMessage());
					failedCount++;
				}
			}

			logger.info("");
			logger.info("Deleted: {} files", deletedCount);
			if (failedCount > 0) {
				logger.info("Failed: {} files", failedCount);
			}
		}

		return 0;
	}

	private void processMetadataFile(
			JdkMetadata metadata, CleanStats stats, List<Path> filesToDelete, Instant pruneThreshold)
			throws IOException {
		stats.totalFiles++;

		Path metadataFile = metadata.metadataFile();

		boolean shouldDelete = false;
		String reason = null;

		// Check for invalid metadata
		if (removeInvalid && !shouldDelete) {
			if (!MetadataUtils.isValidMetadata(metadata)) {
				stats.invalidFiles++;
				shouldDelete = true;
				reason = "invalid (failed validation)";
			}
		}

		// Check for incomplete metadata
		if (removeIncomplete != null && !shouldDelete) {
			boolean missingChecksums = metadata.md5() == null
					|| metadata.sha1() == null
					|| metadata.sha256() == null
					|| metadata.sha512() == null;
			boolean missingReleaseInfo = metadata.releaseInfo() == null;

			boolean isIncomplete =
					switch (removeIncomplete) {
						case checksums -> missingChecksums;
						case release_info -> missingReleaseInfo;
						case all -> missingChecksums || missingReleaseInfo;
					};

			if (isIncomplete) {
				shouldDelete = true;
				if (removeIncomplete == IncompleteType.checksums || (missingChecksums && !missingReleaseInfo)) {
					reason = "incomplete - missing checksums";
					stats.incompleteChecksums++;
				} else if (removeIncomplete == IncompleteType.release_info
						|| (missingReleaseInfo && !missingChecksums)) {
					reason = "incomplete - missing release info";
					stats.incompleteReleaseInfo++;
				} else {
					reason = "incomplete - missing checksums and release info";
					stats.incompleteChecksums++;
					stats.incompleteReleaseInfo++;
				}
			}
		}

		// Check for old EA releases
		if (pruneThreshold != null && "ea".equalsIgnoreCase(metadata.releaseType()) && !shouldDelete) {
			FileTime lastModified = Files.getLastModifiedTime(metadataFile);
			if (lastModified.toInstant().isBefore(pruneThreshold)) {
				stats.oldEaReleases++;
				shouldDelete = true;
				reason = "old EA release (last modified: " + lastModified.toInstant() + ")";
			}
		}

		if (shouldDelete) {
			filesToDelete.add(metadataFile);
			logger.debug("  - {} ({})", metadataFile.getFileName(), reason);
		}
	}

	/** Statistics for clean operation */
	private static class CleanStats {
		int totalFiles = 0;
		int incompleteChecksums = 0;
		int incompleteReleaseInfo = 0;
		int invalidFiles = 0;
		int oldEaReleases = 0;
		int errors = 0;
	}
}

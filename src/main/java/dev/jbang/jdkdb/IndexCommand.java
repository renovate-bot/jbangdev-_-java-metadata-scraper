package dev.jbang.jdkdb;

import dev.jbang.jdkdb.util.MetadataUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Index command to generate all.json files for vendor directories */
@Command(
		name = "index",
		description = "Generate all.json files for vendor directories by aggregating individual metadata files",
		mixinStandardHelpOptions = true)
public class IndexCommand implements Callable<Integer> {
	private static final Logger logger = LoggerFactory.getLogger("command");

	@Option(
			names = {"-m", "--metadata-dir"},
			description = "Directory containing metadata files (default: docs/metadata)",
			defaultValue = "docs/metadata")
	private Path metadataDir;

	@Option(
			names = {"-v", "--vendors"},
			description =
					"Comma-separated list of vendor names to regenerate all.json for (if not specified, all vendors are processed)",
			split = ",")
	private List<String> vendorNames;

	@Option(
			names = {"--allow-incomplete"},
			description = "Allow incomplete metadata files (missing checksums) to be included")
	private boolean allowIncomplete;

	@Override
	public Integer call() throws Exception {
		logger.info("Java Metadata Scraper - Index");
		logger.info("=============================");
		logger.info("Metadata directory: {}", metadataDir.toAbsolutePath());
		logger.info("");

		Path vendorDir = metadataDir.resolve("vendor");
		if (!Files.exists(vendorDir) || !Files.isDirectory(vendorDir)) {
			logger.error("Error: Vendor directory not found: {}", vendorDir.toAbsolutePath());
			return 1;
		}

		// Determine which vendors to process
		List<String> vendorsToProcess;
		if (vendorNames == null || vendorNames.isEmpty()) {
			// Process all vendors
			try (Stream<Path> paths = Files.list(vendorDir)) {
				vendorsToProcess = paths.filter(Files::isDirectory)
						.map(Path::getFileName)
						.map(Path::toString)
						.sorted()
						.toList();
			}
			logger.info("Processing all vendors...");
		} else {
			vendorsToProcess = vendorNames;
			logger.info("Processing specified vendors: {}", String.join(", ", vendorNames));
		}
		logger.info("");

		int result = generateIndices(metadataDir, vendorsToProcess, allowIncomplete);

		return result;
	}

	public static Integer generateIndices(Path metadataDir, List<String> vendorsToProcess, boolean allowIncomplete) {
		int successful = 0;
		int failed = 0;

		Path vendorDir = metadataDir.resolve("vendor");
		if (!Files.exists(vendorDir) || !Files.isDirectory(vendorDir)) {
			logger.error("Error: Vendor directory not found: {}", vendorDir.toAbsolutePath());
			return 1;
		}

		for (String vendorName : vendorsToProcess) {
			Path vendorPath = vendorDir.resolve(vendorName);
			if (!Files.exists(vendorPath) || !Files.isDirectory(vendorPath)) {
				logger.warn("Warning: Vendor directory not found: {}", vendorName);
				failed++;
				continue;
			}

			try {
				MetadataUtils.generateAllJsonFromDirectory(vendorPath, allowIncomplete);
				successful++;
			} catch (Exception e) {
				logger.error("Failed for vendor {}: {}", vendorName, e.getMessage(), e);
				failed++;
			}
		}

		try {
			MetadataUtils.generateComprehensiveIndices(metadataDir, allowIncomplete);
		} catch (Exception e) {
			logger.error("Failed to generate comprehensive indices: {}", e.getMessage(), e);
			failed++;
		}

		logger.info("");
		logger.info("Index Generation Summary");
		logger.info("========================");
		logger.info("Total vendors: {}", vendorsToProcess.size());
		logger.info("Successful: {}", successful);
		logger.info("Failed: {}", failed);

		return failed > 0 ? 1 : 0;
	}
}

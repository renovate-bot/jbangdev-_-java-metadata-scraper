package dev.jbang.jdkdb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Utility class for file operations */
public class FileUtils {

	/** Ensure a directory exists, creating it if necessary */
	public static void ensureDirectory(Path directory) throws IOException {
		if (!Files.exists(directory)) {
			Files.createDirectories(directory);
		}
	}

	/** Get the size of a file in bytes */
	public static long getFileSize(Path file) throws IOException {
		return Files.size(file);
	}

	// Recursively delete a directory and all its contents
	// ignoring any exceptions to ensure best effort cleanup
	public static void deleteDirectory(Path directory) {
		if (Files.exists(directory)) {
			try {
				Files.walk(directory)
						.sorted((a, b) -> b.compareTo(a)) // delete children before parents
						.forEach(path -> {
							try {
								Files.delete(path);
							} catch (IOException e) {
								// ignore: best effort cleanup
							}
						});
			} catch (IOException e) {
				// ignore: best effort cleanup
			}
		}
	}
}

package dev.jbang.jdkdb;

import dev.jbang.jdkdb.util.HttpUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** Main application class with CLI support */
@Command(
		name = "jdkdb-scraper",
		version = "1.0.0",
		description = "Scrapes JDK metadata from various vendors and generates index files",
		mixinStandardHelpOptions = true,
		subcommands = {UpdateCommand.class, IndexCommand.class, DownloadCommand.class, CleanCommand.class})
public class Main {
	private static final String GITHUB_TOKEN_ENV = "GITHUB_TOKEN";

	protected static void setupGitHubToken() {
		String githubToken;
		String fromEnv = System.getenv(GITHUB_TOKEN_ENV);
		if (fromEnv != null && !fromEnv.isBlank()) {
			githubToken = fromEnv.trim();
			System.out.println("Using GitHub token from " + GITHUB_TOKEN_ENV);
		} else {
			githubToken = runGhAuthToken();
			if (githubToken != null) {
				System.out.println("Using GitHub token from gh auth token");
			} else {
				System.out.println("No GitHub token found (set " + GITHUB_TOKEN_ENV
						+ " or run 'gh auth login'); API rate limits may apply");
			}
		}
		if (githubToken != null) {
			System.setProperty(HttpUtils.GITHUB_TOKEN_PROP, githubToken);
		}
	}

	private static String runGhAuthToken() {
		try {
			Process process = new ProcessBuilder("gh", "auth", "token")
					.redirectErrorStream(false)
					.start();
			String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
			int exit = process.waitFor();
			if (exit == 0 && !output.isBlank()) {
				return output;
			}
		} catch (IOException | InterruptedException e) {
			// ignore: no token available
		}
		return null;
	}

	public static void main(String[] args) {
		setupGitHubToken();
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}
}

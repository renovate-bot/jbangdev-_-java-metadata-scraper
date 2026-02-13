package dev.jbang.jdkdb.scraper;

/** Result of a scraper execution */
public record ScraperResult(boolean success, int itemsProcessed, int itemsSkipped, int itemsFailed, Exception error) {

	public static ScraperResult success(int itemsProcessed, int itemsSkipped, int itemsFailed) {
		return new ScraperResult(true, itemsProcessed, itemsSkipped, itemsFailed, null);
	}

	public static ScraperResult failure(Exception error) {
		return new ScraperResult(false, 0, 0, 0, error);
	}

	@Override
	public String toString() {
		return success
				? "SUCCESS (%d items processed, %d items skipped, %d items failed)"
						.formatted(itemsProcessed, itemsSkipped, itemsFailed)
				: "FAILED - %s".formatted(error != null ? error.getMessage() : "Unknown error");
	}
}

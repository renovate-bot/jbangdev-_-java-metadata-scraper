package dev.jbang.jdkdb.scraper;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Abstract base class for paginated iteration over API results.
 * Handles lazy fetching of pages as the iterator is consumed.
 */
public abstract class PaginatedIterator<T> implements Iterator<T> {
	private int currentPage = 1;
	private Iterator<T> currentBatch = null;
	private boolean hasMore = true;

	@Override
	public boolean hasNext() {
		if (currentBatch != null && currentBatch.hasNext()) {
			return true;
		}
		if (!hasMore) {
			return false;
		}
		fetchNextBatch();
		return currentBatch != null && currentBatch.hasNext();
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return currentBatch.next();
	}

	private void fetchNextBatch() {
		try {
			List<T> items = fetchPage(currentPage);

			if (items == null || items.isEmpty()) {
				hasMore = false;
				currentBatch = null;
				return;
			}

			currentBatch = items.iterator();
			currentPage++;
		} catch (Exception e) {
			handleFetchError(e);
			hasMore = false;
			currentBatch = null;
		}
	}

	/**
	 * Fetch a page of results from an API.
	 * @param pageNumber The page number to fetch (1-based)
	 * @return List of items for this page, or null/empty if no more pages
	 */
	protected abstract List<T> fetchPage(int pageNumber) throws Exception;

	/**
	 * Handle errors that occur during page fetching.
	 * @param e The exception that occurred
	 */
	protected abstract void handleFetchError(Exception e);
}

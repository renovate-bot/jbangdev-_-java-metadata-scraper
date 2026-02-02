package dev.jbang.jdkdb.scraper;

public class InterruptedProgressException extends RuntimeException {
	public InterruptedProgressException(String message) {
		super(message);
	}
}

package dev.jbang.jdkdb.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

/**
 * Logback discriminator that extracts distro name from logger names.
 * Expects logger names in the format "distros.{distroName}" and extracts the distro name portion.
 */
public class DistroLoggerDiscriminator implements Discriminator<ILoggingEvent> {
	private static final String KEY = "distroName";
	private boolean started = false;

	@Override
	public String getDiscriminatingValue(ILoggingEvent event) {
		String loggerName = event.getLoggerName();

		// Extract distro name from logger name (e.g., "distros.Temurin" -> "Temurin")
		if (loggerName != null && loggerName.startsWith("distros.")) {
			return loggerName.substring("distros.".length());
		}

		return "unknown";
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void start() {
		started = true;
	}

	@Override
	public void stop() {
		started = false;
	}

	@Override
	public boolean isStarted() {
		return started;
	}
}

package dev.jbang.jdkdb.scraper.distros;

import dev.jbang.jdkdb.scraper.Scraper;
import dev.jbang.jdkdb.scraper.ScraperConfig;
import java.util.ArrayList;
import java.util.List;

/** Scraper for IBM Semeru Open Edition releases */
public class Semeru extends SemeruBaseScraper {
	private static final String VENDOR = "ibm";
	private static final String DISTRO = "semeru";

	public Semeru(ScraperConfig config) {
		super(config);
	}

	@Override
	protected String getRepoSearchString() {
		return "semeru";
	}

	@Override
	protected String getRepoPattern() {
		return "^semeru\\d+-binaries$";
	}

	@Override
	protected String getFilenamePrefix() {
		return "ibm-semeru-open-";
	}

	@Override
	protected String getVendor() {
		return VENDOR;
	}

	@Override
	protected String getDistro() {
		return DISTRO;
	}

	@Override
	protected List<String> getAdditionalFeatures() {
		return new ArrayList<>();
	}

	public static class Discovery implements Scraper.Discovery {
		@Override
		public String name() {
			return DISTRO;
		}

		@Override
		public String distro() {
			return DISTRO;
		}

		@Override
		public String vendor() {
			return VENDOR;
		}

		@Override
		public Scraper create(ScraperConfig config) {
			return new Semeru(config);
		}
	}
}

package com.github.joschi.javametadata.scraper.vendors;

import com.github.joschi.javametadata.scraper.Scraper;
import com.github.joschi.javametadata.scraper.ScraperConfig;
import java.util.*;

/** Scraper for OpenJDK Project Valhalla early access builds */
public class OpenJdkValhalla extends OpenJdkBaseScraper {
    
    public OpenJdkValhalla(ScraperConfig config) {
        super(config);
    }


    @Override
    protected List<String> getIndexUrls() {
        return Collections.singletonList("http://jdk.java.net/valhalla/");
    }

    @Override
    protected String getFeature() {
        return "valhalla";
    }

    public static class Discovery implements Scraper.Discovery {
        @Override
        public String name() {
            return "openjdk-valhalla";
        }

        @Override
        public String vendor() {
            return "openjdk";
        }

        @Override
        public Scraper create(ScraperConfig config) {
            return new OpenJdkValhalla(config);
        }
    }
}

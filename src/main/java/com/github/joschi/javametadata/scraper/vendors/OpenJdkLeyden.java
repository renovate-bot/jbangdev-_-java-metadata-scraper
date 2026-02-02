package com.github.joschi.javametadata.scraper.vendors;

import com.github.joschi.javametadata.scraper.Scraper;
import com.github.joschi.javametadata.scraper.ScraperConfig;
import java.util.*;

/** Scraper for OpenJDK Project Leyden early access builds */
public class OpenJdkLeyden extends OpenJdkBaseScraper {
    
    public OpenJdkLeyden(ScraperConfig config) {
        super(config);
    }


    @Override
    protected List<String> getIndexUrls() {
        return Collections.singletonList("http://jdk.java.net/leyden/");
    }

    @Override
    protected String getFeature() {
        return "leyden";
    }

    public static class Discovery implements Scraper.Discovery {
        @Override
        public String name() {
            return "openjdk-leyden";
        }

        @Override
        public String vendor() {
            return "openjdk";
        }

        @Override
        public Scraper create(ScraperConfig config) {
            return new OpenJdkLeyden(config);
        }
    }
}

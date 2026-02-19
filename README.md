# jdkdb-scraper - JDK Metadata DB Scraper

A Java-based application for scraping JDK metadata from various vendors. This project replaces the original bash scripts with a robust, parallel Java implementation.

This project is based on [Joschi's Java Metadata project](https://github.com/joschi/java-metadata) and incorporates ideas from the [Foojay's Disco API project](https://github.com/foojayio/discoapi).

## Features

- **Parallel Execution**: Run multiple vendor scrapers concurrently for improved performance
- **Selective Scraping**: Run all scrapers or select specific vendors
- **Central Reporting**: Thread-safe progress reporting with real-time status updates
- **Extensible Architecture**: Easy to add new vendor scrapers
- **Generic Base Classes**: Reduces code duplication for similar vendors (e.g., Semeru versions, Trava versions)
- **Comprehensive Logging**: SLF4J/Logback integration with both console and file output
- **Multi-command CLI**: Separate commands for updating metadata, generating indexes, downloading checksums, and cleaning up old releases
- **Archive Extraction**: Automatically extracts release information from JDK archives

## Prerequisites

- Java 21 or higher

## GitHub Authentication

Many scrapers fetch data from GitHub APIs, which have rate limits. To avoid hitting these limits, the application supports GitHub authentication through:

1. **Environment Variable**: Set `GITHUB_TOKEN` to your GitHub personal access token
2. **GitHub CLI**: If you have the GitHub CLI (`gh`) installed and authenticated, the token will be automatically obtained from `gh auth token`

```bash
# Option 1: Using environment variable
export GITHUB_TOKEN=your_github_token_here
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update

# Option 2: Using GitHub CLI (automatic if gh is installed and authenticated)
gh auth login
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update
```

The application checks for tokens in this order: environment variable first, then GitHub CLI.

## Building

This project uses Gradle for dependency management and building.

```bash
# Build the project
./gradlew spotlessApply build

# This creates two jars:
# - jdkdb-scraper-1.0.0-SNAPSHOT.jar (regular jar)
# - jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar (fat jar with all dependencies)
```

## Usage

The application provides four main commands:
- **`update`** - Scrape JDK metadata from various vendors and update metadata files
- **`index`** - Generate aggregated all.json files for vendor directories
- **`download`** - Download and compute checksums for metadata files with missing checksums
- **`clean`** - Clean up metadata by removing incomplete files and pruning old EA releases

### Running using the standalone JAR:

```bash
# Show available commands
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar --help

# Update: Run all scrapers
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update

# Update: List available scrapers
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update --list

# Update: Run specific scrapers
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update --scrapers microsoft,semeru,temurin

# Update: Specify custom directories and control parallelism
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update \
  --metadata-dir /path/to/metadata \
  --checksum-dir /path/to/checksums \
  --threads 4

# Update: Scrape from start (ignore existing metadata)
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update --from-start

# Update: Limit progress for testing
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar update --limit-progress 5

# Index: Generate all.json files for all vendors
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar index

# Index: Regenerate all.json for specific vendors
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar index --vendors temurin,zulu

# Download: Download and compute missing checksums for all vendors
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar download

# Download: Process specific vendors
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar download --vendors microsoft

# Download: Show statistics only (dry-run)
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar download --stats-only

# Clean: Remove incomplete metadata files and prune old EA releases (dry-run)
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar clean --dry-run

# Clean: Actually remove incomplete files and prune EA releases older than 6 months
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar clean --remove-incomplete=all --prune-ea=6m

# Clean: Remove orphaned checksum files
java -jar build/libs/jdkdb-scraper-1.0.0-SNAPSHOT-standalone.jar clean --prune-checksums
```

## Command Line Options

### Main Command

```
Usage: jdkdb-scraper [-hV] [COMMAND]
Scrapes JDK metadata from various vendors and generates index files
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  update    Scrape JDK metadata from various vendors and update metadata files
  index     Generate all.json files for vendor directories by aggregating
              individual metadata files
  download  Download and compute checksums for metadata files that have missing
              checksum values
  clean     Clean up metadata by removing incomplete files and pruning old EA
              releases
```

### Update Command

```
Usage: jdkdb-scraper update [-hlV] [--from-start] [--no-download] [--no-index]
                            [-c=<checksumDir>]
                            [--limit-progress=<limitProgress>]
                            [-m=<metadataDir>] [--max-failures=<maxFailures>]
                            [--skip-ea=<skipEa>] [-t=<maxThreads>]
                            [-s=<scraperIds>[,<scraperIds>...]]...

Scrape JDK metadata from various vendors and update metadata files

Options:
  -c, --checksum-dir=<checksumDir>
                     Directory to store checksum files (default: docs/checksums)
      --from-start   Ignore existing metadata files and scrape all items from
                       the start
  -h, --help         Show this help message and exit.
  -l, --list         List all available scraper IDs and exit
      --limit-progress=<limitProgress>
                     Maximum number of metadata items to process per scraper
                       before aborting (default: unlimited)
  -m, --metadata-dir=<metadataDir>
                     Directory to store metadata files (default: docs/metadata)
      --max-failures=<maxFailures>
                     Maximum number of allowed failures per scraper before
                       aborting that scraper (default: 10)
      --no-download  Skip downloading files and only generate metadata (for
                       testing/dry-run)
      --no-index     Skip generating index files (for testing/dry-run)
  -s, --scrapers=<scraperIds>[,<scraperIds>...]
                     Comma-separated list of scraper IDs to run (if not
                       specified, all scrapers run)
      --skip-ea=<skipEa>
                     Skip early access (EA) releases older than the specified
                       duration (e.g., '6m' for 6 months, '1y' for 1 year)
                       (default: 6m)
  -t, --threads=<maxThreads>
                     Maximum number of parallel scraper threads (default:
                       number of processors)
  -V, --version      Print version information and exit.
```

### Index Command

```
Usage: jdkdb-scraper index [-hV] [--allow-incomplete] [-m=<metadataDir>]
                           [-v=<vendorNames>[,<vendorNames>...]]...

Generate all.json files for vendor directories by aggregating individual
metadata files

Options:
      --allow-incomplete
                     Allow incomplete metadata files (missing checksums) to be
                       included
  -h, --help         Show this help message and exit.
  -m, --metadata-dir=<metadataDir>
                     Directory containing metadata files (default:
                       docs/metadata)
  -v, --vendors=<vendorNames>[,<vendorNames>...]
                     Comma-separated list of vendor names to regenerate
                       all.json for (if not specified, all vendors are
                       processed)
  -V, --version      Print version information and exit.
```

### Download Command

```
Usage: jdkdb-scraper download [-hV] [--stats-only] [-c=<checksumDir>]
                              [--limit-progress=<limitProgress>]
                              [-m=<metadataDir>] [-t=<maxThreads>]
                              [-v=<vendorNames>[,<vendorNames>...]]...

Download and compute checksums for metadata files that have missing checksum
values

Options:
  -c, --checksum-dir=<checksumDir>
                     Directory to store checksum files (default: docs/checksums)
  -h, --help         Show this help message and exit.
      --limit-progress=<limitProgress>
                     Maximum number of metadata items to process per scraper
                       before aborting (default: unlimited)
  -m, --metadata-dir=<metadataDir>
                     Directory containing metadata files (default:
                       docs/metadata)
      --stats-only   Skip downloading files and only show statistics (for
                       testing/dry-run)
  -t, --threads=<maxThreads>
                     Maximum number of parallel download threads (default:
                       number of processors)
  -v, --vendors=<vendorNames>[,<vendorNames>...]
                     Comma-separated list of vendor names to process (if not
                       specified, all vendors are processed)
  -V, --version      Print version information and exit.
```

### Clean Command

```
Usage: jdkdb-scraper clean [-hV] [--dry-run] [--prune-checksums]
                           [--remove-invalid] [-c=<checksumDir>]
                           [-m=<metadataDir>] [--prune-ea=<pruneEa>]
                           [--remove-incomplete=<removeIncomplete>]

Clean up metadata by removing incomplete files and pruning old EA releases

Options:
  -c, --checksum-dir=<checksumDir>
                     Directory containing checksum files (default:
                       docs/checksums)
      --dry-run      Show statistics without actually deleting files
  -h, --help         Show this help message and exit.
  -m, --metadata-dir=<metadataDir>
                     Directory containing metadata files (default:
                       docs/metadata)
      --prune-checksums
                     Remove orphaned checksum files that don't have a matching
                       metadata file
      --prune-ea=<pruneEa>
                     Prune EA releases older than specified duration (e.g.,
                       30d, 3w, 6m, 1y). Duration format: [number][d|w|m|y]
      --remove-incomplete=<removeIncomplete>
                     Remove metadata files with incomplete data. Options:
                       checksums (missing checksums), release-info (missing
                       release info), all (either missing checksums or release
                       info) (default: all)
      --remove-invalid
                     Remove metadata files that fail validation
                       (MetadataUtils.isValidMetadata)
  -V, --version      Print version information and exit.
```

## Architecture

### Core Components

- **Main**: Entry point with Picocli command dispatcher
- **UpdateCommand**: Scrapes JDK metadata from vendors and updates files
- **IndexCommand**: Aggregates individual metadata files into all.json files
- **DownloadCommand**: Downloads JDK files to compute missing checksums
- **CleanCommand**: Cleans up incomplete metadata and prunes old EA releases
- **ProgressReporter**: Central reporting thread that receives and logs progress events from all scrapers
- **ScraperConfig**: Configuration record encapsulating metadata directory, checksum directory, logger, etc
- **BaseScraper**: Abstract base class for all scrapers with common functionality (downloading, hashing, metadata saving)
- **GitHubReleaseScraper**: Specialized base class for scrapers that fetch releases from GitHub
- **AdoptiumMarketplaceScraper**: Specialized base class for scrapers using Adoptium Marketplace API
- **JavaNetBaseScraper**: Specialized base class for scrapers that fetch from java.net archives
- **ScraperFactory**: Factory class that uses ServiceLoader to dynamically discover and instantiate scrapers
- **Scraper.Discovery**: Service provider interface for scraper registration via Java ServiceLoader
- **DownloadManager**: Interface for downloading JDK files (with default and no-op implementations)

### Vendor Scrapers

The project includes **35 vendor scrapers**, supporting all major JDK distributions:

#### Scraper IDs and Vendors

| Scraper ID | Vendor | Notes |
|------------|--------|-------|
| `adoptopenjdk` | AdoptOpenJDK | Legacy |
| `bisheng` | Bisheng | Huawei |
| `corretto` | Amazon Corretto | |
| `debian` | Debian | |
| `dragonwell` | Alibaba Dragonwell | |
| `gluon-graalvm` | Gluon GraalVM | |
| `graalvm-ce` | GraalVM CE | Early Access |
| `graalvm-community` | GraalVM Community | |
| `graalvm-community-ea` | GraalVM Community | Early Access |
| `graalvm-legacy` | GraalVM | Legacy versions |
| `ibm` | IBM JDK | |
| `java-se-ri` | Java SE RI | Reference Implementation |
| `jetbrains` | JetBrains Runtime | |
| `kona` | Tencent Kona | |
| `liberica` | BellSoft Liberica | |
| `liberica-native` | BellSoft Liberica Native | Native Image Kit |
| `mandrel` | Mandrel | Red Hat's GraalVM |
| `microsoft` | Microsoft | Microsoft Build of OpenJDK |
| `openjdk` | OpenJDK | |
| `openjdk-leyden` | OpenJDK Leyden | Project Leyden |
| `openjdk-loom` | OpenJDK Loom | Project Loom |
| `openjdk-valhalla` | OpenJDK Valhalla | Project Valhalla |
| `openlogic` | OpenLogic | |
| `oracle` | Oracle | |
| `oracle-graalvm` | Oracle GraalVM | |
| `oracle-graalvm-ea` | Oracle GraalVM | Early Access |
| `redhat` | Red Hat | |
| `sapmachine` | SAP SapMachine | |
| `semeru` | IBM Semeru | |
| `semeru-certified` | IBM Semeru Certified | |
| `temurin` | Eclipse Temurin | Eclipse Adoptium |
| `trava-8` | TravaOpenJDK | Java 8 |
| `trava-11` | TravaOpenJDK | Java 11 |
| `zulu` | Azul Zulu | |
| `zulu-prime` | Azul Zulu Prime | |

**Total: 35 scrapers**

Each scraper is registered via Java's ServiceLoader mechanism in `META-INF/services`.

### Adding New Scrapers

1. Create a new class in `src/main/java/dev/jbang/jdkdb/scraper/impl/` extending `BaseScraper`, `GitHubReleaseScraper`, `AdoptiumMarketplaceScraper`, or `JavaNetBaseScraper`
2. Implement required abstract methods
3. Add an inner `Discovery` class implementing `Scraper.Discovery`
4. Register the discovery class in `src/main/resources/META-INF/services/dev.jbang.jdkdb.scraper.Scraper$Discovery`

Example:

```java
package dev.jbang.jdkdb.scraper.impl;

public class NewVendor extends BaseScraper {
	public NewVendor(ScraperConfig config) {
		super(config);
	}

	@Override
	protected List<JdkMetadata> scrape() throws Exception {
		List<JdkMetadata> allMetadata = new ArrayList<>();

		try {
			// Figure out what items to scrape here...
			var allItems = ...;
			for (item in allItems) {
				try {
					JdkMetadata metadata = scrapeItem(item);
					allMetadata.add(metadata);
				} catch (InterruptedProgressException|TooManyFailuresException e) {
					// We don't want these exceptions to mark the item as failed
					throw e;
				} catch (Exception e) {
					// For recoverable problems we call fail():
					fail(filename, e);
				}
			}
		} catch (InterruptedProgressException ex) {
			// When we receive this exception we stop scraping
			// but we return whatever is already processed
		}

		return allMetadata;
	}

	private JdkMetadata scrapeItem(...) throws Exception {
		// Here we scrape a single item
		JdkMetadata metadata = ...;
		// For each successful item created we call:
		saveMetadataFile(metadata);
		success(filename);
		return metadata;
	}

	// ServiceLoader discovery
	public static class Discovery implements Scraper.Discovery {
		@Override
		public String name() {
			return "new-vendor";
		}

		@Override
		public String vendor() {
			return "New Vendor";
		}

		@Override
		public Scraper create(ScraperConfig config) {
			return new NewVendor(config);
		}
	}
}
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── dev/jbang/jdkdb/
│   │       ├── Main.java                          # CLI application entry point
│   │       ├── UpdateCommand.java                 # Update command (scrape metadata)
│   │       ├── IndexCommand.java                  # Index command (generate all.json)
│   │       ├── DownloadCommand.java               # Download command (compute checksums)
│   │       ├── CleanCommand.java                  # Clean command (cleanup metadata)
│   │       ├── model/
│   │       │   └── JdkMetadata.java              # Data model for JDK metadata
│   │       ├── reporting/
│   │       │   ├── ProgressEvent.java            # Progress event types
│   │       │   ├── ProgressReporter.java         # Central reporting thread
│   │       │   └── ProgressReporterLogger.java   # Logger adapter for scrapers
│   │       ├── scraper/
│   │       │   ├── Scraper.java                  # Scraper interface with Discovery SPI
│   │       │   ├── ScraperConfig.java            # Configuration record
│   │       │   ├── ScraperResult.java            # Result wrapper
│   │       │   ├── BaseScraper.java              # Base class for all scrapers
│   │       │   ├── GitHubReleaseScraper.java     # Base for GitHub-based scrapers
│   │       │   ├── AdoptiumMarketplaceScraper.java # Base for Adoptium Marketplace
│   │       │   ├── JavaNetBaseScraper.java       # Base for java.net archives
│   │       │   ├── ScraperFactory.java           # Factory using ServiceLoader
│   │       │   ├── DownloadManager.java          # Interface for downloading JDKs
│   │       │   ├── DefaultDownloadManager.java   # Default implementation
│   │       │   ├── NoOpDownloadManager.java      # No-op for testing
│   │       │   ├── DownloadResult.java           # Download result wrapper
│   │       │   ├── PaginatedIterator.java        # GitHub pagination helper
│   │       │   ├── InterruptedProgressException.java # Exception types
│   │       │   ├── TooManyFailuresException.java
│   │       │   └── impl/                         # Vendor scraper implementations
│   │       │       ├── Temurin.java
│   │       │       ├── Zulu.java
│   │       │       ├── ZuluPrime.java
│   │       │       ├── Liberica.java
│   │       │       ├── LibericaNative.java
│   │       │       ├── Microsoft.java
│   │       │       ├── Corretto.java
│   │       │       ├── SapMachine.java
│   │       │       ├── Semeru.java
│   │       │       ├── SemeruBaseScraper.java
│   │       │       ├── SemeruCertified.java
│   │       │       ├── Oracle.java
│   │       │       ├── OracleGraalVm.java
│   │       │       ├── OracleGraalVmEa.java
│   │       │       ├── Mandrel.java
│   │       │       ├── Dragonwell.java
│   │       │       ├── Kona.java
│   │       │       ├── Bisheng.java
│   │       │       ├── Redhat.java
│   │       │       ├── AdoptOpenJdk.java
│   │       │       ├── Ibm.java
│   │       │       ├── JavaSeReference.java
│   │       │       ├── Jetbrains.java
│   │       │       ├── GraalVmLegacy.java
│   │       │       ├── GraalVmCe.java
│   │       │       ├── GraalVmCommunity.java
│   │       │       ├── GraalVmCommunityEa.java
│   │       │       ├── GluonGraalVm.java
│   │       │       ├── OpenLogic.java
│   │       │       ├── OpenJdk.java
│   │       │       ├── OpenJdkLeyden.java
│   │       │       ├── OpenJdkLoom.java
│   │       │       ├── OpenJdkValhalla.java
│   │       │       ├── Debian.java
│   │       │       ├── TravaBaseScraper.java
│   │       │       ├── TravaJdk8.java
│   │       │       └── TravaJdk11.java
│   │       └── util/
│   │           ├── ArchiveUtils.java             # Archive extraction utilities
│   │           ├── FileUtils.java                # File operations
│   │           ├── HashUtils.java                # Hash computation
│   │           ├── HtmlUtils.java                # HTML parsing utilities
│   │           ├── HttpUtils.java                # HTTP operations
│   │           ├── MetadataUtils.java            # Metadata validation/utilities
│   │           ├── VendorLoggerDiscriminator.java # Logging configuration
│   │           └── VersionComparator.java        # Version comparison
│   └── resources/
│       ├── logback.xml                           # Logging configuration
│       └── META-INF/
│           └── services/
│               └── dev.jbang.jdkdb.scraper.Scraper$Discovery
└── test/
	└── java/
		└── dev/jbang/jdkdb/
			├── scraper/
			│   ├── BaseScraperTest.java
			│   ├── DummyScraperTest.java
			│   ├── ScraperFactoryTest.java
			│   └── ScraperResultTest.java
			└── util/
				├── ArchiveUtilsTest.java
				└── MetadataUtilsTest.java
```

## Dependencies

- **Jackson**: JSON processing (2.16.1)
- **Java HttpClient** (`java.net.http`): HTTP operations (built-in)
- **SLF4J/Logback**: Logging (SLF4J 2.0.7, Logback 1.4.14)
- **Picocli**: Command-line interface (4.7.5)
- **Apache Commons Compress**: Archive handling (1.27.1)
- **JUnit 5**: Testing (5.10.1)
- **AssertJ**: Fluent assertions for testing (3.27.6)

## Output

The scrapers generate structured output in the `docs/` directory:

### Metadata Files (`docs/metadata/`)

1. **Top-level aggregated indexes**:
   - `all.json` - All JDK releases across all vendors
   - `ga.json` - General Availability (stable) releases only
   - `ea.json` - Early Access releases only
   - `latest.json` - Latest releases per vendor

2. **Organized by release type** (`all/`, `ea/`, `ga/`):
   - OS-specific files: `linux.json`, `macosx.json`, `windows.json`, `aix.json`, `solaris.json`
   - Architecture-specific subdirectories with further breakdowns

3. **Vendor-specific metadata** (`vendor/<vendor-name>/`):
   - Individual `.json` files for each JDK release
   - `all.json` file combining all releases for that vendor

### Checksum Files (`docs/checksums/`)

- Stored in vendor-specific directories: `docs/checksums/<vendor-name>/`
- Contains MD5, SHA1, SHA256, and SHA512 checksum files
- Organized to match the corresponding metadata files

## Logging

Logs are written to:
- Console (STDOUT) - Real-time progress
- File (`logs/jdkdb-scraper.log`) - Detailed execution log

The logging configuration can be customized in `src/main/resources/logback.xml`.

## Requirements

- Java 21 or higher
- Gradle 8.x (included via wrapper)

## License

Same as the original project (see LICENSE file)

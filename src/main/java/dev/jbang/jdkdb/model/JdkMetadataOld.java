package dev.jbang.jdkdb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.jbang.jdkdb.model.JdkMetadata.ReleaseType;
import dev.jbang.jdkdb.scraper.DownloadResult;
import dev.jbang.jdkdb.util.MetadataUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents JDK metadata for a specific release */
@JsonPropertyOrder({
	"vendor",
	"filename",
	"release_type",
	"version",
	"java_version",
	"jvm_impl",
	"os",
	"architecture",
	"file_type",
	"image_type",
	"features",
	"url",
	"md5",
	"md5_file",
	"sha1",
	"sha1_file",
	"sha256",
	"sha256_file",
	"sha512",
	"sha512_file",
	"size",
	"release_info"
})
public class JdkMetadataOld {
	private static final Logger logger = LoggerFactory.getLogger(JdkMetadataOld.class);

	private String vendor;

	private String filename;

	@JsonProperty("release_type")
	private String releaseType;

	private String version;

	@JsonProperty("java_version")
	private String javaVersion;

	@JsonProperty("jvm_impl")
	private String jvmImpl;

	private String os;

	private String architecture;

	@JsonProperty("file_type")
	private String fileType;

	@JsonProperty("image_type")
	private String imageType;

	private List<String> features;

	private String url;

	private String md5;

	@JsonProperty("md5_file")
	private String md5File;

	private String sha1;

	@JsonProperty("sha1_file")
	private String sha1File;

	private String sha256;

	@JsonProperty("sha256_file")
	private String sha256File;

	private String sha512;

	@JsonProperty("sha512_file")
	private String sha512File;

	private long size;

	@JsonProperty("release_info")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> releaseInfo;

	@JsonIgnore
	private transient Path metadataFile;

	// Constructors
	public JdkMetadataOld() {
		releaseType = "ga";
		jvmImpl = "hotspot";
		imageType = "jdk";
		features = new ArrayList<>();
	}

	// Getters and Setters
	public String getVendor() {
		return vendor;
	}

	public JdkMetadataOld setVendor(String vendor) {
		this.vendor = vendor;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public JdkMetadataOld setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public String getReleaseType() {
		return releaseType;
	}

	public ReleaseType releaseTypeEnum() {
		return ReleaseType.valueOf(releaseType);
	}

	public JdkMetadataOld setReleaseType(String releaseType) {
		this.releaseType = releaseType;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public JdkMetadataOld setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public JdkMetadataOld setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
		return this;
	}

	public String getJvmImpl() {
		return jvmImpl;
	}

	public JdkMetadata.JvmImpl jvmImplEnum() {
		return JdkMetadata.JvmImpl.valueOf(jvmImpl);
	}

	public JdkMetadataOld setJvmImpl(String jvmImpl) {
		this.jvmImpl = jvmImpl;
		return this;
	}

	public String getOs() {
		return os;
	}

	public JdkMetadata.Os osEnum() {
		return JdkMetadata.Os.valueOf(os);
	}

	public JdkMetadataOld setOs(String os) {
		this.os = os;
		return this;
	}

	public String getArchitecture() {
		return architecture;
	}

	public JdkMetadata.Arch archEnum() {
		return JdkMetadata.Arch.valueOf(architecture.replace("-", "_"));
	}

	public JdkMetadataOld setArchitecture(String architecture) {
		this.architecture = architecture;
		return this;
	}

	public String getFileType() {
		return fileType;
	}

	public JdkMetadata.FileType fileTypeEnum() {
		return JdkMetadata.FileType.valueOf(fileType.replace(".", "_"));
	}

	public JdkMetadataOld setFileType(String fileType) {
		this.fileType = fileType;
		return this;
	}

	public String getImageType() {
		return imageType;
	}

	public JdkMetadata.ImageType imageTypeEnum() {
		return JdkMetadata.ImageType.valueOf(imageType);
	}

	public JdkMetadataOld setImageType(String imageType) {
		this.imageType = imageType;
		return this;
	}

	public List<String> getFeatures() {
		return features;
	}

	public JdkMetadataOld setFeatures(List<String> features) {
		this.features = features;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public JdkMetadataOld setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getMd5() {
		return md5;
	}

	public String getMd5File() {
		return md5File;
	}

	public String getSha1() {
		return sha1;
	}

	public String getSha1File() {
		return sha1File;
	}

	public String getSha256() {
		return sha256;
	}

	public String getSha256File() {
		return sha256File;
	}

	public String getSha512() {
		return sha512;
	}

	public String getSha512File() {
		return sha512File;
	}

	public long getSize() {
		return size;
	}

	public Map<String, String> getReleaseInfo() {
		return releaseInfo;
	}

	public JdkMetadataOld setReleaseInfo(Map<String, String> releaseInfo) {
		this.releaseInfo = releaseInfo;
		return this;
	}

	public Path metadataFile() {
		if (metadataFile == null && filename != null) {
			return Path.of(filename + ".json");
		}
		return metadataFile;
	}

	public JdkMetadataOld metadataFile(Path metadataFile) {
		this.metadataFile = metadataFile;
		return this;
	}

	public JdkMetadataOld download(DownloadResult download) {
		this.md5 = download.md5();
		this.md5File = filename + ".md5";
		this.sha1 = download.sha1();
		this.sha1File = filename + ".sha1";
		this.sha256 = download.sha256();
		this.sha256File = filename + ".sha256";
		this.sha512 = download.sha512();
		this.sha512File = filename + ".sha512";
		this.size = download.size();
		return this;
	}

	/**
	 * Validate metadata fields and return true if valid, false if invalid.
	 * This checks that required fields are present and that enum values
	 * are valid (or properly marked as unknown).
	 */
	@JsonIgnore
	public boolean isValid() {
		if (getFilename() == null) {
			logger.warn("Missing 'filename'");
			return false;
		}
		if (getUrl() == null) {
			logger.warn("Missing 'url'");
			return false;
		}
		if (getVersion() == null
				|| getVersion().trim().isEmpty()
				|| !getVersion().matches("^\\d.*")) {
			logger.warn("Invalid 'version': {}", getVersion());
			return false;
		}
		if (getJavaVersion() == null
				|| getJavaVersion().trim().isEmpty()
				|| !getJavaVersion().matches("^\\d.*")) {
			logger.warn("Invalid 'java_version': {}", getJavaVersion());
			return false;
		}
		if (!MetadataUtils.isValidEnumOrUnknown(JdkMetadata.Os.class, getOs())) {
			logger.warn("Invalid 'os': {}", getOs());
			return false;
		}
		if (!MetadataUtils.isValidEnum(JdkMetadata.ImageType.class, getImageType())) {
			logger.warn("Invalid 'image_type': {}", getImageType());
			return false;
		}
		if (!MetadataUtils.isValidEnum(JdkMetadata.JvmImpl.class, getJvmImpl())) {
			logger.warn("Invalid 'jvm_impl': {}", getJvmImpl());
			return false;
		}
		if (!MetadataUtils.isValidEnum(JdkMetadata.ReleaseType.class, getReleaseType())) {
			logger.warn("Invalid 'release_type': {}", getReleaseType());
			return false;
		}
		if (!MetadataUtils.isValidEnumOrUnknown(
				JdkMetadata.Arch.class, getArchitecture().replace("-", "_"))) {
			logger.warn("Invalid 'architecture': {}", getArchitecture());
			return false;
		}
		if (!MetadataUtils.isValidEnumOrUnknown(
				JdkMetadata.FileType.class, getFileType().replace(".", "_"))) {
			logger.warn("Invalid 'file_type': {}", getFileType());
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JdkMetadataOld that = (JdkMetadataOld) o;
		return Objects.equals(vendor, that.vendor)
				&& Objects.equals(filename, that.filename)
				&& Objects.equals(version, that.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vendor, filename, version);
	}

	public static JdkMetadataOld create() {
		return new JdkMetadataOld();
	}
}

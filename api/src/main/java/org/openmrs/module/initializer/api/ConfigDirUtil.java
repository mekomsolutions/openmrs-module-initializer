package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.initializer.InitializerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps read and write to and from the configuration and checksum directories.
 */
public class ConfigDirUtil {
	
	protected static final String NOT_COMPUTABLE_CHECKSUM = "not_computable_checksum";
	
	protected static final String NOT_READABLE_CHECKSUM = "not_readadble_checksum";
	
	protected static final String CHECKSUM_FILE_EXT = "checksum";
	
	protected static final Logger log = LoggerFactory.getLogger(ConfigDirUtil.class);
	
	protected boolean skipChecksums = true;
	
	/*
	 * The domain name, so the final part of configuration domain subdirectory. Eg.
	 * "locations" in "../configuration/locations"
	 */
	protected String domain = "";
	
	/*
	 * The absolute path to the configuration domain subdirectory. Eg.
	 * "../configuration/locations"
	 */
	protected String domainDirPath = "";
	
	/*
	 * The absolute path to the configuration domain checksum subdirectory. Eg.
	 * "../configuration_checksums/locations"
	 */
	protected String checksumDirPath = "";
	
	/*
	 * The absolute path to the configuration domain rejections subdirectory. Eg.
	 * "../configuration_rejections/concepts"
	 */
	protected String rejectionsDirPath = "";
	
	/**
	 * @param configDirPath The absolute path to the config directory, eg. "../configuration"
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param domain The metadata domain, eg. "locations"
	 */
	public ConfigDirUtil(String configDirPath, String checksumDirPath, String rejectionsDirPath, String domain,
	    InitializerConfig cfg) {
		this.domain = domain;
		this.domainDirPath = Paths.get(configDirPath, domain).toString();
		this.checksumDirPath = Paths.get(checksumDirPath, domain).toString();
		this.rejectionsDirPath = Paths.get(rejectionsDirPath, domain).toString();
		this.skipChecksums = cfg.skipChecksums();
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getDomainDirPath() {
		return domainDirPath;
	}
	
	public String getChecksumDirPath() {
		return checksumDirPath;
	}
	
	@Override
	public String toString() {
		return domainDirPath;
	}
	
	/**
	 * Convenience method to get a FilenameFilter for files of a given extension.
	 * 
	 * @param The dot-less extension to filter for, eg. "xml", "json".
	 * @return The FilenameFilter.
	 */
	public static FilenameFilter getExtensionFilenameFilter(final String extension) {
		FilenameFilter filter = (file, name) -> StringUtils.isNotEmpty(extension)
		        && new SuffixFileFilter("." + extension).accept(file, name) && FileFileFilter.FILE.accept(file, name);
		return filter;
	}
	
	/**
	 * Extracts the name of a file based on the to the domain directory path.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param filePath The absolute path to a file inside the config. directory structure, eg.
	 *            "../configuration/locations/config.xml".
	 * @return The file name, eg. "config.xml".
	 */
	protected static String getFileName(String domainDirPath, String filePath) {
		return filePath.replace(new StringBuilder().append(domainDirPath).append(File.separator).toString(), "");
	}
	
	/**
	 * @see #getFileName(String, String)
	 */
	public String getFileName(String filePath) {
		return getFileName(domainDirPath, filePath);
	}
	
	/**
	 * Returns the checksum of a config file if the file has been updated since the last checksum was
	 * saved.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param configFileName The config file name, eg. "config.xml"
	 * @return An empty string if the checksum hasn't changed, the new checksum otherwise.
	 */
	protected static String getChecksumIfChanged(String domainDirPath, String checksumDirPath, String configFileName) {
		String savedChecksum = readLatestChecksum(checksumDirPath, configFileName);
		String checksum = computeChecksum(domainDirPath, configFileName);
		return savedChecksum.equals(checksum) ? "" : checksum;
	}
	
	/**
	 * @see #getChecksumIfChanged(String, String, String)
	 */
	public String getChecksumIfChanged(String configFileName) {
		return getChecksumIfChanged(domainDirPath, checksumDirPath, configFileName);
	}
	
	/**
	 * Fetches all the files in a directory based on their extension and that do not match the exclusion
	 * patterns.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param extension The dot-less extension to filter for, eg. "xml", "json", ... etc.
	 * @param wildcardExclusions A list of wildcard exclusion patterns, eg. "*test*.java~*~".
	 * @return The list of {@link File} instances.
	 */
	protected static List<File> getFiles(String domainDirPath, String extension, List<String> wildcardExclusions) {
		
		final List<File> allFiles = new ArrayList<File>();
		
		FilenameFilter exclusionFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				boolean matched = false;
				for (String pattern : Optional.ofNullable(wildcardExclusions).orElse(Collections.emptyList())) {
					matched = matched || new WildcardFileFilter(pattern).accept(dir, name);
				}
				return !matched; // a file is accepted when the (exclusion) patterns are *not* matched 
			}
		};
		
		FilenameFilter filter = (file, name) -> getExtensionFilenameFilter(extension).accept(file, name)
		        && exclusionFilter.accept(file, name);
		
		final File[] files = new File(domainDirPath).listFiles(filter);
		if (files != null) {
			allFiles.addAll(Arrays.asList(files));
		}
		
		return allFiles;
	}
	
	/**
	 * @see #getFiles(String, String)
	 */
	public List<File> getFiles(String extension, List<String> wildcardExclusions) {
		return getFiles(domainDirPath, extension, wildcardExclusions);
	}
	
	/**
	 * @see #getFiles(String, String)
	 */
	public List<File> getFiles(String extension) {
		return getFiles(domainDirPath, extension, Collections.emptyList());
	}
	
	/**
	 * Fetches the config. file from its relative path inside the configuration folder.
	 * 
	 * @param dirPath The absolute path to the containing directory, eg. "../configuration/locations" or
	 *            "../configuration_checksums/locations"
	 * @param fileName The file name, eg. "locations.csv" or "locations.checksum"
	 * @return The {@link File} instance.
	 */
	protected static File getFile(String dirPath, String fileName) {
		StringBuilder path = new StringBuilder(dirPath);
		path.append(File.separator).append(fileName);
		return new File(path.toString());
	}
	
	/**
	 * @see #getFile(String, String)
	 */
	public File getConfigFile(String fileName) {
		return getFile(domainDirPath, fileName);
	}
	
	/**
	 * Returns the checksum file name inside the domain folder.
	 * 
	 * @param configFileName The config file name, eg. "locations.csv"
	 * @return The checksum file name, eg. "locations.checksum"
	 */
	public static String toChecksumFileName(String configFileName) {
		// addressConfiguration.xml -> addressConfiguration.checksum
		return FilenameUtils.getBaseName(configFileName) + "." + CHECKSUM_FILE_EXT;
	}
	
	/**
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param configFileName The config file name, eg. "locations.csv"
	 * @return The checksum of the config file that was last successfully loaded.
	 */
	protected static String readLatestChecksum(String checksumDirPath, String configFileName) {
		
		String checksum = NOT_READABLE_CHECKSUM;
		
		if (!new File(checksumDirPath).exists()) {
			return checksum;
		}
		
		final String checksumFileName = toChecksumFileName(configFileName);
		try {
			final File checksumFile = getFile(checksumDirPath, checksumFileName);
			if (checksumFile.exists()) {
				checksum = FileUtils.readFileToString(checksumFile, "UTF-8");
			}
		}
		catch (Exception e) {
			log.warn("Error reading latest checksum of entry file from " + checksumFileName, e);
		}
		return checksum;
	}
	
	/**
	 * @see #readLatestChecksum(String, String)
	 */
	public String readLatestChecksum(String configFileName) {
		return readLatestChecksum(checksumDirPath, configFileName);
	}
	
	/**
	 * Compute the checksum of a configuration file.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param configFileName The config file name, eg. "locations.csv"
	 * @return The checksum of the file.
	 */
	protected static String computeChecksum(String domainDirPath, String configFileName) {
		
		String checksum = NOT_COMPUTABLE_CHECKSUM;
		
		File configFile = getFile(domainDirPath, configFileName);
		if (configFile.exists()) {
			try {
				// checksum = Long.toHexString( FileUtils.checksumCRC32(file) );
				FileInputStream fis = new FileInputStream(configFile);
				checksum = DigestUtils.md5Hex(fis);
				fis.close();
			}
			catch (Exception e) {
				log.warn("Error computing checksum of config. file: " + configFileName, e);
			}
		}
		return checksum;
	}
	
	/**
	 * @see #computeChecksum(String, String)
	 */
	public String computeChecksum(String configFileName) {
		return computeChecksum(domainDirPath, configFileName);
	}
	
	/**
	 * Writes the the checksum of a config file into the corresponding .checksum file.
	 * 
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param checksumFileName The checksum file name
	 * @param checksum The checksum hash of the config file.
	 */
	protected static void writeChecksum(String checksumDirPath, String checksumFileName, String checksum) {
		
		deleteChecksum(checksumDirPath, checksumFileName);
		
		if (NOT_COMPUTABLE_CHECKSUM.equals(checksum)) {
			return;
		}
		
		try {
			FileUtils.writeStringToFile(getFile(checksumDirPath, checksumFileName), checksum, "UTF-8");
		}
		catch (Exception e) {
			log.error("Error writing hash ('" + checksum + "') of configuration file to: " + checksumFileName, e);
		}
	}
	
	/**
	 * @see #writeChecksum(String, String, String)
	 */
	public void writeChecksum(String configFileName, String checksum) {
		if (!skipChecksums) {
			writeChecksum(checksumDirPath, toChecksumFileName(configFileName), checksum);
		}
	}
	
	/**
	 * Deletes the checksum file of a config. file.
	 * 
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param checksumFileName The checksum file name
	 */
	protected static void deleteChecksum(String checksumDirPath, String checksumFileName) {
		
		try {
			Files.deleteIfExists(getFile(checksumDirPath, checksumFileName).toPath());
		}
		catch (IOException e) {
			log.warn("Error deleting hash of configuration file: " + checksumFileName, e);
		}
	}
	
	/**
	 * @see #deleteChecksum(String, String)
	 */
	public void deleteChecksum(String checksumFileName) {
		deleteChecksum(checksumDirPath, checksumFileName);
	}
	
	/**
	 * Removes all the checksum files inside the provided directory.
	 * 
	 * @param checksumDirPath The absolute path to a config directory, eg. "../configuration/locations"
	 *            or "../configuration"
	 * @param recursive Set to true to continue recursively into subdirectories.
	 */
	public static void deleteChecksums(String checksumDirPath, boolean recursive) {
		
		deleteChecksums(checksumDirPath);
		
		if (recursive) {
			final String[] dirNames = new File(checksumDirPath).list(DirectoryFileFilter.INSTANCE);
			if (dirNames != null) {
				for (String dirName : dirNames) {
					deleteChecksums(new StringBuilder(checksumDirPath).append(File.separator).append(dirName).toString(),
					    true);
				}
			}
		}
	}
	
	/**
	 * @see #deleteChecksums(String, boolean)
	 */
	public void deleteChecksums(boolean recursive) {
		deleteChecksums(checksumDirPath, recursive);
	}
	
	/**
	 * @see #deleteChecksums(boolean)
	 */
	public void deleteChecksums() {
		deleteChecksums(checksumDirPath);
	}
	
	/**
	 * Removes all the checksum files inside the provided directory.
	 * 
	 * @param checksumDirPath The absolute path to a config directory, eg. "../configuration/locations"
	 *            or "../configuration"
	 */
	public static void deleteChecksums(String checksumDirPath) {
		
		final File[] checksumFiles = new File(checksumDirPath).listFiles(getExtensionFilenameFilter(CHECKSUM_FILE_EXT));
		
		if (checksumFiles != null) {
			for (File file : checksumFiles) {
				file.delete();
			}
		}
	}
}

package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
	
	/**
	 * @param configDirPath The absolute path to the configuration directory, eg. "../configuration"
	 * @param checksumsDirPath The absolute path to the checksums directory, eg.
	 *            "../configuration_checksums"
	 * @param domain The metadata domain, eg. "locations"
	 */
	public ConfigDirUtil(String configDirPath, String checksumsDirPath, String domain, InitializerConfig cfg) {
		this.domain = domain;
		this.domainDirPath = Paths.get(configDirPath, domain).toString();
		this.checksumDirPath = Paths.get(checksumsDirPath, domain).toString();
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
	 * The located file name is a name that encodes the location of the file relative to the
	 * configuration directory. Eg. "../configuration/domain/level1/level2/config.xml" has a located
	 * file name "level1_level2_config". Located file names are used to generate checksum file names so
	 * that they can all live at the same level in the checksums folder.
	 * 
	 * @param domainDirPath The absolute path to the domain, eg. "../configuration/domain"
	 * @param file The configuration file nested somewhere in the domain directory.
	 * @return The located file name.
	 */
	public static String getLocatedFilename(String domainDirPath, File file) {
		return FilenameUtils.removeExtension(
		    file.getAbsolutePath().replace(new StringBuilder().append(domainDirPath).append(File.separator).toString(), "")
		            .replace(File.separator, "_"));
	}
	
	/**
	 * Returns the checksum of a configuration file if the file has been changed since the last time a
	 * checksum was saved.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param configFile The config file.
	 * @return An empty string if the checksum hasn't changed, the new checksum otherwise.
	 */
	protected static String getChecksumIfChanged(String domainDirPath, String checksumDirPath, File configFile) {
		final String checksumFilename = getLocatedFilename(domainDirPath, configFile) + "." + CHECKSUM_FILE_EXT;
		String checksum = computeChecksum(configFile);
		return readChecksum(checksumDirPath, checksumFilename).equals(checksum) ? "" : checksum;
	}
	
	/**
	 * @see #getChecksumIfChanged(String, String, File)
	 */
	public String getChecksumIfChanged(File configFile) {
		return getChecksumIfChanged(domainDirPath, checksumDirPath, configFile);
	}
	
	/**
	 * Fetches recursively all the files in a domain directory (or subdirectory) based on their
	 * extension and that do not match the exclusion patterns.
	 * 
	 * @param domainDirPath The absolute path to the domain directory or subdirectory, eg.
	 *            "../configuration/locations" or "../configuration/locations/level1".
	 * @param extension The dot-less extension to filter for, eg. "xml", "json", ... etc.
	 * @param wildcardExclusions A list of wildcard exclusion patterns, eg. "*test*.java~*~".
	 * @return The list of {@link File} instances found recursively inside the folder.
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
		
		Stream.of(Optional.ofNullable(new File(domainDirPath).list(DirectoryFileFilter.INSTANCE)).orElse(new String[0]))
		        .forEach(dirName -> {
			        allFiles.addAll(getFiles(Paths.get(domainDirPath, dirName).toString(), extension, wildcardExclusions));
		        });
		
		return allFiles;
	}
	
	/**
	 * @see #getFiles(String, String, List<String>)
	 */
	public List<File> getFiles(String extension, List<String> wildcardExclusions) {
		return getFiles(domainDirPath, extension, wildcardExclusions);
	}
	
	/**
	 * @see #getFiles(String, String, List<String>)
	 */
	public List<File> getFiles(String extension) {
		return getFiles(domainDirPath, extension, Collections.emptyList());
	}
	
	/**
	 * @param checksumsDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param checksumFilename The checksum file name.
	 * @return The checksum value of the config file that was last successfully loaded.
	 */
	protected static String readChecksum(String checksumsDirPath, String checksumFilename) {
		
		String checksum = NOT_READABLE_CHECKSUM;
		
		if (!new File(checksumsDirPath).exists()) {
			return checksum;
		}
		
		final String checksumFilePath = Paths.get(checksumsDirPath, checksumFilename).toString();
		try {
			final File checksumFile = new File(checksumFilePath);
			if (checksumFile.exists()) {
				checksum = FileUtils.readFileToString(checksumFile, "UTF-8");
			}
		}
		catch (Exception e) {
			log.warn("Error reading checksum file: " + checksumFilePath, e);
		}
		return checksum;
	}
	
	/**
	 * Reads the saved checksum for the given configuration file.
	 * 
	 * @param configFile The configuration file.
	 * @return The saved checksum hash value.
	 */
	public String readSavedChecksum(File configFile) {
		final String checksumFilename = getLocatedFilename(domainDirPath, configFile) + "." + CHECKSUM_FILE_EXT;
		return readChecksum(checksumDirPath, checksumFilename);
	}
	
	/**
	 * Compute the checksum of a configuration file.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg. "../configuration/locations"
	 * @param configFileName The config file name, eg. "locations.csv"
	 * @return The checksum of the file.
	 */
	protected static String computeChecksum(File configFile) {
		
		String checksum = NOT_COMPUTABLE_CHECKSUM;
		
		if (configFile.exists()) {
			try {
				// checksum = Long.toHexString( FileUtils.checksumCRC32(file) );
				FileInputStream fis = new FileInputStream(configFile);
				checksum = DigestUtils.md5Hex(fis);
				fis.close();
			}
			catch (Exception e) {
				log.warn("Error computing checksum of config. file: " + configFile.getName(), e);
			}
		}
		return checksum;
	}
	
	/**
	 * Writes the the checksum of a configuration file into the corresponding checksum file.
	 * 
	 * @param checksumsDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param checksumFilename The checksum file name
	 * @param checksum The checksum hash of the configuration file.
	 */
	protected static void writeChecksum(String checksumsDirPath, String checksumFilename, String checksum) {
		
		deleteChecksum(checksumsDirPath, checksumFilename);
		
		if (NOT_COMPUTABLE_CHECKSUM.equals(checksum)) {
			return;
		}
		
		final String checksumFilePath = Paths.get(checksumsDirPath, checksumFilename).toString();
		try {
			FileUtils.writeStringToFile(new File(checksumFilePath), checksum, "UTF-8");
		}
		catch (Exception e) {
			log.error("Error writing hash ('" + checksum + "') of configuration file at: " + checksumFilePath, e);
		}
	}
	
	/**
	 * @see #writeChecksum(String, String, String)
	 */
	public void writeChecksum(File configFile, String checksum) {
		final String checksumFilename = getLocatedFilename(domainDirPath, configFile) + "." + CHECKSUM_FILE_EXT;
		if (!skipChecksums) {
			writeChecksum(checksumDirPath, checksumFilename, checksum);
		}
	}
	
	/**
	 * Deletes the checksum file of a configuration file.
	 * 
	 * @param checksumsDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param checksumFilename The checksum file name
	 */
	protected static void deleteChecksum(String checksumsDirPath, String checksumFilename) {
		
		final Path checksumFilePath = Paths.get(checksumsDirPath, checksumFilename);
		try {
			Files.deleteIfExists(checksumFilePath);
		}
		catch (IOException e) {
			log.warn("Error deleting hash of configuration file at: " + checksumFilePath.toString(), e);
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

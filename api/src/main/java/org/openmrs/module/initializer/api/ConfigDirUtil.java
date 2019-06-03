package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.openmrs.module.initializer.InitializerLogFactory;

/**
 * Helps read and write to and from the configuration and checksum directories.
 */
public class ConfigDirUtil {
	
	protected static final String NOT_COMPUTABLE_CHECKSUM = "not_computable_checksum";
	
	protected static final String NOT_READABLE_CHECKSUM = "not_readadble_checksum";
	
	protected static final String CHECKSUM_FILE_EXT = "checksum";
	
	protected static Log log = InitializerLogFactory.getLog(ConfigDirUtil.class);
	
	/*
	 * The domain name, so the final part of configuration domain subdirectory. Eg.
	 * "addresshierarchy" in "../configuration/addresshierarchy"
	 */
	protected String domain = "";
	
	/*
	 * The absolute path to the configuration domain subdirectory. Eg.
	 * "../configuration/addresshierarchy"
	 */
	protected String domainDirPath = "";
	
	/*
	 * The absolute path to the configuration domain checksum subdirectory. Eg.
	 * "../configuration_checksums/addresshierarchy"
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
	 * @param domain The metadata domain, eg. "addresshierarchy"
	 */
	public ConfigDirUtil(String configDirPath, String checksumDirPath, String rejectionsDirPath, String domain) {
		this.domain = domain;
		this.domainDirPath = Paths.get(configDirPath, domain).toString();
		this.checksumDirPath = Paths.get(checksumDirPath, domain).toString();
		this.rejectionsDirPath = Paths.get(rejectionsDirPath, domain).toString();
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
	
	/*
	 * To filter files of a certain extension only.
	 * 
	 * @param extension The file extension to filter for.
	 */
	protected static FilenameFilter getExtensionFilenameFilter(final String extension) {
		return new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String ext = FilenameUtils.getExtension(name);
				if (StringUtils.isEmpty(ext)) { // to be safe, ext can only be null if name is null
					return false;
				}
				if (new File(dir, name).isDirectory()) {
					return false;
				}
				if (ext.equals(extension)) {
					return true; // filtering only checksum files based on their extension
				}
				return false;
			}
		};
	}
	
	/*
	 * To filter directories only.
	 */
	protected static FilenameFilter getDirectoryFilenameFilter() {
		return new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (new File(dir, name).isDirectory()) {
					return true;
				}
				return false;
			}
		};
	}
	
	/**
	 * Extracts the name of a file based on the to the domain directory path.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param filePath The absolute path to a file inside the config. directory structure, eg.
	 *            "../configuration/addresshierarchy/config.xml".
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
	 * @param domainDirPath The absolute path to the domain directory, eg.
	 *            "../configuration/addresshierarchy"
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
	 * @see #getChecksumIfChanged(String, String)
	 */
	public String getChecksumIfChanged(String configFileName) {
		return getChecksumIfChanged(domainDirPath, checksumDirPath, configFileName);
	}
	
	/**
	 * Fetches all the files in a directory based on their extension.
	 * 
	 * @param domainDirPath The absolute path to the domain directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param extension The extension to filter for, eg "xml".
	 * @return The list of {@link File} instances.
	 */
	protected static List<File> getFiles(String domainDirPath, String extension) {
		
		final List<File> allFiles = new ArrayList<File>();
		
		final File[] files = new File(domainDirPath).listFiles(getExtensionFilenameFilter(extension));
		if (files != null) {
			allFiles.addAll(Arrays.asList(files));
		}
		
		return allFiles;
	}
	
	/**
	 * @see #getFiles(String, String)
	 */
	public List<File> getFiles(String extension) {
		return getFiles(domainDirPath, extension);
	}
	
	/**
	 * Fetches the config. file from its relative path inside the configuration folder.
	 * 
	 * @param dirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy" or "../configuration_checksums/addresshierarchy"
	 * @param fileName The file name, eg. "config.xml" or "config.checksum"
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
	 * @param configFileName The config file name, eg. "config.xml"
	 * @return The checksum file name, eg. "config.checksum"
	 */
	public static String toChecksumFileName(String configFileName) {
		// addressConfiguration.xml -> addressConfiguration.checksum
		return FilenameUtils.getBaseName(configFileName) + "." + CHECKSUM_FILE_EXT;
	}
	
	/**
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param configFileName The config file name, eg. "config.xml"
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
	 * @param domainDirPath The absolute path to the domain directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFileName The config file name, eg. "config.xml"
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
	 * @param configFileName The config file name, eg. "config.xml"
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
		writeChecksum(checksumDirPath, ConfigDirUtil.toChecksumFileName(configFileName), checksum);
	}
	
	/**
	 * Deletes the checksum file of a config. file.
	 * 
	 * @param checksumDirPath The absolute path to the checksum directory, eg.
	 *            "../configuration_checksums"
	 * @param configFileName The config file name, eg. "config.xml"
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
	 * @param checksumDirPath The absolute path to a config directory, eg.
	 *            "../configuration/addresshierarchy" or "../configuration"
	 * @param recursive Set to true to continue recursively into subdirectories.
	 */
	public static void deleteChecksums(String checksumDirPath, boolean recursive) {
		
		deleteChecksums(checksumDirPath);
		
		if (recursive) {
			final String[] dirNames = new File(checksumDirPath).list(getDirectoryFilenameFilter());
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
	 * @param checksumDirPath The absolute path to a config directory, eg.
	 *            "../configuration/addresshierarchy" or "../configuration"
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

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
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigLoaderUtil {
	
	protected static final String NOT_COMPUTABLE_CHECKSUM = "not_computable_checksum";
	
	protected static final String NOT_READABLE_CHECKSUM = "not_readadble_checksum";
	
	protected static final String CHECKSUM_FILE_EXT = "checksum";
	
	protected static Log log = LogFactory.getLog(ConfigLoaderUtil.class);
	
	protected String configDirPath = "";
	
	/**
	 * Non-static instances are based on a configuration subdirectory path.
	 * 
	 * @param configDirPath The absolute path to the subdirectory, eg.
	 *            "../configuration/addresshierarchy"
	 */
	public ConfigLoaderUtil(String configDirPath) {
		if (!StringUtils.isEmpty(configDirPath)) {
			this.configDirPath = configDirPath;
		}
	}
	
	/*
	 * To filter files of a certain extension only.
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
	 * Extracts the relative path of a file to the config. directory.
	 * 
	 * @param configDirPath The absolute path to the config. directory, eg.
	 *            "../configuration/addresshierarchy".
	 * @param filePath The absolute path to a file inside the config. directory structure, eg.
	 *            "../configuration/addresshierarchy/subdir/config.xml".
	 * @return The relative path, eg. "subdir/config.xml".
	 */
	public String getRelativePath(String configDirPath, String filePath) {
		return filePath.replace(new StringBuilder().append(configDirPath).append(File.separator).toString(), "");
	}
	
	/**
	 * @see #getRelativePath(String, String)
	 */
	public String getRelativePath(String filePath) {
		return getRelativePath(configDirPath, filePath);
	}
	
	/**
	 * Returns the checksum of a config. file if the file has been updated since the last checksum
	 * was saved.
	 * 
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @return An empty string if the checksum hasn't changed, the new checksum otherwise.
	 */
	public static String getChecksumIfChanged(String configDirPath, String configFilePath) {
		String savedChecksum = readLatestChecksum(configDirPath, configFilePath);
		String checksum = computeChecksum(configDirPath, configFilePath);
		return savedChecksum.equals(checksum) ? "" : checksum;
	}
	
	/**
	 * @see #getChecksumIfChanged(String, String)
	 */
	public String getChecksumIfChanged(String configFilePath) {
		return getChecksumIfChanged(configDirPath, configFilePath);
	}
	
	/**
	 * Fetches all the files in a directory based on their extension.
	 * 
	 * @param configDirPath The absolute path to the config. directory, eg.
	 *            "../configuration/addresshierarchy".
	 * @param extension The extension to filter for, eg "xml".
	 * @param recursive Set to true to continue recursively into subdirectories.
	 * @return The list of {@link File} instances.
	 */
	public static List<File> getFiles(String configDirPath, String extension, boolean recursive) {
		
		final List<File> allFiles = new ArrayList<File>();
		
		final File[] files = new File(configDirPath).listFiles(getExtensionFilenameFilter(extension));
		if (files != null) {
			allFiles.addAll(Arrays.asList(files));
		}
		
		if (recursive) {
			final File[] dirs = new File(configDirPath).listFiles(getDirectoryFilenameFilter());
			if (dirs != null) {
				for (File dir : dirs) {
					allFiles.addAll(getFiles(dir.getPath(), extension, true));
				}
			}
		}
		
		return allFiles;
	}
	
	/**
	 * @see #getFiles(String, String, boolean)
	 */
	public static List<File> getFiles(String configDirPath, String extension) {
		return getFiles(configDirPath, extension, false);
	}
	
	/**
	 * @see #getFiles(String, String, boolean)
	 */
	public List<File> getFiles(String extension, boolean recursive) {
		return getFiles(configDirPath, extension, recursive);
	}
	
	/**
	 * @see #getFiles(String, String)
	 */
	public List<File> getFiles(String extension) {
		return getFiles(configDirPath, extension);
	}
	
	/**
	 * Fetches the config. file from its relative path inside the configuration folder.
	 * 
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @return The {@link File} instance.
	 */
	public static File getFile(String configDirPath, String configFilePath) {
		StringBuilder path = new StringBuilder(configDirPath);
		path.append(File.separator).append(configFilePath);
		return new File(path.toString());
	}
	
	/**
	 * @see #getFile(String, String)
	 */
	public File getFile(String configFilePath) {
		return getFile(configDirPath, configFilePath);
	}
	
	/**
	 * Returns the checksum file relative path inside the configuration folder.
	 * 
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @return The checksum fime name, eg. "subdir/config.checksum"
	 */
	public static String getChecksumFilePath(String configDirPath, String configFilePath) {
		
		final Path path = Paths.get(configFilePath);
		String configFileName = path.getFileName().toString();
		
		String dirPath = "";
		if (path.getParent() != null) {
			dirPath = path.getParent().getFileName().toString() + File.separator;
		}
		
		// addressConfiguration.xml -> addressConfiguration.checksum 
		return dirPath + FilenameUtils.getBaseName(configFileName) + "." + CHECKSUM_FILE_EXT;
	}
	
	/**
	 * @see #getChecksumFilePath(String, String)
	 */
	public String getChecksumFilePath(String configFilePath) {
		return getChecksumFilePath(configDirPath, configFilePath);
	}
	
	/**
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @return The checksum of the config. file that was last successfully loaded.
	 */
	public static String readLatestChecksum(String configDirPath, String configFilePath) {
		
		String checksum = NOT_READABLE_CHECKSUM;
		
		final String hashFileName = getChecksumFilePath(configDirPath, configFilePath);
		try {
			final File hashFile = getFile(configDirPath, hashFileName);
			if (hashFile.exists()) {
				checksum = FileUtils.readFileToString(hashFile, "UTF-8");
			}
		}
		catch (Exception e) {
			log.warn("Error reading latest checksum of entry file from " + hashFileName, e);
		}
		return checksum;
	}
	
	/**
	 * @see #readLatestChecksum(String, String)
	 */
	public String readLatestChecksum(String configFilePath) {
		return readLatestChecksum(configDirPath, configFilePath);
	}
	
	/**
	 * Compute the checksum of a configuration file.
	 * 
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @return The checksum of the file.
	 */
	public static String computeChecksum(String configDirPath, String configFilePath) {
		
		String checksum = NOT_COMPUTABLE_CHECKSUM;
		
		File file = getFile(configDirPath, configFilePath);
		if (file.exists()) {
			try {
				// checksum = Long.toHexString( FileUtils.checksumCRC32(file) );
				FileInputStream fis = new FileInputStream(file);
				checksum = DigestUtils.md5Hex(fis);
				fis.close();
			}
			catch (Exception e) {
				log.warn("Error computing checksum of config. file: " + configFilePath, e);
			}
		}
		return checksum;
	}
	
	/**
	 * @see #computeChecksum(String, String)
	 */
	public String computeChecksum(String configFilePath) {
		return computeChecksum(configDirPath, configFilePath);
	}
	
	/**
	 * Writes the the checksum of a config. file to the corresponding .checksum file.
	 * 
	 * @param configDirPath The absolute path to the containing directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 * @param checksum The hash of the config. file.
	 */
	public static void writeChecksum(String configDirPath, String configFilePath, String checksum) {
		
		deleteChecksum(configDirPath, configFilePath);
		
		if (NOT_COMPUTABLE_CHECKSUM.equals(checksum)) {
			return;
		}
		
		final String checksumFilePath = getChecksumFilePath(configDirPath, configFilePath);
		try {
			FileUtils.writeStringToFile(getFile(configDirPath, checksumFilePath), checksum, "UTF-8");
		}
		catch (Exception e) {
			log.error("Error writing hash ('" + checksum + "') of configuration file to: " + checksumFilePath, e);
		}
	}
	
	/**
	 * @see #writeChecksum(String, String, String)
	 */
	public void writeChecksum(String configFilePath, String checksum) {
		writeChecksum(configDirPath, configFilePath, checksum);
	}
	
	/**
	 * Deletes the the .checksum checksum file of a config. file.
	 * 
	 * @param configFilePath The config. file relative path inside the containing directory, eg.
	 *            "subdir/config.xml"
	 */
	public static void deleteChecksum(String configDirPath, String configFilePath) {
		
		final String checksumFileName = getChecksumFilePath(configDirPath, configFilePath);
		try {
			Files.deleteIfExists(getFile(configDirPath, checksumFileName).toPath());
		}
		catch (IOException e) {
			log.warn("Error deleting hash of configuration file: " + configFilePath, e);
		}
	}
	
	/**
	 * @see #deleteChecksum(String, String)
	 */
	public void deleteChecksum(String configFilePath) {
		deleteChecksum(configDirPath, configFilePath);
	}
	
	/**
	 * Removes all the checksum files inside the provided directory.
	 * 
	 * @param configDirPath The absolute path to the directory, eg.
	 *            "../configuration/addresshierarchy"
	 * @param recursive Set to true to continue recursively into subdirectories.
	 */
	public static void deleteChecksums(String configDirPath, boolean recursive) {
		
		deleteChecksums(configDirPath);
		
		if (recursive) {
			final String[] dirs = new File(configDirPath).list(getDirectoryFilenameFilter());
			if (dirs != null) {
				for (String dir : dirs) {
					deleteChecksums(dir, true);
				}
			}
		}
	}
	
	/**
	 * @see #deleteChecksums(String, boolean)
	 */
	public void deleteChecksums(boolean recursive) {
		deleteChecksums(configDirPath, recursive);
	}
	
	/**
	 * @see #deleteChecksums(boolean)
	 */
	public void deleteChecksums() {
		deleteChecksums(configDirPath);
	}
	
	/**
	 * Removes all the checksum files inside the provided directory.
	 * 
	 * @param configDirPath The absolute path to the directory, eg.
	 *            "../configuration/addresshierarchy"
	 */
	public static void deleteChecksums(String configDirPath) {
		
		final File[] checksumFiles = new File(configDirPath).listFiles(getExtensionFilenameFilter(CHECKSUM_FILE_EXT));
		
		if (checksumFiles != null) {
			for (File file : checksumFiles) {
				file.delete();
			}
		}
	}
}

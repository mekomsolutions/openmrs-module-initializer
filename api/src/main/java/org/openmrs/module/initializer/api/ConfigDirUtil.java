package org.openmrs.module.initializer.api;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps read and write to and from the configuration and checksums directories.
 */
public class ConfigDirUtil {
	
	protected static final Logger log = LoggerFactory.getLogger(ConfigDirUtil.class);
	
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
	
	/**
	 * Instantiates a configuration directory utility class for the specified configuration and checksum
	 * files directories and for the specified domain.
	 * 
	 * @param configDirPath The absolute path to the configuration directory.
	 * @param domain The domain name within the configuration directory.
	 */
	public ConfigDirUtil(String configDirPath, String domain) {
		this.domain = domain;
		this.domainDirPath = Paths.get(configDirPath, domain).toString();
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getDomainDirPath() {
		return domainDirPath;
	}
	
	@Override
	public String toString() {
		return domainDirPath;
	}
	
	/**
	 * Convenience method to get a FilenameFilter for files of a given extension.
	 * 
	 * @param extension The dot-less extension to filter for, eg. "xml", "json".
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
	 * file name "level1_level2_config" (so as a based file name, without an extension). Located file
	 * names are used to generate checksum file names so that they can all live at the same level in the
	 * checksum files folder.
	 * 
	 * @param domainDirPath The absolute path to the domain, eg. "../configuration/domain"
	 * @param file The configuration file nested somewhere in the domain directory.
	 * @return The located file name.
	 */
	public static String getLocatedFilename(String domainDirPath, File file) {
		return FilenameUtils
		        .removeExtension(Paths.get(domainDirPath).relativize(file.toPath()).toString().replace(File.separator, "_"));
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
	public static List<File> getFiles(String domainDirPath, String extension, List<String> wildcardExclusions) {
		
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
}

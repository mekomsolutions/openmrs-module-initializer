package org.openmrs.module.initializer.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.openmrs.module.initializer.api.ConfigDirUtil.CHECKSUM_FILE_EXT;
import static org.openmrs.module.initializer.api.ConfigDirUtil.getLocatedFilename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.api.c.ConceptsLoader;

public class ConfigDirUtilTest {
	
	private String dirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include/file_patterns")
	        .getPath();
	
	private List<String> allFiles;
	
	@Before
	public void before() {
		allFiles = Arrays.asList(new File(dirPath).list());
	}
	
	@Test
	public void getFiles_shouldFilterOutBasedOnWildCardPattern() {
		{
			// setup
			List<String> patterns = null;
			
			// replay
			List<String> files = ConfigDirUtil.getFiles(dirPath, "csv", patterns).stream().map(f -> f.getName())
			        .collect(Collectors.toList());
			Collection<String> excludedFiles = CollectionUtils.subtract(allFiles, files);
			
			// verify
			Assert.assertThat(excludedFiles, is(empty()));
		}
		{
			// setup
			List<String> patterns = Arrays.asList("diagnoses*");
			
			// replay
			List<String> files = ConfigDirUtil.getFiles(dirPath, "csv", patterns).stream().map(f -> f.getName())
			        .collect(Collectors.toList());
			Collection<String> excludedFiles = CollectionUtils.subtract(allFiles, files);
			
			// verify
			Assert.assertThat(excludedFiles.size(), is(3));
			Assert.assertThat(excludedFiles, containsInAnyOrder("diagnoses.csv", "diagnoses_02.csv", "diagnoses_03.csv"));
		}
		{
			// setup
			List<String> patterns = Arrays.asList("*diagnoses*");
			
			// replay
			List<String> files = ConfigDirUtil.getFiles(dirPath, "csv", patterns).stream().map(f -> f.getName())
			        .collect(Collectors.toList());
			Collection<String> excludedFiles = CollectionUtils.subtract(allFiles, files);
			
			// verify
			Assert.assertThat(excludedFiles.size(), is(5));
			Assert.assertThat(excludedFiles, containsInAnyOrder("diagnoses.csv", "diagnoses_02.csv", "diagnoses_03.csv",
			    "newer_diagnoses.csv", "retired_diagnoses.csv"));
		}
	}
	
	@Test
	public void getFiles_shouldHandleChecksumsWhenNestedFiles() throws IOException {
		// setup
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include").getPath();
		String checksumsDirPath = Files.createTempDirectory("configuration_checksums").toString();
		String domain = "nested_txt_files";
		
		// replay
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, checksumsDirPath, domain);
		
		Map<String, String> fileContents = new HashMap<>();
		
		for (File file : dirUtil.getFiles("txt")) {
			String checksum = dirUtil.getChecksumIfChanged(file);
			{
				// we store each file "path" and text content
				String locatedFilename = getLocatedFilename(Paths.get(configDirPath, domain).toString(), file);
				fileContents.put(locatedFilename, FileUtils.readFileToString(file, "UTF-8"));
			}
			dirUtil.writeChecksum(file, checksum);
		}
		
		Assert.assertThat(fileContents.size(), is(3));
		for (String locatedFilename : fileContents.keySet()) {
			
			String checksumFilename = locatedFilename + "." + CHECKSUM_FILE_EXT;
			
			// verify checksum
			File checksumFile = new File(Paths.get(checksumsDirPath, "nested_txt_files", checksumFilename).toUri());
			Assert.assertThat(checksumFile.exists(), is(true));
			Assert.assertEquals(DigestUtils.md5Hex(fileContents.get(locatedFilename)),
			    FileUtils.readFileToString(checksumFile, "UTF-8"));
			
			// verify deletion
			dirUtil.deleteChecksumFile(checksumFilename);
			Assert.assertThat(checksumFile.exists(), is(false));
		}
	}
	
	/*
	 * One of the CSV files has a non-parseable _order.
	 * The resulting exception is logged as an error.
	 * TODO: Maybe assert the logger?
	 */
	@Test
	public void getOrderedFiles_shouldReturnOrderedCsvFilesWithConceptsLoader() {
		// Setup
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include/csv")
		        .getPath();
		String checksumsDirPath = null;
		String domain = "orders";
		
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, checksumsDirPath, domain);
		
		// Replay
		List<File> orderableFiles = dirUtil.getOrderedFiles("csv", null, new ConceptsLoader());
		
		// Verif
		Assert.assertEquals("5_order_500.csv", orderableFiles.get(0).getName());
		Assert.assertEquals("4_order_1000.csv", orderableFiles.get(1).getName());
		Assert.assertEquals("1_order_1500.csv", orderableFiles.get(2).getName());
	}
}

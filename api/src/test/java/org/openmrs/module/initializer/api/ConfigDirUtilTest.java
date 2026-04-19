package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.openmrs.module.initializer.api.ConfigDirUtil.getLocatedFilename;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
	public void getFiles_shouldReturnNestedFiles() {
		// setup
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include").getPath();
		
		// replay
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, "nested_txt_files");
		
		assertThat(
		    dirUtil.getFiles("txt").stream().map(f -> Paths.get(configDirPath).relativize(f.toPath()).toString())
		            .collect(Collectors.toList()),
		    containsInAnyOrder("nested_txt_files/config.txt", "nested_txt_files/level1/config.txt",
		        "nested_txt_files/level1/level2/config.txt"));
	}
	
	@Test
	public void getLocatedFilename_shouldHandleRootLevelAndSubdirLevelFiles() {
		// with trailing slashes
		Assert.assertEquals("cfg", getLocatedFilename("/configuration/domain/", new File("/configuration/domain/cfg.txt")));
		Assert.assertEquals("subdir_cfg",
		    getLocatedFilename("/configuration/domain/", new File("/configuration/domain/subdir/cfg.txt")));
		Assert.assertEquals("subdir_subsubdir_cfg",
		    getLocatedFilename("/configuration/domain/", new File("/configuration/domain/subdir/subsubdir/cfg.txt")));
		// without trailing slashes
		Assert.assertEquals("cfg", getLocatedFilename("/configuration/domain", new File("/configuration/domain/cfg.txt")));
		Assert.assertEquals("subdir_cfg",
		    getLocatedFilename("/configuration/domain", new File("/configuration/domain/subdir/cfg.txt")));
		Assert.assertEquals("subdir_subsubdir_cfg",
		    getLocatedFilename("/configuration/domain", new File("/configuration/domain/subdir/subsubdir/cfg.txt")));
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
			assertThat(excludedFiles, is(empty()));
		}
		{
			// setup
			List<String> patterns = Arrays.asList("diagnoses*");
			
			// replay
			List<String> files = ConfigDirUtil.getFiles(dirPath, "csv", patterns).stream().map(f -> f.getName())
			        .collect(Collectors.toList());
			Collection<String> excludedFiles = CollectionUtils.subtract(allFiles, files);
			
			// verify
			assertThat(excludedFiles.size(), is(3));
			assertThat(excludedFiles, containsInAnyOrder("diagnoses.csv", "diagnoses_02.csv", "diagnoses_03.csv"));
		}
		{
			// setup
			List<String> patterns = Arrays.asList("*diagnoses*");
			
			// replay
			List<String> files = ConfigDirUtil.getFiles(dirPath, "csv", patterns).stream().map(f -> f.getName())
			        .collect(Collectors.toList());
			Collection<String> excludedFiles = CollectionUtils.subtract(allFiles, files);
			
			// verify
			assertThat(excludedFiles.size(), is(5));
			assertThat(excludedFiles, containsInAnyOrder("diagnoses.csv", "diagnoses_02.csv", "diagnoses_03.csv",
			    "newer_diagnoses.csv", "retired_diagnoses.csv"));
		}
	}
	
	/*
	 * One of the CSV files has a non-parseable _order.
	 * The resulting exception is logged as an error.
	 * TODO: Maybe assert the logger?
	 */
	@Test
	public void getFiles_shouldAllowToOrderCsvFilesWithConceptsLoader() {
		// Setup
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include/csv")
		        .getPath();
		String checksumsDirPath = null;
		String domain = "orders";
		
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, domain);
		
		// Replay
		List<String> orderedFilenames = dirUtil.getFiles("csv", null).stream()
		        .map(f -> new ConceptsLoader().toOrderedFile(f)).sorted().map(f -> f.getName()).collect(Collectors.toList());
		
		// Verif
		assertThat(orderedFilenames.size(), is(5));
		Assert.assertEquals("5_order_500.csv", orderedFilenames.get(0));
		Assert.assertEquals("4_order_1000.csv", orderedFilenames.get(1));
		Assert.assertEquals("1_order_1500.csv", orderedFilenames.get(2));
		assertThat(orderedFilenames.subList(3, 5), containsInAnyOrder("3_order_missing.csv", "2_order_e00.csv"));
	}
}

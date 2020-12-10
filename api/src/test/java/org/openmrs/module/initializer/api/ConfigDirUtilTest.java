package org.openmrs.module.initializer.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
}

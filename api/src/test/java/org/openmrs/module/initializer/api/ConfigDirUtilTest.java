package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.openmrs.module.initializer.api.ConfigDirUtil.CHECKSUM_FILE_EXT;
import static org.openmrs.module.initializer.api.ConfigDirUtil.ROW_CHECKSUM_FILE_EXT;
import static org.openmrs.module.initializer.api.ConfigDirUtil.computeRowChecksum;
import static org.openmrs.module.initializer.api.ConfigDirUtil.getLocatedFilename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		
		assertThat(fileContents.size(), is(3));
		for (String locatedFilename : fileContents.keySet()) {
			
			String checksumFilename = locatedFilename + "." + CHECKSUM_FILE_EXT;
			
			// verify checksum
			File checksumFile = new File(Paths.get(checksumsDirPath, "nested_txt_files", checksumFilename).toUri());
			assertThat(checksumFile.exists(), is(true));
			Assert.assertEquals(DigestUtils.md5Hex(fileContents.get(locatedFilename)),
			    FileUtils.readFileToString(checksumFile, "UTF-8"));
			
			// verify deletion
			dirUtil.deleteChecksumFile(checksumFilename);
			assertThat(checksumFile.exists(), is(false));
		}
	}
	
	@Test
	public void computeRowChecksum_shouldBeStableUnderColumnReordering() {
		String[] header1 = { "uuid", "name", "description" };
		String[] row1 = { "abc-123", "Acme", "A clinic" };
		String[] header2 = { "name", "description", "uuid" };
		String[] row2 = { "Acme", "A clinic", "abc-123" };
		Assert.assertEquals(computeRowChecksum(header1, row1), computeRowChecksum(header2, row2));
	}
	
	@Test
	public void computeRowChecksum_shouldChangeWhenAValueChanges() {
		String[] header = { "uuid", "name" };
		String[] row1 = { "abc-123", "Acme" };
		String[] row2 = { "abc-123", "Beta" };
		assertThat(computeRowChecksum(header, row1), is(not(computeRowChecksum(header, row2))));
	}
	
	@Test
	public void computeRowChecksum_shouldDistinguishAbsentFromPresentButEmpty() {
		// Adding a column — even when its cell is empty — must change the hash, because some line
		// processors (e.g. NestedConceptLineProcessor) treat a present-but-empty cell as a directive
		// to clear an existing field and treat an absent column as "leave the field alone".
		String[] header1 = { "uuid", "name" };
		String[] row1 = { "abc-123", "Acme" };
		String[] header2 = { "uuid", "name", "description" };
		String[] row2 = { "abc-123", "Acme", null };
		String[] row3 = { "abc-123", "Acme", "" };
		assertThat(computeRowChecksum(header1, row1), is(not(computeRowChecksum(header2, row2))));
		assertThat(computeRowChecksum(header1, row1), is(not(computeRowChecksum(header2, row3))));
		// However null and empty values within an existing column should be treated identically,
		// since CsvParser normalizes blank cells to null on read.
		Assert.assertEquals(computeRowChecksum(header2, row2), computeRowChecksum(header2, row3));
	}
	
	@Test
	public void computeRowChecksum_shouldChangeWhenAColumnIsRenamed() {
		String[] header1 = { "uuid", "name" };
		String[] header2 = { "uuid", "label" };
		String[] row = { "abc-123", "Acme" };
		assertThat(computeRowChecksum(header1, row), is(not(computeRowChecksum(header2, row))));
	}
	
	@Test
	public void rowChecksums_shouldRoundTripThroughDisk() throws IOException {
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include").getPath();
		String checksumsDirPath = Files.createTempDirectory("configuration_checksums_rows").toString();
		String domain = "file_patterns";
		
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, checksumsDirPath, domain);
		File configFile = new File(Paths.get(configDirPath, domain, "diagnoses.csv").toString());
		
		Set<String> hashes = new HashSet<>();
		hashes.add("hash-a");
		hashes.add("hash-b");
		hashes.add("hash-c");
		
		// Writing then reading should round-trip the set.
		dirUtil.writeRowChecksums(configFile, hashes);
		File rowsFile = Paths.get(checksumsDirPath, domain,
		    getLocatedFilename(Paths.get(configDirPath, domain).toString(), configFile) + "." + ROW_CHECKSUM_FILE_EXT)
		        .toFile();
		assertThat(rowsFile.exists(), is(true));
		assertThat(dirUtil.readRowChecksums(configFile), is(hashes));
		
		// Writing an empty set should remove the file.
		dirUtil.writeRowChecksums(configFile, new HashSet<>());
		assertThat(rowsFile.exists(), is(false));
		
		// Reading when no file exists returns an empty set.
		assertThat(dirUtil.readRowChecksums(configFile), is(empty()));
	}
	
	@Test
	public void deleteRowChecksums_shouldRemoveTheRowChecksumFile() throws IOException {
		String configDirPath = getClass().getClassLoader().getResource("org/openmrs/module/initializer/include").getPath();
		String checksumsDirPath = Files.createTempDirectory("configuration_checksums_rows_delete").toString();
		String domain = "file_patterns";
		
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, checksumsDirPath, domain);
		File configFile = new File(Paths.get(configDirPath, domain, "diagnoses.csv").toString());
		
		Set<String> hashes = new HashSet<>();
		hashes.add("hash-a");
		dirUtil.writeRowChecksums(configFile, hashes);
		
		File rowsFile = Paths.get(checksumsDirPath, domain,
		    getLocatedFilename(Paths.get(configDirPath, domain).toString(), configFile) + "." + ROW_CHECKSUM_FILE_EXT)
		        .toFile();
		assertThat(rowsFile.exists(), is(true));
		
		dirUtil.deleteRowChecksums(configFile);
		assertThat(rowsFile.exists(), is(false));
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
		
		ConfigDirUtil dirUtil = new ConfigDirUtil(configDirPath, checksumsDirPath, domain);
		
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

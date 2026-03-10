package org.openmrs.module.initializer.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.module.initializer.api.loaders.BaseFileLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class InitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	AdministrationService adminService;
	
	@Autowired
	InitializerDAO initializerDAO;
	
	@Autowired
	InitializerConfig initializerConfig;
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	Path tempDir;
	
	private InitializerService service;
	
	@Before
	public void setup() {
		tempDir = temporaryFolder.getRoot().toPath();
		
		InitializerServiceImpl initializerService = new InitializerServiceImpl();
		initializerService.setInitializerDAO(initializerDAO);
		initializerService.setConfig(initializerConfig);
		initializerService.setAdminService(adminService);
		service = Mockito.spy(initializerService);
		
		when(service.getConfigDirPath()).thenReturn(tempDir.resolve("config").toString());
		when(service.getChecksumsDirPath()).thenReturn(tempDir.resolve("checksums").toString());
		
		service.clearChecksums();
		service.clearChecksumsCache();
	}
	
	@Test
	public void loadUnsafe_shouldHandleNestedFiles() throws Exception {
		String domain = Domain.METADATA_SETS.getName();
		File domainDir = new File(new File(tempDir.toFile(), "config"), domain);
		domainDir.mkdirs();
		FileUtils.writeStringToFile(new File(domainDir, "config0.txt"), "config0", "UTF-8");
		
		File level1 = new File(domainDir, "level1");
		level1.mkdirs();
		FileUtils.writeStringToFile(new File(level1, "config1.txt"), "config1", "UTF-8");
		
		File level2 = new File(level1, "level2");
		level2.mkdirs();
		FileUtils.writeStringToFile(new File(level2, "config2.txt"), "config2", "UTF-8");
		
		BaseFileLoader loader = new BaseFileLoader() {
			
			@Override
			protected String getFileExtension() {
				return "txt";
			}
			
			@Override
			protected void load(File file) throws Exception {
				
			}
			
			@Override
			protected Domain getDomain() {
				return Domain.METADATA_SETS;
			}
		};
		loader.setIniz(service);
		loader.setCfg(initializerConfig);
		
		when(service.getLoaders()).thenReturn(Collections.singletonList(loader));
		
		service.loadUnsafe(true, false);
		
		assertThat(service.getSavedChecksums().size(), is(3));
		assertThat(service.getSavedChecksums(),
		    hasEntry("metadatasets/level1/level2/config2.txt", "02dda8b322cc9afc1d17ae05d54dd065"));
		assertThat(service.getSavedChecksums(),
		    hasEntry("metadatasets/level1/config1.txt", "b1361bf5a751c823a9cafbf0c431d5a7"));
		assertThat(service.getSavedChecksums(), hasEntry("metadatasets/config0.txt", "738b948db48f1f4d91eb7576ecd4e865"));
	}
	
	@Test
	public void migrateChecksumsFromFilesToDB_shouldMigrateFilesAndSetGlobalProperty() throws IOException {
		String domain = "testdomain";
		File domainDir = new File(new File(tempDir.toFile(), "config"), domain);
		domainDir.mkdirs();
		File configFile = new File(domainDir, "config.txt");
		FileUtils.writeStringToFile(configFile, "some content", "UTF-8");
		
		File domainChecksumDir = new File(new File(tempDir.toFile(), "checksums"), domain);
		domainChecksumDir.mkdirs();
		File checksumFile = new File(domainChecksumDir, "config." + ConfigDirUtil.CHECKSUM_FILE_EXT);
		String checksumValue = "checksum123";
		FileUtils.writeStringToFile(checksumFile, checksumValue, "UTF-8");
		
		service.migrateChecksumsFromFilesToDB();
		
		assertEquals(1, service.getSavedChecksums().size());
		String savedChecksum = service.getSavedChecksums().get(domain + "/config.txt");
		assertEquals(checksumValue, savedChecksum);
		assertFalse(checksumFile.exists());
		
		assertEquals("true", adminService.getGlobalProperty("initializer.checksumsMigrated"));
	}
	
	@Test
	public void migrateChecksumsFromFilesToDB_shouldSkipIfGlobalPropertyExists() throws IOException {
		adminService.setGlobalProperty("initializer.checksumsMigrated", "true");
		
		File domainDir = new File(new File(tempDir.toFile(), "config"), "domain");
		domainDir.mkdirs();
		File configFile = new File(domainDir, "config.txt");
		FileUtils.writeStringToFile(configFile, "content", "UTF-8");
		
		service.migrateChecksumsFromFilesToDB();
		
		assertTrue(service.getSavedChecksums().isEmpty());
	}
	
	@Test
	public void migrateChecksumsFromFilesToDB_shouldHandleSubdirectories() throws IOException {
		String domain = "testdomain";
		File domainDir = new File(new File(tempDir.toFile(), "config"), domain);
		File subDir = new File(domainDir, "subdir");
		subDir.mkdirs();
		
		File configFile = new File(subDir, "config.txt");
		FileUtils.writeStringToFile(configFile, "content", "UTF-8");
		
		File domainChecksumDir = new File(new File(tempDir.toFile(), "checksums"), domain);
		domainChecksumDir.mkdirs();
		// domain/subdir/config.txt -> domain/subdir_config.checksum
		File checksumFile = new File(domainChecksumDir, "subdir_config." + ConfigDirUtil.CHECKSUM_FILE_EXT);
		String checksumValue = "checksumSub";
		FileUtils.writeStringToFile(checksumFile, checksumValue, "UTF-8");
		
		service.migrateChecksumsFromFilesToDB();
		
		assertEquals(1, service.getSavedChecksums().size());
		String savedChecksum = service.getSavedChecksums().get(domain + "/subdir/config.txt");
		assertEquals(checksumValue, savedChecksum);
		assertFalse(checksumFile.exists());
	}
	
	@Test
	public void deleteChecksums_shouldDeleteChecksumsStartingWithGivenPath() {
		service.saveOrUpdateChecksum(new InitializerChecksum("concept/concept1.csv", "hash1"));
		service.saveOrUpdateChecksum(new InitializerChecksum("concept/concept2.csv", "hash2"));
		service.saveOrUpdateChecksum(new InitializerChecksum("encounter/enc1.csv", "hash3"));
		service.saveOrUpdateChecksum(new InitializerChecksum("conceptNumeric/concept3.csv", "hash4"));
		
		service.deleteChecksums("concept");
		
		assertEquals(2, service.getSavedChecksums().size());
		assertTrue(service.getSavedChecksums().containsKey("encounter/enc1.csv"));
		assertTrue(service.getSavedChecksums().containsKey("conceptNumeric/concept3.csv"));
	}
}

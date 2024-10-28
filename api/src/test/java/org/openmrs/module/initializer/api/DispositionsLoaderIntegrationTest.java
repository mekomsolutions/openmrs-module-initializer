package org.openmrs.module.initializer.api;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.DispositionsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DispositionsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	protected InitializerService iniz;
	
	@Autowired
	private DispositionsLoader loader;
	
	@Autowired
	private DispositionService dispositionService;
	
	@Test
	public void load_shouldLoadDisposition() {
		loader.load(); 
		assertEquals(5, dispositionService.getDispositions().size());
	}
	
	@Test
	public void load_shouldReloadOnMultipleLoads() {
		loader.load();
		// sanity check
		assertEquals(5, dispositionService.getDispositions().size());
		
		dispositionService.setDispositionConfig(null);
		loader.load();
		assertEquals(5, dispositionService.getDispositions().size());
	}
	
	@Test(expected = RuntimeException.class)
	public void load_shouldThrowExceptionInUnsafeModeIfMultipleFiles() throws Exception {
		String existingFilePath = loader.getDirUtil().getDomainDirPath() + "/dispositionConfig.json";
		String additionalFilePath = loader.getDirUtil().getDomainDirPath() + "/additionalDispositionConfig.json";
		File srcFile = new File(existingFilePath);
		File dstFile = new File(additionalFilePath);
		FileUtils.copyFile(srcFile, dstFile);

		try {
			loader.loadUnsafe(null, true);
		}
		finally {
			FileUtils.deleteQuietly(dstFile);
		}
	}
}

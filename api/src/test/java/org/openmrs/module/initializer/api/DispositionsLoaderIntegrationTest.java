package org.openmrs.module.initializer.api;

import org.junit.Test;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.DispositionsLoader;
import org.springframework.beans.factory.annotation.Autowired;

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
		loader.load(); // ignore the test file we are using for the exception test below
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
	public void load_shouldThrowExceptionInUnsafeMode() throws Exception {
		loader.loadUnsafe(null, true);
	}
	
}

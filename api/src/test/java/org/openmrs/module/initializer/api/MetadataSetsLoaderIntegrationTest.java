package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.mds.MetadataSetsLoader;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.springframework.beans.factory.annotation.Autowired;

public class MetadataSetsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private MetadataMappingService service;
	
	@Autowired
	private MetadataSetsLoader loader;
	
	private static String SET_TO_RETIRE_UUID = "97f94721-2f01-463a-95bb-3ce780bdfea6";
	
	private static String SET_TO_EDIT_UUID = "f0ebcb99-7618-41b7-b0bf-8ff93de67b9e";
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadatasets.xml");
	}
	
	@Test
	public void load_shouldLoadMetadataSetsFromCSVLines() {
		// Setup
		MetadataSet set = null;
		
		// Replay
		loader.load();
		
		// Verify created
		set = service.getMetadataSetByUuid("e3410148-a7df-447c-9bde-1a08aaecc0f5");
		Assert.assertNotNull(set);
		Assert.assertEquals("Required Attribute Types", set.getName());
		
		// Verify edited
		set = service.getMetadataSetByUuid(SET_TO_EDIT_UUID);
		Assert.assertEquals("Extra Identifiers Set", set.getName());
		Assert.assertEquals("Set of extra patient identifiers", set.getDescription());
		
		// Verify retired
		set = service.getMetadataSetByUuid(SET_TO_RETIRE_UUID);
		Assert.assertTrue(set.isRetired());
	}
}

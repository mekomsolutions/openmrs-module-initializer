package org.openmrs.module.initializer.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AttributeTypesLoaderTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AttributeTypesLoader loader;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	@Qualifier("visitService")
	private VisitService vs;
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creations
		{}
		{}
		
		// Verify editions
		{}
	}
	
	// More tests go here ....
	
}

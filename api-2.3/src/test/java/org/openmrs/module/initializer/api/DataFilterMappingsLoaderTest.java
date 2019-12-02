package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.module.datafilter.impl.api.DataFilterService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.datafilter.mappings.DataFilterMappingsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DataFilterMappingsLoaderTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private DataFilterMappingsLoader loader;
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private DataFilterService dfs;
	
	@Override
	public void updateSearchIndex() {
		// to prevent Data Filter's 'Illegal Record Access'
	}
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-metadata.xml");
		
		dfs.grantAccess(us.getPrivilege("Add Apples"), ls.getLocation(4089));
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// verify new access for entity through basis
		Assert.assertTrue(dfs.hasAccess(us.getRole("test-entity-role"), pws.getProgram(4089)));
		
		// verify revoked access for entity through basis
		Assert.assertFalse(dfs.hasAccess(us.getPrivilege("Add Apples"), ls.getLocation(4089)));
	}
}

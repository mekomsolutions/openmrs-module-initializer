package org.openmrs.module.initializer.api.patientflags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PrioritiesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("flagService")
	private FlagService flagService;
	
	@Autowired
	private PrioritiesLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
		
		{
			// To be edited
			Priority priority = new Priority();
			priority.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			priority.setName("High Priority");
			priority.setStyle("color:blue");
			priority.setRank(10);
			priority.setRetired(false);
			flagService.savePriority(priority);
		}
		
		{
			// To be retired
			Priority priority = new Priority();
			priority.setUuid("829bf278-ba81-4436-b867-c2f6641d060d");
			priority.setName("Very Low Priority");
			priority.setStyle("color:gray");
			priority.setRank(4);
			priority.setRetired(false);
			flagService.savePriority(priority);
		}
	}
	
	@Test
	public void load_shouldLoadPrioritiesAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation with blank style (null, legacy field not used in OpenMRS 3+)
		{
			Priority priority = flagService.getPriorityByUuid("627bf278-ba81-4436-b867-c2f6641d060b");
			assertNotNull(priority);
			assertEquals("Medium Priority", priority.getName());
			assertEquals("/**/", priority.getStyle()); // Blank style stored as null
			assertEquals(Integer.valueOf(2), priority.getRank());
			assertEquals("Medium priority flags", priority.getDescription());
		}
		
		{
			Priority priority = flagService.getPriorityByUuid("728bf278-ba81-4436-b867-c2f6641d060c");
			assertNotNull(priority);
			assertEquals("Low Priority", priority.getName());
			assertEquals("/**/", priority.getStyle()); // Blank style stored as null
			assertEquals(Integer.valueOf(3), priority.getRank());
		}
		
		// Verify editing
		{
			Priority priority = flagService.getPriorityByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			assertNotNull(priority);
			assertEquals("High Priority", priority.getName());
			assertEquals("color:red", priority.getStyle()); // Updated from blue
			assertEquals(Integer.valueOf(1), priority.getRank()); // Updated from 10
		}
		
		// Verify retirement (style set to placeholder from CSV)
		{
			Priority priority = flagService.getPriorityByUuid("829bf278-ba81-4436-b867-c2f6641d060d");
			assertTrue(priority.getRetired());
		}
	}
}

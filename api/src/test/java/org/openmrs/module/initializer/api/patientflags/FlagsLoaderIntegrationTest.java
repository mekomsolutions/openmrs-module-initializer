package org.openmrs.module.initializer.api.patientflags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class FlagsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("flagService")
	private FlagService flagService;
	
	@Autowired
	private FlagsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
		
		// Set up prerequisites: Priorities and Tags that flags will reference
		
		// Create priorities
		Priority highPriority = new Priority();
		highPriority.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
		highPriority.setName("High Priority");
		highPriority.setStyle("color:red");
		highPriority.setRank(1);
		highPriority.setRetired(false);
		flagService.savePriority(highPriority);
		
		Priority mediumPriority = new Priority();
		mediumPriority.setUuid("627bf278-ba81-4436-b867-c2f6641d060b");
		mediumPriority.setName("Medium Priority");
		mediumPriority.setStyle("color:orange");
		mediumPriority.setRank(2);
		mediumPriority.setRetired(false);
		flagService.savePriority(mediumPriority);
		
		Priority lowPriority = new Priority();
		lowPriority.setUuid("728bf278-ba81-4436-b867-c2f6641d060c");
		lowPriority.setName("Low Priority");
		lowPriority.setStyle("color:yellow");
		lowPriority.setRank(3);
		lowPriority.setRetired(false);
		flagService.savePriority(lowPriority);
		
		// Create tags
		Tag hivTag = new Tag();
		hivTag.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
		hivTag.setName("HIV");
		hivTag.setRetired(false);
		flagService.saveTag(hivTag);
		
		Tag clinicalTag = new Tag();
		clinicalTag.setUuid("627bf278-ba81-4436-b867-c2f6641d060b");
		clinicalTag.setName("Clinical");
		clinicalTag.setRetired(false);
		flagService.saveTag(clinicalTag);
		
		Tag urgentTag = new Tag();
		urgentTag.setUuid("728bf278-ba81-4436-b867-c2f6641d060c");
		urgentTag.setName("Urgent");
		urgentTag.setRetired(false);
		flagService.saveTag(urgentTag);
		
		{
			// To be edited - create a flag that will be updated
			Flag flag = new Flag();
			flag.setUuid("f279d252-g6ge-5ffd-ce4f-744cef2d0717");
			flag.setName("Test Flag");
			
			flag.setCriteria("SELECT a.patient_id FROM allergy a where a.allergen_type = 'CATS'");
			flag.setEvaluator("org.openmrs.module.patientflags.evaluator.SqlFlagEvaluator");
			flag.setMessage("Old test message");
			flag.setPriority(lowPriority);
			flag.setEnabled(false);
			flag.setRetired(false);
			flagService.saveFlag(flag);
		}
		
		{
			// To be retired
			Flag flag = new Flag();
			flag.setUuid("g38ae363-h7hf-6gge-df5g-855dfg3e1828");
			flag.setName("Retired Flag");
			flag.setCriteria("SELECT a.patient_id FROM allergy a where a.allergen_type = 'DOGS'");
			flag.setEvaluator("org.openmrs.module.patientflags.evaluator.SqlFlagEvaluator");
			flag.setMessage("Retired message");
			flag.setPriority(lowPriority);
			flag.setEnabled(true);
			flag.setRetired(false);
			flagService.saveFlag(flag);
		}
	}
	
	@Test
	public void load_shouldLoadFlagsAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation
		{
			Flag flag = flagService.getFlagByUuid("e168c141-f5fd-4eec-bd3e-633bed1c9606");
			assertNotNull(flag);
			assertEquals("HIV Positive", flag.getName());
			assertEquals("SELECT c.patient_id FROM condition c where c.condition_name = 'HIV'", flag.getCriteria());
			assertEquals("org.openmrs.module.patientflags.evaluator.SqlFlagEvaluator", flag.getEvaluator());
			assertEquals("patientflags.message.hivPositive", flag.getMessage());
			assertNotNull(flag.getPriority());
			assertEquals("High Priority", flag.getPriority().getName());
			assertTrue(flag.getEnabled());
			assertNotNull(flag.getTags());
			assertEquals(2, flag.getTags().size()); // Should have HIV and Clinical tags
			assertEquals("Flag for HIV positive patients", flag.getDescription());
		}
		
		// Verify editing
		{
			Flag flag = flagService.getFlagByUuid("f279d252-g6ge-5ffd-ce4f-744cef2d0717");
			assertNotNull(flag);
			assertEquals("Test Flag", flag.getName());
			assertEquals("SELECT a.patient_id FROM allergy a where a.allergen_type = 'DRUG'", flag.getCriteria()); // Updated
			assertEquals("Test message", flag.getMessage()); // Updated
			assertNotNull(flag.getPriority());
			assertEquals("Medium Priority", flag.getPriority().getName()); // Updated
			assertTrue(flag.getEnabled()); // Updated from false
			assertNotNull(flag.getTags());
			assertEquals(1, flag.getTags().size()); // Should have Clinical tag
		}
		
		// Verify retirement
		{
			Flag flag = flagService.getFlagByUuid("g38ae363-h7hf-6gge-df5g-855dfg3e1828");
			assertTrue(flag.getRetired());
		}
	}
}

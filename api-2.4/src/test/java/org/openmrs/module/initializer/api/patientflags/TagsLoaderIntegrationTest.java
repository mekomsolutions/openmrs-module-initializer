package org.openmrs.module.initializer.api.patientflags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.initializer.api.DomainBaseModuleContextSensitive_2_4_patientflags_test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TagsLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_patientflags_test {
	
	@Autowired
	@Qualifier("flagService")
	private FlagService flagService;
	
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	private TagsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts-2.4.xml");
		
		// Create roles for testing
		Role clinicianRole = new Role("Clinician", "Clinician role");
		userService.saveRole(clinicianRole);
		
		Role nurseRole = new Role("Nurse", "Nurse role");
		userService.saveRole(nurseRole);
		
		// Create display points for testing
		DisplayPoint patientSummary = new DisplayPoint("Patient Summary");
		patientSummary.setUuid("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
		patientSummary.setRetired(false);
		flagService.saveDisplayPoint(patientSummary);
		
		DisplayPoint patientDashboard = new DisplayPoint("Patient Dashboard");
		patientDashboard.setUuid("b2c3d4e5-f6a7-8901-bcde-f12345678901");
		patientDashboard.setRetired(false);
		flagService.saveDisplayPoint(patientDashboard);
		
		{
			// To be edited
			Tag tag = new Tag();
			tag.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			tag.setName("HIV");
			tag.setRetired(false);
			flagService.saveTag(tag);
		}
		
		{
			// To be retired
			Tag tag = new Tag();
			tag.setUuid("829bf278-ba81-4436-b867-c2f6641d060d");
			tag.setName("Deprecated");
			tag.setRetired(false);
			flagService.saveTag(tag);
		}
	}
	
	@Test
	public void load_shouldLoadTagsAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation
		{
			Tag tag = flagService.getTagByUuid("627bf278-ba81-4436-b867-c2f6641d060b");
			assertNotNull(tag);
			assertEquals("Clinical", tag.getName());
			assertEquals("General clinical flags", tag.getDescription());
			assertNotNull(tag.getRoles());
			assertEquals(2, tag.getRoles().size()); // Should have Clinician and Nurse roles
		}
		
		{
			Tag tag = flagService.getTagByUuid("728bf278-ba81-4436-b867-c2f6641d060c");
			assertNotNull(tag);
			assertEquals("Urgent", tag.getName());
		}
		
		// Verify edition
		{
			Tag tag = flagService.getTagByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
			assertNotNull(tag);
			assertEquals("HIV", tag.getName());
			assertEquals("Tags for HIV-related flags", tag.getDescription());
		}
		
		// Verify retirement
		{
			Tag tag = flagService.getTagByUuid("829bf278-ba81-4436-b867-c2f6641d060d");
			assertTrue(tag.getRetired());
		}
	}
}

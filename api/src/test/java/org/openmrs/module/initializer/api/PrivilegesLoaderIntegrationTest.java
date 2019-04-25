package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.PrivilegesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PrivilegesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	@Autowired
	private PrivilegesLoader loader;
	
	@Before
	public void setup() {
		
		// privileges to be edited (description)
		{
			Privilege priv = new Privilege();
			priv.setUuid("cf68b952-2700-102b-80cb-0017a47871b2");
			priv.setPrivilege("Edit Concepts");
			priv.setDescription("Test description");
			us.savePrivilege(priv);
		}
		{
			Privilege priv = new Privilege();
			priv.setPrivilege("Add People");
			priv.setDescription("Add People objects");
			us.savePrivilege(priv);
		}
		// A privilege not to be edited
		{
			Privilege priv = new Privilege();
			priv.setUuid("cf687ee2-2700-102b-80cb-0017a47871b2");
			priv.setPrivilege("Add Test Privilege");
			us.savePrivilege(priv);
		}
	}
	
	@Test
	public void load_shouldLoadPrivilegesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created privileges
		{
			Privilege priv = us.getPrivilegeByUuid("494e1213-360d-45ac-877d-515993444290");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add Cohort", priv.getName());
			Assert.assertEquals("Add Cohort", priv.getPrivilege());
			Assert.assertEquals("Able to add a cohort to the system", priv.getDescription());
			
		}
		{
			Privilege priv = us.getPrivilege("Add Patient");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add Patient", priv.getName());
			Assert.assertEquals("Able to add new patients to the system", priv.getDescription());
		}
		// edited privileges
		{
			Privilege priv = us.getPrivilegeByUuid("cf68b952-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Edit Concepts", priv.getName());
			Assert.assertEquals("Edit Concepts", priv.getPrivilege());
			Assert.assertEquals("Able to change attributes of existing terms", priv.getDescription());
		}
		{
			Privilege priv = us.getPrivilege("Add People");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add People", priv.getName());
			Assert.assertEquals("Able to add person objects", priv.getDescription());
		}
		// CSV line without name should result in no privilege creation
		{
			Privilege priv = us.getPrivilegeByUuid("cf688144-2700-102b-80cb-0017a47871b2");
			Assert.assertNull(priv);
		}
		// a privilege name cannot be edited
		{
			Privilege priv = us.getPrivilegeByUuid("cf687ee2-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add Test Privilege", priv.getName());
			Assert.assertEquals("Add Test Privilege", priv.getPrivilege());
		}
	}
}

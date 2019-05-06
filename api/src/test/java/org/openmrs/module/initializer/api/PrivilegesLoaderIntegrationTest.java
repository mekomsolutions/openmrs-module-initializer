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
		
		// privilege to be edited
		{
			Privilege priv = new Privilege();
			priv.setPrivilege("Add People");
			priv.setDescription("Able to add folks.");
			us.savePrivilege(priv);
		}
		// privilege to be changed it's Uuid.
		{
			Privilege priv = new Privilege();
			priv.setPrivilege("Add Reports");
			priv.setDescription("Able to add new reports.");
			priv.setUuid("cf68a296-2700-102b-80cb-0017a47871b2");
			us.savePrivilege(priv);
		}
		
	}
	
	@Test
	public void load_shouldLoadPrivilegesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created privilege
		{
			Privilege priv = us.getPrivilege("Add Patient");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add Patient", priv.getName());
			Assert.assertEquals(priv.getName(), priv.getPrivilege());
			Assert.assertEquals("Able to add patients.", priv.getDescription());
		}
		// edited privilege
		{
			Privilege priv = us.getPrivilege("Add People");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add People", priv.getName());
			Assert.assertNotEquals("", priv.getUuid());
			Assert.assertEquals(priv.getName(), priv.getPrivilege());
			Assert.assertEquals("Able to add people.", priv.getDescription());
		}
		{
			Privilege priv = us.getPrivilege("Add Users");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Add Users", priv.getName());
			Assert.assertEquals("cf68a296-2700-102b-80cb-0017a47871b2", priv.getUuid());
			Assert.assertEquals(priv.getName(), priv.getPrivilege());
			Assert.assertEquals("Able to add users.", priv.getDescription());
		}
		{
			Privilege priv = us.getPrivilege("Add Reports");
			Assert.assertNotNull(priv);
			Assert.assertEquals("Able to add new reports.", priv.getDescription());
		}
	}
}

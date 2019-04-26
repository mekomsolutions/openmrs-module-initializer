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
			Assert.assertEquals(priv.getName(), priv.getPrivilege());
			Assert.assertEquals("Able to add people.", priv.getDescription());
		}
	}
}

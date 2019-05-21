package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.privileges.PrivilegesLoader;
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
		
		// privileges to be edited
		{
			Privilege p = new Privilege();
			p.setPrivilege("Add Apples");
			p.setDescription("Able to add fruits.");
			us.savePrivilege(p);
		}
		{
			Privilege p = new Privilege();
			p.setPrivilege("Add Reports");
			p.setDescription("Able to add analytic reports.");
			p.setUuid("cf68a296-2700-102b-80cb-0017a47871b2");
			us.savePrivilege(p);
		}
		{
			Privilege p = new Privilege();
			p.setPrivilege("Add Hens");
			p.setDescription("Able to add poultry.");
			p.setUuid("36404041-c255-4c5b-9b47-0d757d2afa95");
			us.savePrivilege(p);
		}
	}
	
	@Test
	public void load_shouldLoadPrivilegesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// privilege created
		{
			Privilege p = us.getPrivilege("Add People");
			Assert.assertNotNull(p);
			Assert.assertEquals("Add People", p.getName());
			Assert.assertEquals(p.getName(), p.getPrivilege());
			Assert.assertEquals("Able to add people.", p.getDescription());
		}
		// privilege description edited - using name as primary id
		{
			Privilege p = us.getPrivilege("Add Apples");
			Assert.assertNotNull(p);
			Assert.assertEquals(p.getName(), p.getPrivilege());
			Assert.assertEquals("Able to add apples.", p.getDescription());
		}
		// privilege names can't be changed
		{
			Privilege p = us.getPrivilegeByUuid("36404041-c255-4c5b-9b47-0d757d2afa95");
			Assert.assertNotNull(p);
			Assert.assertNotEquals("Add Chickens", p.getName());
			Assert.assertEquals("Add Hens", p.getName());
			Assert.assertEquals(p.getName(), p.getPrivilege());
			Assert.assertEquals("Able to add poultry.", p.getDescription());
		}
		// privilege description edited - using UUID as primary id
		{
			Privilege p = us.getPrivilegeByUuid("cf68a296-2700-102b-80cb-0017a47871b2");
			Assert.assertNotNull(p);
			Assert.assertEquals("Add Reports", p.getName());
			Assert.assertEquals(p.getName(), p.getPrivilege());
			Assert.assertEquals("Able to add reports.", p.getDescription());
		}
	}
}

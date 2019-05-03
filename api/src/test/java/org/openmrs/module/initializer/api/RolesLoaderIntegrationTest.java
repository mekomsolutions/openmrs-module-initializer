package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.RolesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

public class RolesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	@Autowired
	private RolesLoader loader;
	
	@Before
	public void setup() {
		
		// Privileges to be used for assigning to roles.
		{
			Privilege priv1 = new Privilege();
			priv1.setPrivilege("Add Allergies");
			us.savePrivilege(priv1);
			
			Privilege priv2 = new Privilege();
			priv2.setPrivilege("Add Patient");
			us.savePrivilege(priv2);
			
			Privilege priv3 = new Privilege();
			priv3.setPrivilege("Add Orders");
			us.savePrivilege(priv3);
			
			Privilege priv4 = new Privilege();
			priv4.setPrivilege("Add Users");
			us.savePrivilege(priv4);
		}
		
		// Roles to be used as parent roles.
		{
			Role role1 = new Role();
			role1.setRole("Application: Records Allergies");
			role1.setDescription("role 1");
			us.saveRole(role1);
			
			Role role2 = new Role();
			role2.setRole("Application: Uses Patient Summary");
			role2.setDescription("role 2");
			us.saveRole(role2);
			
			Role role3 = new Role();
			role3.setRole("Application: Sees Appointment Schedule");
			us.saveRole(role3);
		}
		
		// role to be edited
		{
			Set<Role> roles = new HashSet<Role>();
			roles.add(us.getRole("Application: Records Allergies"));
			roles.add(us.getRole("Application: Uses Patient Summary"));
			
			Set<Privilege> privileges = new HashSet<Privilege>();
			privileges.add(us.getPrivilege("Add Allergies"));
			privileges.add(us.getPrivilege("Add Patient"));
			
			Role role = new Role();
			role.setRole("Organizational: Nurse");
			role.setUuid("d2fcb33e-2700-102b-80cb-0017a47871b2");
			role.setInheritedRoles(roles);
			role.setPrivileges(privileges);
			us.saveRole(role);
		}
	}
	
	@Test
	public void load_shouldLoadRolesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// created role
		{
			Set<Role> roles = new HashSet<Role>();
			roles.add(us.getRole("Application: Records Allergies"));
			roles.add(us.getRole("Application: Uses Patient Summary"));
			
			Set<Privilege> privileges = new HashSet<Privilege>();
			privileges.add(us.getPrivilege("Add Allergies"));
			privileges.add(us.getPrivilege("Add Patient"));
			
			Role role = us.getRole("Organizational: Doctor");
			Assert.assertNotNull(role);
			Assert.assertEquals("Organizational: Doctor", role.getName());
			Assert.assertEquals("Doctor role", role.getDescription());
			Assert.assertEquals(roles, role.getAllParentRoles());
			Assert.assertEquals(privileges, role.getPrivileges());
		}
		// edited role
		{
			Set<Role> roles = new HashSet<Role>();
			roles.add(us.getRole("Application: Sees Appointment Schedule"));
			
			Set<Privilege> privileges = new HashSet<Privilege>();
			privileges.add(us.getPrivilege("Add Orders"));
			privileges.add(us.getPrivilege("Add Users"));
			
			Role role = us.getRole("Organizational: Nurse");
			Assert.assertNotNull(role);
			Assert.assertEquals("Organizational: Nurse", role.getName());
			Assert.assertEquals("Nurse role", role.getDescription());
			Assert.assertEquals(roles, role.getAllParentRoles());
			Assert.assertEquals(privileges, role.getPrivileges());
		}
	}
}

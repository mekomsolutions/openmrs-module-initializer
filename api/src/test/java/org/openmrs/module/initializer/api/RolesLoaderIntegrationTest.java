package org.openmrs.module.initializer.api;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.roles.RolesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RolesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("userService")
	private UserService us;
	
	@Autowired
	private RolesLoader loader;
	
	@Before
	public void setup() {
		
		// A couple of privileges to use with roles
		us.savePrivilege(new Privilege("Add Allergies"));
		us.savePrivilege(new Privilege("Add Patient"));
		us.savePrivilege(new Privilege("Add Orders"));
		us.savePrivilege(new Privilege("Add Users"));
		{
			Privilege priv = new Privilege();
			priv.setUuid("cf688946-2700-102b-80cb-0017a47871b2");
			priv.setPrivilege("Add Forms");
			us.savePrivilege(priv);
		}
		
		// A couple of roles to be used as parent/inherited roles
		us.saveRole(new Role("Application: Records Allergies"));
		us.saveRole(new Role("Application: Uses Patient Summary"));
		us.saveRole(new Role("Application: Sees Appointment Schedule"));
		{
			Role role = new Role();
			role.setUuid("d2fcc9fa-2700-102b-80cb-0017a47871b2");
			role.setRole("Social Worker");
			us.saveRole(role);
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
			// setup
			Set<Role> roles = new HashSet<Role>();
			roles.add(us.getRole("Application: Records Allergies"));
			roles.add(us.getRole("Application: Uses Patient Summary"));
			
			Set<Privilege> privileges = new HashSet<Privilege>();
			privileges.add(us.getPrivilege("Add Allergies"));
			privileges.add(us.getPrivilege("Add Patient"));
			privileges.add(us.getPrivilege("Add Forms"));
			
			// replay
			Role role = us.getRoleByUuid("d2fcb604-2700-102b-80cb-0017a47871b2");
			
			// verif
			Assert.assertNotNull(role);
			Assert.assertEquals("Organizational: Doctor", role.getName());
			Assert.assertEquals("Doctor role", role.getDescription());
			Assert.assertEquals(roles, role.getAllParentRoles());
			Assert.assertEquals(privileges, role.getPrivileges());
		}
		
		// edited role
		{
			// setup
			Set<Role> roles = new HashSet<Role>();
			roles.add(us.getRole("Application: Sees Appointment Schedule"));
			roles.add(us.getRole("Social Worker")); // was provided by UUID in the CSV
			
			Set<Privilege> privileges = new HashSet<Privilege>();
			privileges.add(us.getPrivilege("Add Orders"));
			privileges.add(us.getPrivilege("Add Users"));
			
			// replay
			Role role = us.getRole("Organizational: Nurse");
			
			// verif
			Assert.assertNotNull(role);
			Assert.assertEquals("Organizational: Nurse", role.getName());
			Assert.assertEquals("Nurse role", role.getDescription());
			Assert.assertEquals(roles, role.getAllParentRoles());
			Assert.assertEquals(privileges, role.getPrivileges());
		}
	}
}

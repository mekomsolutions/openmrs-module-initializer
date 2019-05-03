package org.openmrs.module.initializer.api.roles;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

public class RoleLineProcessor extends BaseLineProcessor<Role, UserService> {
	
	protected static String HEADER_ROLE_NAME = "role name";
	
	protected static String HEADER_INHERITED_ROLES = "inherited roles";
	
	protected static String HEADER_PRIVILEGES = "privileges";
	
	public RoleLineProcessor(String[] headerLine, UserService us) {
		super(headerLine, us);
	}
	
	@Override
	protected Role bootstrap(CsvLine line) throws IllegalArgumentException {
		String roleName = line.get(HEADER_ROLE_NAME, true);
		
		if (roleName == null) {
			throw new IllegalArgumentException("A role must at least be provided a role name: '" + line.toString() + "'");
		}
		
		Role role = service.getRole(roleName);
		if (role == null) {
			role = new Role();
		}
		return role;
	}
	
	@Override
	protected Role fill(Role role, CsvLine line) throws IllegalArgumentException {
		role.setRole(line.get(HEADER_ROLE_NAME, true));
		role.setName(role.getRole());
		role.setDescription(line.get(HEADER_DESC));
		role.setInheritedRoles(parseRoleList(line.get(HEADER_INHERITED_ROLES), service));
		role.setPrivileges(parsePrivilegeList(line.get(HEADER_PRIVILEGES), service));
		return role;
	}
	
	protected static Set<Role> parseRoleList(String roleList, UserService us) throws IllegalArgumentException {
		
		Set<Role> roles = new HashSet<Role>();
		
		String[] parts = roleList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String id : parts) {
			id = id.trim();
			Role r = us.getRole(id);
			if (r == null) {
				throw new IllegalArgumentException("The role identified by '" + id + "' could not be found in database.");
			}
			roles.add(r);
		}
		return roles;
	}
	
	protected static Set<Privilege> parsePrivilegeList(String privilegeList, UserService us)
	        throws IllegalArgumentException {
		
		Set<Privilege> privileges = new HashSet<Privilege>();
		
		String[] parts = privilegeList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String id : parts) {
			id = id.trim();
			Privilege priv = us.getPrivilege(id);
			if (priv == null) {
				throw new IllegalArgumentException(
				        "The privilege identified by '" + id + "' could not be found in database.");
			}
			privileges.add(priv);
		}
		return privileges;
	}
}

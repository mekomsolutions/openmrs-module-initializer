package org.openmrs.module.initializer.api.roles;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

import java.util.HashSet;
import java.util.Set;

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
		role.setInheritedRoles(parseRoleList(role, line.get(HEADER_INHERITED_ROLES), service));
		role.setPrivileges(parsePrivilegesList(role, line.get(HEADER_PRIVILEGES), service));
		return role;
	}
	
	// get roles list from input string
	protected static Set<Role> parseRoleList(Role role, String roleList, UserService us) throws IllegalArgumentException {
		
		Set<Role> roles = role.getInheritedRoles();
		
		if (roles == null) {
			roles = new HashSet<Role>();
		}
		
		String[] roleStrings = roleList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String roleStr : roleStrings) {
			roleStr = roleStr.trim();
			Role rol = us.getRole(roleStr);
			if (rol == null) {
				throw new IllegalArgumentException("No Role could be fetched from '" + roleStr + "' string");
			}
			roles.add(rol);
		}
		return roles;
	}
	
	// get privileges list from input String
	protected static Set<Privilege> parsePrivilegesList(Role role, String privilegeList, UserService us)
	        throws IllegalArgumentException {
		
		Set<Privilege> privileges = role.getPrivileges();
		
		if (privileges == null) {
			privileges = new HashSet<Privilege>();
		}
		
		String[] privilegeStrings = privilegeList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String privStr : privilegeStrings) {
			privStr = privStr.trim();
			Privilege priv = us.getPrivilege(privStr);
			if (priv == null) {
				throw new IllegalArgumentException("No Privilege could be fetched from '" + privStr + "' string");
			}
			privileges.add(priv);
		}
		return privileges;
	}
}

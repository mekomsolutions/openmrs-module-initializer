package org.openmrs.module.initializer.api.roles;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.Utils;

public class RoleLineProcessor extends BaseLineProcessor<Role, UserService> {
	
	protected static String HEADER_ROLE_NAME = "role name";
	
	protected static String HEADER_INHERITED_ROLES = "inherited roles";
	
	protected static String HEADER_PRIVILEGES = "privileges";
	
	public RoleLineProcessor(String[] headerLine, UserService us) {
		super(headerLine, us);
	}
	
	@Override
	protected Role bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		String roleName = line.get(HEADER_ROLE_NAME, true);
		
		Role role = service.getRoleByUuid(uuid);
		if (role != null && !role.getRole().equals(roleName)) {
			throw new IllegalArgumentException("A role name cannot be edited.");
		}
		
		if (role == null) {
			if (!StringUtils.isEmpty(roleName)) {
				role = service.getRole(roleName);
			}
		}
		
		if (role == null) {
			role = new Role();
			if (!StringUtils.isEmpty(uuid)) {
				role.setUuid(uuid);
			}
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
			Role r = Utils.fetchRole(id, us);
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
			Privilege priv = Utils.fetchPrivilege(id, us);
			if (priv == null) {
				throw new IllegalArgumentException(
				        "The privilege identified by '" + id + "' could not be found in database.");
			}
			privileges.add(priv);
		}
		return privileges;
	}
}

package org.openmrs.module.initializer.api.roles;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.PrivilegeListParser;
import org.openmrs.module.initializer.api.utils.RoleListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RoleLineProcessor extends BaseLineProcessor<Role> {
	
	protected static String HEADER_ROLE_NAME = "role name";
	
	protected static String HEADER_INHERITED_ROLES = "inherited roles";
	
	protected static String HEADER_PRIVILEGES = "privileges";
	
	private UserService userService;
	
	private PrivilegeListParser pListParser;
	
	private RoleListParser rListParser;
	
	@Autowired
	public RoleLineProcessor(@Qualifier("userService") UserService userService, PrivilegeListParser privilegeListParser,
	    RoleListParser roleListParser) {
		this.userService = userService;
		this.pListParser = privilegeListParser;
		this.rListParser = roleListParser;
	}
	
	@Override
	protected Role bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		String roleName = line.get(HEADER_ROLE_NAME, true);
		
		Role role = userService.getRoleByUuid(uuid);
		if (role != null && !role.getRole().equals(roleName)) {
			throw new IllegalArgumentException("A role name cannot be edited.");
		}
		
		if (role == null) {
			if (!StringUtils.isEmpty(roleName)) {
				role = userService.getRole(roleName);
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
		role.setInheritedRoles(new HashSet<Role>(rListParser.parseList(line.get(HEADER_INHERITED_ROLES))));
		role.setPrivileges(new HashSet<Privilege>(pListParser.parseList(line.get(HEADER_PRIVILEGES))));
		return role;
	}
}

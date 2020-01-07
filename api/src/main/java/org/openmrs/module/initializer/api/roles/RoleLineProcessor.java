package org.openmrs.module.initializer.api.roles;

import java.util.HashSet;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.PrivilegeListParser;
import org.openmrs.module.initializer.api.utils.RoleListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleLineProcessor extends BaseLineProcessor<Role> {
	
	protected static String HEADER_ROLE_NAME = "role name";
	
	protected static String HEADER_INHERITED_ROLES = "inherited roles";
	
	protected static String HEADER_PRIVILEGES = "privileges";
	
	private PrivilegeListParser privilegeListParser;
	
	private RoleListParser roleListParser;
	
	@Autowired
	public RoleLineProcessor(PrivilegeListParser privilegeListParser, RoleListParser roleListParser) {
		this.privilegeListParser = privilegeListParser;
		this.roleListParser = roleListParser;
	}
	
	@Override
	public Role fill(Role role, CsvLine line) throws IllegalArgumentException {
		role.setRole(line.get(HEADER_ROLE_NAME, true));
		role.setName(role.getRole());
		role.setDescription(line.get(HEADER_DESC));
		role.setInheritedRoles(new HashSet<Role>(roleListParser.parseList(line.get(HEADER_INHERITED_ROLES))));
		role.setPrivileges(new HashSet<Privilege>(privilegeListParser.parseList(line.get(HEADER_PRIVILEGES))));
		return role;
	}
}

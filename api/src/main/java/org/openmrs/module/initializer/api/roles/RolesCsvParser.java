package org.openmrs.module.initializer.api.roles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RolesCsvParser extends CsvParser<Role, BaseLineProcessor<Role>> {
	
	private UserService userService;
	
	@Autowired
	public RolesCsvParser(@Qualifier("userService") UserService userService, RoleLineProcessor processor) {
		super(processor);
		this.userService = userService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ROLES;
	}
	
	@Override
	public Role bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String roleName = line.get(RoleLineProcessor.HEADER_ROLE_NAME, true);
		
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
	public Role save(Role instance) {
		return userService.saveRole(instance);
	}
}

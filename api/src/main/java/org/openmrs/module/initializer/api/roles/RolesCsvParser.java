package org.openmrs.module.initializer.api.roles;

import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
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
	protected Role save(Role instance) {
		return userService.saveRole(instance);
	}
}

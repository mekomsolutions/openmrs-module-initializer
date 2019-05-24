package org.openmrs.module.initializer.api.privileges;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesCsvParser extends CsvParser<Privilege, BaseLineProcessor<Privilege>> {
	
	private UserService userService;
	
	@Autowired
	public PrivilegesCsvParser(@Qualifier("userService") UserService userService, PrivilegeLineProcessor processor) {
		super(processor);
		this.userService = userService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PRIVILEGES;
	}
	
	@Override
	protected Privilege save(Privilege instance) {
		return userService.savePrivilege(instance);
	}
}

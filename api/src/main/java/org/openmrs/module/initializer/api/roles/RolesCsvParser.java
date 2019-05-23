package org.openmrs.module.initializer.api.roles;

import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.PrivilegeListParser;
import org.openmrs.module.initializer.api.utils.RoleListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RolesCsvParser extends CsvParser<Role, UserService, RoleLineProcessor> {
	
	@Autowired
	public RolesCsvParser(@Qualifier("userService") UserService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ROLES;
	}
	
	@Override
	protected Role save(Role instance) {
		return service.saveRole(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Role instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(
		    new RoleLineProcessor(headerLine, service, new PrivilegeListParser(service), new RoleListParser(service)));
	}
	
	@Override
	protected boolean isSaved(Role instance) {
		return instance.getRole() != null;
	}
}

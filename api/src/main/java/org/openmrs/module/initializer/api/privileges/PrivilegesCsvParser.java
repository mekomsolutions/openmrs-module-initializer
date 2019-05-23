package org.openmrs.module.initializer.api.privileges;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesCsvParser extends CsvParser<Privilege, UserService, org.openmrs.module.initializer.api.privileges.PrivilegeLineProcessor> {
	
	@Autowired
	public PrivilegesCsvParser(@Qualifier("userService") UserService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PRIVILEGES;
	}
	
	@Override
	protected Privilege save(Privilege instance) {
		return service.savePrivilege(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Privilege instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new PrivilegeLineProcessor(headerLine, service));
	}
	
	@Override
	protected boolean isSaved(Privilege instance) {
		return instance.getPrivilege() != null;
	}
}

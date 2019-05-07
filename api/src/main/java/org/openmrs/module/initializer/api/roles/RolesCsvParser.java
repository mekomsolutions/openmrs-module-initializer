package org.openmrs.module.initializer.api.roles;

import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.api.CsvParser;

import java.io.IOException;
import java.io.InputStream;

public class RolesCsvParser extends CsvParser<Role, UserService, RoleLineProcessor> {
	
	public RolesCsvParser(InputStream is, UserService us) throws IOException {
		super(is, us);
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
		addLineProcessor(new RoleLineProcessor(headerLine, service));
	}
	
	@Override
	protected boolean isSaved(Role instance) {
		return instance.getRole() != null;
	}
}

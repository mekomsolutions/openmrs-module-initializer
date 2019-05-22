package org.openmrs.module.initializer.api.privileges;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;

public class PrivilegesCsvParser extends CsvParser<Privilege, UserService, org.openmrs.module.initializer.api.privileges.PrivilegeLineProcessor> {
	
	public PrivilegesCsvParser(InputStream is, UserService us) throws IOException {
		super(is, us);
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
		addLineProcessor(new PrivilegeLineProcessor(headerLine, service));
	}
	
	@Override
	protected boolean isSaved(Privilege instance) {
		return instance.getPrivilege() != null;
	}
}

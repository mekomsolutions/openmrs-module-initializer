package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.UserService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.privileges.PrivilegesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.PRIVILEGES;
	}
	
	@Autowired
	@Qualifier("userService")
	private UserService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new PrivilegesCsvParser(is, service);
	}
}

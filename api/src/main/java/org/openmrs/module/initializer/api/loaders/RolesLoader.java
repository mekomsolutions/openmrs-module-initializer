package org.openmrs.module.initializer.api.loaders;

import org.openmrs.api.UserService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.roles.RolesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class RolesLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("userService")
	private UserService service;
	
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new RolesCsvParser(is, service);
	}
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_ROLE;
	}
	
	@Override
	public Integer getOrder() {
		return 4;
	}
}

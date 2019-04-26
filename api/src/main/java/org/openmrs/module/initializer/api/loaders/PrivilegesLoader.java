package org.openmrs.module.initializer.api.loaders;

import org.openmrs.api.UserService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.privileges.PrivilegesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;

@Component
public class PrivilegesLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("userService")
	private UserService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_PRIV;
	}
	
	@Override
	public Integer getOrder() {
		return 3;
	}
	
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new PrivilegesCsvParser(is, service);
	}
}

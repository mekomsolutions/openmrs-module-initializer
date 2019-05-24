package org.openmrs.module.initializer.api.privileges;

import org.openmrs.Privilege;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesLoader extends BaseCsvLoader<Privilege, PrivilegesCsvParser> {
	
	@Autowired
	public void setParser(PrivilegesCsvParser parser) {
		this.parser = parser;
	}
}

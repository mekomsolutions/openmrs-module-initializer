package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.privileges.PrivilegesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrivilegesLoader extends BaseCsvLoader<PrivilegesCsvParser> {
	
	@Autowired
	public void setParser(PrivilegesCsvParser parser) {
		this.parser = parser;
	}
}

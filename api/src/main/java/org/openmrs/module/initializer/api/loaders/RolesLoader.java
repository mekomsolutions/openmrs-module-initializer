package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.roles.RolesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RolesLoader extends BaseCsvLoader<RolesCsvParser> {
	
	@Autowired
	public void setParser(RolesCsvParser parser) {
		this.parser = parser;
	}
}

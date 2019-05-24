package org.openmrs.module.initializer.api.roles;

import org.openmrs.Role;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RolesLoader extends BaseCsvLoader<Role, RolesCsvParser> {
	
	@Autowired
	public void setParser(RolesCsvParser parser) {
		this.parser = parser;
	}
}

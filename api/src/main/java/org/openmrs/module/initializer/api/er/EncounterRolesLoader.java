package org.openmrs.module.initializer.api.er;

import org.openmrs.EncounterRole;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterRolesLoader extends BaseCsvLoader<EncounterRole, EncounterRolesCsvParser> {
	
	@Autowired
	public void setParser(EncounterRolesCsvParser parser) {
		this.parser = parser;
	}
}

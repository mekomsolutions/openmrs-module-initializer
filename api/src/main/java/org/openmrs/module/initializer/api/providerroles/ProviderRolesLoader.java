package org.openmrs.module.initializer.api.providerroles;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderRolesLoader extends BaseCsvLoader<OpenmrsMetadata, ProviderRolesCsvParser> {
	
	@Autowired
	public void setParser(ProviderRolesCsvParser parser) {
		this.parser = parser;
	}
}

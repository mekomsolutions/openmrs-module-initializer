package org.openmrs.module.initializer.api.providerroles;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.providermanagement.ProviderRole;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "providermanagement:*" })
public class ProviderRolesLoader extends BaseCsvLoader<ProviderRole, ProviderRolesCsvParser> {
	
	@Autowired
	public void setParser(ProviderRolesCsvParser parser) {
		this.parser = parser;
	}
}

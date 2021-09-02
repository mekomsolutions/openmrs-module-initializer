package org.openmrs.module.initializer.api.providerroles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "providermanagement:*" })
public class ProviderRolesCsvParser extends CsvParser<ProviderRole, BaseLineProcessor<ProviderRole>> {
	
	private ProviderManagementService providerManagementService;
	
	@Autowired
	public ProviderRolesCsvParser(ProviderManagementService pms, ProviderRolesLineProcessor processor) {
		super(processor);
		this.providerManagementService = pms;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROVIDER_ROLES;
	}
	
	@Override
	public ProviderRole bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalArgumentException("uuid is required for provider roles");
		}
		ProviderRole role = providerManagementService.getProviderRoleByUuid(uuid);
		if (role == null) {
			role = new ProviderRole();
			role.setUuid(uuid);
		}
		return role;
	}
	
	@Override
	public ProviderRole save(ProviderRole instance) {
		return providerManagementService.saveProviderRole(instance);
	}
}

package org.openmrs.module.initializer.api.providerroles;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderRolesCsvParser extends CsvParser<OpenmrsMetadata, BaseLineProcessor<OpenmrsMetadata>> {
	
	private final ProviderRoleServiceAdapter service = new ProviderRoleServiceAdapter();
	
	@Autowired
	public ProviderRolesCsvParser(ProviderRolesLineProcessor processor) {
		super(processor);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PROVIDER_ROLES;
	}
	
	@Override
	public OpenmrsMetadata bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		if (StringUtils.isEmpty(uuid)) {
			throw new IllegalArgumentException("uuid is required for provider roles");
		}
		OpenmrsMetadata role = service.getProviderRoleByUuid(uuid);
		if (role == null) {
			ProviderRoleAdapter roleAdapter = service.newProviderRoleAdapter();
			role = roleAdapter.getProviderRole();
			role.setUuid(uuid);
		}
		return role;
	}
	
	@Override
	public OpenmrsMetadata save(OpenmrsMetadata instance) {
		ProviderRoleAdapter roleAdapter = new ProviderRoleAdapter(instance);
		service.saveProviderRole(roleAdapter);
		return roleAdapter.getProviderRole();
	}
}

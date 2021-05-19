package org.openmrs.module.initializer.api.er;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.EncounterRole;
import org.openmrs.api.EncounterService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EncounterRolesCsvParser extends CsvParser<EncounterRole, BaseLineProcessor<EncounterRole>> {
	
	private EncounterService encounterService;
	
	@Autowired
	public EncounterRolesCsvParser(@Qualifier("encounterService") EncounterService encounterService,
	    EncounterRoleLineProcessor processor) {
		super(processor);
		this.encounterService = encounterService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ENCOUNTER_ROLES;
	}
	
	@Override
	public EncounterRole bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		EncounterRole type = encounterService.getEncounterRoleByUuid(uuid);
		if (type == null) {
			type = encounterService.getEncounterRoleByName(line.getName(true));
		}
		if (type == null) {
			type = new EncounterRole();
			if (!StringUtils.isEmpty(uuid)) {
				type.setUuid(uuid);
			}
		}
		
		return type;
	}
	
	@Override
	public EncounterRole save(EncounterRole instance) {
		return encounterService.saveEncounterRole(instance);
	}
}

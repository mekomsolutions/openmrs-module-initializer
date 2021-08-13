package org.openmrs.module.initializer.api.er;

import org.openmrs.EncounterRole;
import org.openmrs.api.EncounterService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EncounterRoleLineProcessor extends BaseLineProcessor<EncounterRole> {
	
	private EncounterService service;
	
	@Autowired
	public EncounterRoleLineProcessor(@Qualifier("encounterService") EncounterService encounterService) {
		super();
		this.service = encounterService;
	}
	
	@Override
	public EncounterRole fill(EncounterRole type, CsvLine line) throws IllegalArgumentException {
		
		type.setName(line.getName(true));
		type.setDescription(line.get(HEADER_DESC));
		
		return type;
	}
}

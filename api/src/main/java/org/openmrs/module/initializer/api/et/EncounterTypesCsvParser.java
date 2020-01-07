package org.openmrs.module.initializer.api.et;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EncounterTypesCsvParser extends CsvParser<EncounterType, BaseLineProcessor<EncounterType>> {
	
	private EncounterService encounterService;
	
	@Autowired
	public EncounterTypesCsvParser(@Qualifier("encounterService") EncounterService encounterService,
	    EncounterTypeLineProcessor processor) {
		super(processor);
		this.encounterService = encounterService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ENCOUNTER_TYPES;
	}
	
	@Override
	public EncounterType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		EncounterType type = encounterService.getEncounterTypeByUuid(uuid);
		if (type == null) {
			type = encounterService.getEncounterType(line.getName(true));
		}
		if (type == null) {
			type = new EncounterType();
			if (!StringUtils.isEmpty(uuid)) {
				type.setUuid(uuid);
			}
		}
		
		return type;
	}
	
	@Override
	public EncounterType save(EncounterType instance) {
		return encounterService.saveEncounterType(instance);
	}
}

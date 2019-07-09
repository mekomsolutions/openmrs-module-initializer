package org.openmrs.module.initializer.api.et;

import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
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
	protected EncounterType save(EncounterType instance) {
		return encounterService.saveEncounterType(instance);
	}
}

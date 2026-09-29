package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptMapTypesCsvParser extends CsvParser<ConceptMapType, BaseLineProcessor<ConceptMapType>> {
	
	private ConceptService service;
	
	@Autowired
	public ConceptMapTypesCsvParser(@Qualifier("conceptService") ConceptService service,
	    ConceptMapTypeLineProcessor processor) {
		super(processor);
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_MAP_TYPES;
	}
	
	@Override
	public ConceptMapType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		ConceptMapType mapType = service.getConceptMapTypeByUuid(uuid);
		if (mapType == null) {
			mapType = service.getConceptMapTypeByName(line.getName(true));
		}
		if (mapType == null) {
			mapType = new ConceptMapType();
			if (!StringUtils.isEmpty(uuid)) {
				mapType.setUuid(uuid);
			}
		}
		
		return mapType;
	}
	
	@Override
	public ConceptMapType save(ConceptMapType instance) {
		return service.saveConceptMapType(instance);
	}
}

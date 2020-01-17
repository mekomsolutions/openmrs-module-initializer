package org.openmrs.module.initializer.api.c_class;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptClass;
import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassCsvParser extends CsvParser<ConceptClass, BaseLineProcessor<ConceptClass>> {
	
	private ConceptService service;
	
	@Autowired
	public ConceptClassCsvParser(@Qualifier("conceptService") ConceptService service, ConceptClassLineProcessor processor) {
		super(processor);
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_CLASSES;
	}
	
	@Override
	public ConceptClass bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		ConceptClass c_class = service.getConceptClassByUuid(uuid);
		if (c_class == null) {
			c_class = service.getConceptClassByName(line.getName(true));
		}
		if (c_class == null) {
			c_class = new ConceptClass();
			if (!StringUtils.isEmpty(uuid)) {
				c_class.setUuid(uuid);
			}
		}
		
		return c_class;
	}
	
	@Override
	public ConceptClass save(ConceptClass instance) {
		return service.saveConceptClass(instance);
	}
}

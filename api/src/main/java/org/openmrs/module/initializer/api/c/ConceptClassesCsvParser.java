package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassesCsvParser extends CsvParser<ConceptClass, BaseLineProcessor<ConceptClass>> {
	
	private ConceptService service;
	
	@Autowired
	public ConceptClassesCsvParser(@Qualifier("conceptService") ConceptService service,
	    ConceptClassLineProcessor processor) {
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
		
		ConceptClass conceptClass = service.getConceptClassByUuid(uuid);
		if (conceptClass == null) {
			conceptClass = service.getConceptClassByName(line.getName(true));
		}
		if (conceptClass == null) {
			conceptClass = new ConceptClass();
			if (!StringUtils.isEmpty(uuid)) {
				conceptClass.setUuid(uuid);
			}
		}
		
		return conceptClass;
	}
	
	@Override
	public ConceptClass save(ConceptClass instance) {
		return service.saveConceptClass(instance);
	}
}

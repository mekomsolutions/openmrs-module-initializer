package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptSourcesCsvParser extends CsvParser<ConceptSource, BaseLineProcessor<ConceptSource>> {
	
	private ConceptService service;
	
	@Autowired
	public ConceptSourcesCsvParser(@Qualifier("conceptService") ConceptService service,
	    ConceptSourceLineProcessor processor) {
		super(processor);
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_SOURCES;
	}
	
	@Override
	public ConceptSource bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		ConceptSource conceptSource = service.getConceptSourceByUuid(uuid);
		if (conceptSource == null) {
			conceptSource = service.getConceptSourceByName(line.getName(true));
		}
		if (conceptSource == null) {
			conceptSource = new ConceptSource();
			if (!StringUtils.isEmpty(uuid)) {
				conceptSource.setUuid(uuid);
			}
		}
		
		return conceptSource;
	}
	
	@Override
	public ConceptSource save(ConceptSource instance) {
		return service.saveConceptSource(instance);
	}
}

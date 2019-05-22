package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.ConceptListParser;
import org.openmrs.module.initializer.api.utils.ConceptMapListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptsCsvParser extends CsvParser<Concept, ConceptService, BaseLineProcessor<Concept, ConceptService>> {
	
	@Autowired
	public ConceptsCsvParser(@Qualifier("conceptService") ConceptService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPTS;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.clear();
		lineProcessors.add(new ConceptNumericLineProcessor(headerLine, service));
		lineProcessors.add(new ConceptComplexLineProcessor(headerLine, service));
		lineProcessors.add(new BaseConceptLineProcessor(headerLine, service));
		lineProcessors.add(new NestedConceptLineProcessor(headerLine, service, new ConceptListParser(service)));
		lineProcessors.add(new MappingsConceptLineProcessor(headerLine, service, new ConceptMapListParser(service)));
	}
	
	@Override
	protected Concept save(Concept instance) {
		return service.saveConcept(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Concept instance) {
		return instance.isRetired();
	}
}

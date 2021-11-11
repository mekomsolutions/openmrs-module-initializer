package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptSetsCsvParser extends CsvParser<Concept, BaseLineProcessor<Concept>> {
	
	private final ConceptService conceptService;
	
	@Autowired
	public ConceptSetsCsvParser(@Qualifier("conceptService") ConceptService conceptService,
	    @Qualifier("initializer.conceptSetLineProcessor") ConceptSetLineProcessor processor) {
		
		super(processor);
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_SETS;
	}
	
	@Override
	public Concept bootstrap(CsvLine line) throws IllegalArgumentException {
		String conceptLookup = line.get(ConceptSetLineProcessor.HEADER_CONCEPT, true);
		Concept concept = Utils.fetchConcept(conceptLookup, conceptService);
		if (concept == null) {
			throw new IllegalArgumentException("No concept found with identifier: " + conceptLookup);
		}
		return concept;
	}
	
	@Override
	public Concept save(Concept instance) {
		return conceptService.saveConcept(instance);
	}
}

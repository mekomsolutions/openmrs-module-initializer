package org.openmrs.module.initializer.api.utils;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptListParser extends ListParser<Concept> {
	
	private ConceptService conceptService;
	
	@Autowired
	public ConceptListParser(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	protected Concept fetch(String id) {
		return Utils.fetchConcept(id, conceptService);
	}
	
}

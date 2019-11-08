package org.openmrs.module.initializer.api.utils;

import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassListParser extends ListParser<ConceptClass> {
	
	private ConceptService conceptService;
	
	@Autowired
	public ConceptClassListParser(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	protected ConceptClass fetch(String id) {
		return Utils.fetchConceptClass(id, conceptService);
	}
	
}

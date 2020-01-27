package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassLineProcessor extends BaseLineProcessor<ConceptClass> {
	
	private ConceptService service;
	
	@Autowired
	public ConceptClassLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.service = conceptService;
	}
	
	public ConceptClass fill(ConceptClass conceptClass, CsvLine line) throws IllegalArgumentException {
		conceptClass.setName(line.get(HEADER_NAME, true));
		conceptClass.setDescription(line.get(HEADER_DESC));
		return conceptClass;
	}
}

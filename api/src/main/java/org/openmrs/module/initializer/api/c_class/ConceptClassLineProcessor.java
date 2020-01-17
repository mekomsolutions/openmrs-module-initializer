package org.openmrs.module.initializer.api.c_class;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassLineProcessor extends BaseLineProcessor<ConceptClass> {
	
	protected static String HEADER_NAME = "name";
	
	private ConceptService service;
	
	@Autowired
	public ConceptClassLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.service = conceptService;
	}
	
	public ConceptClass fill(ConceptClass c_class, CsvLine line) throws IllegalArgumentException {
		
		c_class.setName(line.get(HEADER_NAME, true));
		
		return c_class;
	}
}

package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptSourceLineProcessor extends BaseLineProcessor<ConceptSource> {
	
	final public static String HEADER_HL7_CODE = "HL7 Code";
	
	final public static String HEADER_UNIQUE_ID = "Unique ID";
	
	private ConceptService service;
	
	@Autowired
	public ConceptSourceLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.service = conceptService;
	}
	
	public ConceptSource fill(ConceptSource conceptSource, CsvLine line) throws IllegalArgumentException {
		conceptSource.setName(line.get(HEADER_NAME, true));
		conceptSource.setDescription(line.get(HEADER_DESC, true));
		conceptSource.setHl7Code(line.get(HEADER_HL7_CODE));
		conceptSource.setUniqueId(line.get(HEADER_UNIQUE_ID));
		return conceptSource;
	}
}

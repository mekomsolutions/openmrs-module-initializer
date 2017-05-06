package org.openmrs.module.initializer.api.c;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;

public class ConceptsCsvParser extends CsvParser<Concept, ConceptService, BaseConceptLineProcessor> {
	
	public ConceptsCsvParser(InputStream is, ConceptService cs) throws IOException {
		super(is, cs);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		
		addLineProcessor(new BaseConceptLineProcessor(headerLine, service));
		
		if (InitializerConstants.VERSION_NESTED.equals(version)) {
			addLineProcessor(new NestedConceptLineProcessor(headerLine, service));
		}
	}
	
	@Override
	protected Concept save(Concept instance) {
		return service.saveConcept(instance);
	}
}

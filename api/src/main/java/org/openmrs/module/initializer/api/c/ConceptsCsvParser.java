package org.openmrs.module.initializer.api.c;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

public class ConceptsCsvParser extends CsvParser<Concept, ConceptService, BaseLineProcessor<Concept, ConceptService>> {
	
	public ConceptsCsvParser(InputStream is, ConceptService cs) throws IOException {
		super(is, cs);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new ConceptNumericLineProcessor(headerLine, service));
		addLineProcessor(new ConceptComplexLineProcessor(headerLine, service));
		addLineProcessor(new BaseConceptLineProcessor(headerLine, service));
		addLineProcessor(new NestedConceptLineProcessor(headerLine, service));
		addLineProcessor(new MappingsConceptLineProcessor(headerLine, service));
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

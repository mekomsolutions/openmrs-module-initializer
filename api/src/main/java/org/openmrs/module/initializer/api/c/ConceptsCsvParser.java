package org.openmrs.module.initializer.api.c;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;

public class ConceptsCsvParser extends CsvParser<Concept, BaseConceptLineProcessor> {
	
	protected ConceptService cs;
	
	public ConceptsCsvParser(InputStream is, ConceptService cs) throws IOException {
		super(is);
		this.cs = cs;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		
		addLineProcessor(new BaseConceptLineProcessor(headerLine, cs));
		
		if (InitializerConstants.VERSION_CHILDREN.equals(version)) {
			addLineProcessor(new ConceptWithChildrenLineProcessor(headerLine, cs));
		}
	}
	
	@Override
	protected Concept fromCsvLine(String[] line) {
		Concept concept = null;
		for (BaseConceptLineProcessor p : getLineProcessors()) {
			concept = p.getConcept(concept, line, cs);
		}
		return concept;
	}
	
	@Override
	protected Concept save(Concept instance) {
		return cs.saveConcept(instance);
	}
}

package org.openmrs.module.initializer.api.c;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
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
		addLineProcessor(new BaseConceptLineProcessor(headerLine, service));
		addLineProcessor(new NestedConceptLineProcessor(headerLine, service));
		addLineProcessor(new MappingsConceptLineProcessor(headerLine, service));
	}
	
	@Override
	protected Concept save(Concept instance) {
		return service.saveConcept(instance);
	}
	
	@Override
	protected Concept bootstrap(String[] line) throws IllegalArgumentException {
		String uuid = BaseLineProcessor.getUuid(headerLine, line);
		Concept concept = service.getConceptByUuid(uuid);
		if (concept == null) {
			concept = new Concept();
			if (!StringUtils.isEmpty(uuid)) {
				concept.setUuid(uuid);
			}
		}
		
		concept.setRetired(BaseLineProcessor.getVoidOrRetire(headerLine, line));
		
		return concept;
	}
	
	@Override
	protected boolean voidOrRetire(Concept instance) {
		return instance.isRetired();
	}
}

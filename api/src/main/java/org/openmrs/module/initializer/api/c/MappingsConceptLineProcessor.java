package org.openmrs.module.initializer.api.c;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptMapListParser;

public class MappingsConceptLineProcessor extends BaseConceptLineProcessor {
	
	protected static String HEADER_MAPPINGS_SAMEAS = "same as mappings";
	
	private ConceptMapListParser listParser;
	
	public MappingsConceptLineProcessor(String[] headerLine, ConceptService cs, ConceptMapListParser listParser) {
		super(headerLine, cs);
		this.listParser = listParser;
	}
	
	protected Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		if (!CollectionUtils.isEmpty(concept.getConceptMappings())) {
			concept.getConceptMappings().clear();
		}
		String mappingsStr = line.get(HEADER_MAPPINGS_SAMEAS);
		if (!StringUtils.isEmpty(mappingsStr)) {
			for (ConceptMap mapping : listParser.parseList(mappingsStr)) {
				concept.addConceptMapping(mapping);
			}
		}
		
		return concept;
	}
}

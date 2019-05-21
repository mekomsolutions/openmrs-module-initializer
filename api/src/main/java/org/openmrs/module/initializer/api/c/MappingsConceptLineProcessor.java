package org.openmrs.module.initializer.api.c;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.ConceptMapListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.mappingsConceptLineProcessor")
public class MappingsConceptLineProcessor extends ConceptLineProcessor {
	
	protected static String HEADER_MAPPINGS_SAMEAS = "same as mappings";
	
	private ConceptMapListParser listParser;
	
	@Autowired
	public MappingsConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService,
	    ConceptMapListParser listParser) {
		super(conceptService);
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

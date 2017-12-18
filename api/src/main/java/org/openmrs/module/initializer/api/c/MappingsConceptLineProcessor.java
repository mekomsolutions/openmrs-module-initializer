package org.openmrs.module.initializer.api.c;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.impl.Utils;

public class MappingsConceptLineProcessor extends BaseConceptLineProcessor {
	
	protected static String HEADER_MAPPINGS_SAMEAS = "same as mappings";
	
	public MappingsConceptLineProcessor(String[] headerLine, ConceptService cs) {
		super(headerLine, cs);
	}
	
	protected static List<ConceptMap> parseMappingList(String conceptList, ConceptMapType mapType, ConceptService cs)
	        throws IllegalArgumentException {
		
		List<ConceptMap> conceptMappings = new ArrayList<ConceptMap>();
		
		String[] parts = conceptList.split(BaseLineProcessor.LIST_SEPARATOR);
		
		for (String mappingStr : parts) {
			mappingStr = mappingStr.trim();
			Utils.ConceptMappingWrapper mappingWrapper = new Utils.ConceptMappingWrapper(mappingStr, mapType, cs);
			if (mappingWrapper.isValid()) {
				conceptMappings.add(mappingWrapper.getConceptMapping());
			} else {
				throw new IllegalArgumentException("The concept mapping identified by '" + mappingStr
				        + "' could not be created. Does the concept source actually exist in the database?. The concept defined with the following mappings was not created/updated: ["
				        + conceptList + "].");
			}
		}
		
		return conceptMappings;
	}
	
	protected Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		if (!CollectionUtils.isEmpty(concept.getConceptMappings())) {
			concept.getConceptMappings().clear();
		}
		String mappingsStr = line.get(HEADER_MAPPINGS_SAMEAS);
		if (!StringUtils.isEmpty(mappingsStr)) {
			ConceptMapType mapType = service.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
			for (ConceptMap mapping : parseMappingList(mappingsStr, mapType, service)) {
				concept.addConceptMapping(mapping);
			}
		}
		
		return concept;
	}
}

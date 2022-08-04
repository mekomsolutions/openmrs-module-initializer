package org.openmrs.module.initializer.api.c;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.mappingsConceptLineProcessor")
public class MappingsConceptLineProcessor extends ConceptLineProcessor {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String MAPPING_HEADER_PREFIX = "mapping";
	
	public static final String MAPPING_HEADER_SEPARATOR = "|";
	
	public static final String MAPPING_HEADER_SEPARATOR_REGEX = "\\|";
	
	public static final String HEADER_MAPPINGS_SAMEAS = "same as mappings";
	
	@Autowired
	public MappingsConceptLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super(conceptService);
	}
	
	public Concept fill(Concept concept, CsvLine line) throws IllegalArgumentException {
		
		if (!CollectionUtils.isEmpty(concept.getConceptMappings())) {
			concept.getConceptMappings().clear();
		}
		
		for (String header : line.getHeaderLine()) {
			
			String lineValue = line.get(header);
			if (!StringUtils.isEmpty(lineValue)) {
				
				// For backwards-compatibility, support the original same as mappings column header
				if (header.trim().equalsIgnoreCase(HEADER_MAPPINGS_SAMEAS)) {
					header = MAPPING_HEADER_PREFIX + MAPPING_HEADER_SEPARATOR + ConceptMapType.SAME_AS_MAP_TYPE_UUID;
				}
				
				String[] headerComponents = header.split(MAPPING_HEADER_SEPARATOR_REGEX);
				
				if (headerComponents[0].equalsIgnoreCase(MAPPING_HEADER_PREFIX)) {
					
					// First determine the map type (required)
					String headerMapTypeStr = headerComponents[1].trim().toLowerCase();
					ConceptMapType mapType = Utils.fetchConceptMapType(headerMapTypeStr, conceptService);
					if (mapType == null) {
						throw new IllegalArgumentException("Unable to determine concept map type: " + headerMapTypeStr);
					}
					
					String sourcePrefix = "";
					if (headerComponents.length > 2) {
						sourcePrefix = headerComponents[2].trim() + ":";
					}
					
					// Each column can support a delimited list of mappings for the given header
					// Construct each mapping based on by source:code.  Source could come from header or value
					for (String val : lineValue.split(BaseLineProcessor.LIST_SEPARATOR)) {
						String m = sourcePrefix + val.trim();
						ConceptMap cm = new Utils.ConceptMappingWrapper(m, mapType, conceptService).getConceptMapping();
						concept.addConceptMapping(cm);
					}
				}
			}
		}
		
		return concept;
	}
}

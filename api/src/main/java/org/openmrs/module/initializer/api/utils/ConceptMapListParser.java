package org.openmrs.module.initializer.api.utils;

import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptMapListParser extends ListParser<ConceptMap> {
	
	private ConceptService conceptService;
	
	@Autowired
	public ConceptMapListParser(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	protected ConceptMap fetch(String mappingStr) {
		/*
		 * TODO below should be gotten from a global config
		 */
		final ConceptMapType mapType = conceptService.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
		
		Utils.ConceptMappingWrapper mappingWrapper = new Utils.ConceptMappingWrapper(mappingStr, mapType, conceptService);
		return mappingWrapper.getConceptMapping();
	}
	
}

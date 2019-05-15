package org.openmrs.module.initializer.api.utils;

import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;

public class ConceptMapListParser extends ListParser<ConceptMap> {
	
	private ConceptService cs;
	
	public ConceptMapListParser(ConceptService cs) {
		this.cs = cs;
	}
	
	@Override
	protected ConceptMap fetch(String mappingStr) {
		/*
		 * TODO below should be gotten from a global config
		 */
		final ConceptMapType mapType = cs.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
		
		Utils.ConceptMappingWrapper mappingWrapper = new Utils.ConceptMappingWrapper(mappingStr, mapType, cs);
		return mappingWrapper.getConceptMapping();
	}
	
}

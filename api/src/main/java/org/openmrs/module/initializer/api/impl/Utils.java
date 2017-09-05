package org.openmrs.module.initializer.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;

public class Utils {
	
	/**
	 * Helps build a {@link ConceptMap} out the usual string inputs.
	 */
	public static class ConceptMappingWrapper {
		
		private ConceptSource source;
		
		private String code;
		
		private ConceptMapType mapType;
		
		public ConceptMappingWrapper(String mappingStr, ConceptMapType mapType, ConceptService cs) {
			String[] parts = mappingStr.split(":");
			if (parts.length == 2) {
				source = cs.getConceptSourceByName(parts[0].trim());
				code = parts[1].trim();
			}
			this.mapType = mapType;
		}
		
		public ConceptMap getConceptMapping() {
			ConceptReferenceTerm refTerm = new ConceptReferenceTerm(source, code, "");
			return new ConceptMap(refTerm, mapType);
		}
		
		public boolean isValid() {
			return source != null;
		}
	}
	
	/**
	 * @param mapping The concept mapping, eg. "cambodia:123"
	 * @param service
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept getConceptByMapping(String mapping, ConceptService service) {
		Concept instance = null;
		if (StringUtils.isEmpty(mapping)) {
			return instance;
		}
		String[] parts = mapping.split(":");
		if (parts.length == 2) {
			instance = service.getConceptByMapping(parts[1].trim(), parts[0].trim());
		}
		return instance;
	}
	
	/**
	 * Fetches a concept trying various routes for its "id".
	 * 
	 * @param id The concept mapping ("cambodia:123"), concept name or concept UUID.
	 * @param service
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept fetchConcept(String id, ConceptService service) {
		Concept instance = null;
		if (instance == null) {
			instance = service.getConceptByUuid(id);
		}
		if (instance == null) {
			instance = service.getConceptByName(id);
		}
		if (instance == null) {
			instance = getConceptByMapping(id, service);
		}
		return instance;
	}
	
	/**
	 * Fetches a person attribute type trying various routes for its "id".
	 * 
	 * @param id The person attribute type name or UUID.
	 * @param service
	 * @return The {@link PersonAttributeType} instance if found, null otherwise.
	 */
	public static PersonAttributeType fetchPersonAttributeType(String id, PersonService service) {
		PersonAttributeType instance = null;
		if (instance == null) {
			instance = service.getPersonAttributeTypeByUuid(id);
		}
		if (instance == null) {
			instance = service.getPersonAttributeTypeByName(id);
		}
		return instance;
	}
}

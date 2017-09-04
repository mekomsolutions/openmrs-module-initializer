package org.openmrs.module.initializer.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;

public class Utils {
	
	/**
	 * Helps build a {@link ConceptMap} out the usual string inputs.
	 */
	public static class MappingWrapper {
		
		private ConceptSource source;
		
		private String code;
		
		private ConceptMapType mapType;
		
		public MappingWrapper(String mappingStr, ConceptMapType mapType, ConceptService cs) {
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
	 * @param cs
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept getConceptByMapping(String mapping, ConceptService cs) {
		Concept concept = null;
		if (StringUtils.isEmpty(mapping)) {
			return concept;
		}
		String[] parts = mapping.split(":");
		if (parts.length == 2) {
			concept = cs.getConceptByMapping(parts[1].trim(), parts[0].trim());
		}
		return concept;
	}
	
	/**
	 * Fetches a concept trying various routes for its "id".
	 * 
	 * @param id The concept mapping ("cambodia:123"), concept name or concept UUID.
	 * @param cs
	 * @return The {@link Concept} instance if found, null otherwise.
	 */
	public static Concept fetchConcept(String id, ConceptService cs) {
		Concept concept = null;
		if (concept == null) {
			concept = cs.getConceptByUuid(id);
		}
		if (concept == null) {
			concept = cs.getConceptByName(id);
		}
		if (concept == null) {
			concept = getConceptByMapping(id, cs);
		}
		return concept;
	}
}

package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;

public interface InitializerConceptService {
	
	/**
	 * Return a Concept that matches the exact fully specified name
	 * 
	 * @param name The search string
	 * @return the found Concept
	 */
	public Concept getConceptByName(String name);
}

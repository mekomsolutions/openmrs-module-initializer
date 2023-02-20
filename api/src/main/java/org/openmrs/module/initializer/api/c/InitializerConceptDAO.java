package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;

/**
 * Concept-related database functions
 * 
 * @see InitializerConceptService
 */
public interface InitializerConceptDAO {
	
	/**
	 * @see org.openmrs.module.initializer.api.c.InitializerConceptService#getConceptByName(String)
	 */
	public Concept getConceptByName(String name);
}

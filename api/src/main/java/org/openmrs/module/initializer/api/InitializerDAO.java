package org.openmrs.module.initializer.api;

import org.openmrs.Concept;

/**
 * For database related functions
 * 
 * @see InitializerService
 */
public interface InitializerDAO {
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getConceptByName(String)
	 */
	public Concept getConceptByName(String name);
}

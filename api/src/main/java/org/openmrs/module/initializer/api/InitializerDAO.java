package org.openmrs.module.initializer.api;

import org.openmrs.Concept;

/**
 * For database related functions
 * 
 * @see InitializerService
 */
public interface InitializerDAO {
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getUnretiredConceptByFullySpecifiedName(String)
	 */
	public Concept getUnretiredConceptByFullySpecifiedName(String name);
}

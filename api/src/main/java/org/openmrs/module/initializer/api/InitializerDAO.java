package org.openmrs.module.initializer.api;

import java.util.List;

import org.openmrs.Concept;

/**
 * For database related functions
 * 
 * @see InitializerService
 */
public interface InitializerDAO {
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getUnretiredConceptsByFullySpecifiedName(String)
	 */
	public List<Concept> getUnretiredConceptsByFullySpecifiedName(String name);
}

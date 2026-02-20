package org.openmrs.module.initializer.api;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;

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
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#tryAcquireLock(String)
	 */
	Boolean tryAcquireLock(String nodeId);
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#releaseLock(String)
	 */
	void releaseLock(String nodeId);
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#forceReleaseLock()
	 */
	void forceReleaseLock();
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#isLocked()
	 */
	Boolean isLocked();
	
	List<InitializerChecksum> getAll();
	
	void deleteAll();
	
	void saveOrUpdate(InitializerChecksum checksum);
	
}

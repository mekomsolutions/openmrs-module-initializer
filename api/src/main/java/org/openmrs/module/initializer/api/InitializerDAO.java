package org.openmrs.module.initializer.api;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.module.initializer.api.entities.InitializerLock;

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
	Boolean tryAcquireLock(String lockName, Date lockUntil, String lockedBy);
	
	void removeExpiredLock(String lockName);
	
	void deleteLock(String lockName);
	
	List<InitializerChecksum> getAll();
	
	void saveOrUpdate(InitializerChecksum checksum);
	
	void deleteByFilePath(String filePath);
	
}

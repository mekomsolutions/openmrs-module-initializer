package org.openmrs.module.initializer.api;

import java.util.Date;
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
	List<Concept> getUnretiredConceptsByFullySpecifiedName(String name);
	
	Boolean tryAcquireLock(String lockName, Date lockUntil, String lockedBy);
	
	void removeExpiredLock(String lockName);
	
	void deleteLock(String lockName);
	
	List<InitializerChecksum> getAllChecksums();
	
	void saveOrUpdateChecksum(InitializerChecksum checksum);
	
	void clearChecksums();
	
	void deleteChecksum(String checksumPath);
	
	void deleteChecksumsStartingWith(String checksumPathStartingWith);
}

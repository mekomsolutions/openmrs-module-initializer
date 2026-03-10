/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.entities.InitializerChecksum;
import org.openmrs.module.initializer.api.loaders.Loader;

public interface InitializerService extends OpenmrsService {
	
	/**
	 * @return A Path object representing the base path of the application data directory
	 */
	Path getBasePath();
	
	/**
	 * @return The path to the configuration folder (with NO trailing forward slash), eg.
	 *         "/opt/openmrs/configuration"
	 */
	String getConfigDirPath();
	
	/**
	 * @return The path to the checksum folder (with NO trailing forward slash), eg.
	 *         "/opt/openmrs/configuration_checksums"
	 * @deprecated used only for migration
	 */
	@Deprecated
	String getChecksumsDirPath();
	
	/**
	 * @return The list of ordered domain loaders.
	 */
	List<Loader> getLoaders();
	
	/**
	 * Loads all domains based on their order.
	 * 
	 * @param applyFilters Set this to false to ignore the inclusion or exclusion list and the
	 *            domain-specific file name wildcard filters.
	 * @param doThrow Set this to true to make this method throw early as soon as the first loading
	 *            error is encountered.
	 * @since 2.1.0
	 */
	void loadUnsafe(boolean applyFilters, boolean doThrow) throws Exception;
	
	/**
	 * Loads all domains based on their order. This method is the exception safe version of
	 * {@link #loadUnsafe(boolean, boolean)}.
	 * 
	 * @see #loadUnsafe(boolean, boolean) This variant of the loading routine does not throw checked
	 *      exceptions, it only logs errors.
	 * @since 2.1.0
	 */
	void load();
	
	/**
	 * Add a single key value to the memory store.
	 * 
	 * @param key
	 * @param value
	 */
	void addKeyValue(String key, String value);
	
	/**
	 * Adds a key values file to the memory store.
	 * 
	 * @param is A JSON key-values file as input stream.
	 */
	void addKeyValues(InputStream is) throws Exception;
	
	/**
	 * Fetches a value from the JSON key-value configuration.
	 */
	String getValueFromKey(String key);
	
	/**
	 * Guesses a concept from the JSON key-value configuration.
	 */
	Concept getConceptFromKey(String key, Concept defaultInstance);
	
	/**
	 * Guesses a concept from the JSON key-value configuration.
	 */
	Concept getConceptFromKey(String key);
	
	/**
	 * Guesses a list of concepts from the JSON key-value configuration.
	 */
	List<Concept> getConceptsFromKey(String key);
	
	/**
	 * Guesses a person attribute type from the JSON key-value configuration.
	 */
	PersonAttributeType getPersonAttributeTypeFromKey(String key, PersonAttributeType defaultInstance);
	
	/**
	 * Guesses a person attribute type from the JSON key-value configuration.
	 */
	PersonAttributeType getPersonAttributeTypeFromKey(String key);
	
	/**
	 * Guesses a boolean from the JSON key-value configuration.
	 */
	Boolean getBooleanFromKey(String key, Boolean defaultInstance);
	
	/**
	 * Guesses a boolean from the JSON key-value configuration.
	 */
	Boolean getBooleanFromKey(String key);
	
	/**
	 * @return the configuration of the Initializer module as defined by the runtime properties
	 */
	InitializerConfig getInitializerConfig();
	
	/**
	 * Returns non-retired unique concepts whose fully specified name exactly matches the supplied name
	 * in any locale
	 * 
	 * @param name The search string
	 * @return the found Concepts
	 */
	List<Concept> getUnretiredConceptsByFullySpecifiedName(String name);
	
	/**
	 * Determines whether the initializer config files have changed since the last successful execution.
	 * 
	 * @return true or false if changes are detected and the initializer should run or otherwise.
	 */
	boolean hasChecksumsChanged();
	
	/**
	 * Used to free up memory after initialization is done
	 */
	void clearChecksumsCache();
	
	/**
	 * Used for testing only
	 */
	void clearChecksums();
	
	/**
	 * Use to delete a checksum for a particular Path
	 * 
	 * @param path
	 */
	void deleteChecksum(Path path);
	
	/**
	 * Delete all checksums for a particular domain
	 * 
	 * @param domain
	 */
	void deleteChecksums(String domain);
	
	/**
	 * Gets all checksums from DB.
	 * 
	 * @return map of file paths and checksums
	 */
	Map<String, String> getSavedChecksums();
	
	/**
	 * Calculates checksum for the given file.
	 * 
	 * @param path
	 * @return
	 */
	String getFileChecksum(Path path);
	
	/**
	 * Calculates checksum for the given file and returns it if it is different from the one in DB.
	 * 
	 * @param path
	 * @return checksum if changed
	 */
	InitializerChecksum getChecksumIfChanged(Path path);
	
	/**
	 * Calculates checksums for all initializer files. It is cached.
	 * 
	 * @return map of file paths and checksums
	 */
	Map<String, String> getFileChecksums();
	
	/**
	 * Saves new or updates checksum if changed
	 * 
	 * @param checksum
	 */
	void saveOrUpdateChecksum(InitializerChecksum checksum);
	
	/**
	 * Attempts to acquire the initializer lock for the given name.
	 * 
	 * @param lockName the name of the lock.
	 * @return true if the lock was successfully acquired, false otherwise.
	 */
	Boolean tryAcquireLock(String lockName);
	
	/**
	 * Releases the initializer lock for the given name.
	 * 
	 * @param lockName the name of the lock.
	 */
	void releaseLock(String lockName);
	
	/**
	 * Waits to acquire the initializer lock for the given name until the specified timeout.
	 * 
	 * @param lockName the name of the lock.
	 * @param timeoutMillis maximum time in milliseconds to wait for the lock.
	 * @throws RuntimeException if the timeout is reached before acquiring the lock.
	 */
	void acquireLockOrWait(String lockName, long timeoutMillis);
	
	/**
	 * Migrates checksums saved in files to DB. It is intended to be run on module startup only. It will
	 * be removed in some later version of module.
	 */
	@Deprecated
	void migrateChecksumsFromFilesToDB();
}

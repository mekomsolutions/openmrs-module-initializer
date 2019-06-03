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
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.initializer.api.loaders.Loader;

public interface InitializerService extends OpenmrsService {
	
	/**
	 * @return The path to the configuration folder (with NO trailing forward slash), eg.
	 *         "/opt/openmrs/configuration"
	 */
	String getConfigDirPath();
	
	/**
	 * @return The path to the checksum folder (with NO trailing forward slash), eg.
	 *         "/opt/openmrs/configuration_checksums"
	 */
	String getChecksumsDirPath();
	
	/**
	 * @return The path to the checksum folder (with NO trailing forward slash), eg.
	 *         "/opt/openmrs/configuration_rejections"
	 */
	String getRejectionsDirPath();
	
	/**
	 * @return The list of ordered domain loaders.
	 */
	List<Loader> getLoaders();
	
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
}

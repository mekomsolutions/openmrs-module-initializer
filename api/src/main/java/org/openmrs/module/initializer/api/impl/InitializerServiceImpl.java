/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerSerializer;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesConfig;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.util.OpenmrsUtil;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected Map<String, Object> keyValueCache = new HashMap<String, Object>();
	
	@Override
	public String getConfigDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_CONFIG).toString();
	}
	
	@Override
	public String getConfigChecksumsDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_CONFIG_CHECKSUM).toString();
	}
	
	@Override
	public String getDataDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_DATA).toString();
	}
	
	@Override
	public String getDataChecksumsDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_DATA_CHECKSUM).toString();
	}
	
	@Override
	public void loadConcepts() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_C);
	}
	
	@Override
	public void loadDrugs() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_DRUGS);
	}
	
	@Override
	public void loadLocations() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_LOC);
	}
	
	@Override
	public void loadOrderFrequencies() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_FREQ);
	}
	
	@Override
	public void loadPersonAttributeTypes() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_PAT);
	}
	
	@Override
	public void loadGlobalProperties() {
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getConfigChecksumsDirPath(),
		        InitializerConstants.DOMAIN_GP);
		
		final List<GlobalProperty> globalProperties = new ArrayList<GlobalProperty>();
		for (File file : util.getFiles("xml")) { // processing all the XML files inside the domain
			
			String fileName = util.getFileName(file.getPath());
			String checksum = util.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			
			GlobalPropertiesConfig config = new GlobalPropertiesConfig();
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				config = InitializerSerializer.getGlobalPropertiesConfig(is);
				globalProperties.addAll(config.getGlobalProperties());
				util.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The global properties config. file has been processed: " + fileName);
			}
			catch (Exception e) {
				log.error("Could not load the global properties from: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
		
		log.info("Saving the global properties.");
		Context.getAdministrationService().saveGlobalProperties(globalProperties);
	}
	
	@Override
	public void loadIdentifierSources() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getConfigChecksumsDirPath(), InitializerConstants.DOMAIN_IDGEN);
	}
	
	@Override
	public void importMetadataSharingPackages() {
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getConfigChecksumsDirPath(),
		        InitializerConstants.DOMAIN_MDS);
		
		final PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		ImportConfig importConfig = new ImportConfig();
		importConfig.setPossibleMatch(ImportType.PREFER_THEIRS);
		importConfig.setExactMatch(ImportType.PREFER_THEIRS);
		importer.setImportConfig(importConfig);
		for (File file : util.getFiles("zip")) { // processing all the zip files inside the domain
			
			String fileName = util.getFileName(file.getPath());
			String checksum = util.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				importer.loadSerializedPackageStream(is);
				is.close();
				importer.importPackage();
				util.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The following MDS package was succesfully imported: " + fileName);
			}
			catch (Exception e) {
				log.error("The MDS package could not be imported: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
	
	protected void addKeyValues(InputStream is) throws Exception {
		keyValueCache.putAll((new ObjectMapper()).readValue(is, Map.class));
	}
	
	@Override
	public void loadJsonKeyValues() {
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getConfigChecksumsDirPath(),
		        InitializerConstants.DOMAIN_JKV);
		
		for (File file : util.getFiles("json")) { // processing all the JSON files inside the domain
			
			String fileName = util.getFileName(file.getPath());
			
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				addKeyValues(is);
				is.close();
				log.info("The following JSON key-value file was succesfully imported: " + fileName);
			}
			catch (Exception e) {
				log.error("The JSON key-value file could not be imported: " + file.getPath(), e);
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}
	}
	
	/*
	 * Data Loaders
	 */
	
	@Override
	public void loadPatients() {
		ConfigDirUtil.loadCsvFiles(getDataDirPath(), getDataChecksumsDirPath(), InitializerConstants.DOMAIN_P);
	}
	
	/*
	 * Convenience method to serialize a JSON object that also handles the simple
	 * string case.
	 */
	protected String asString(Object jsonObj) throws JsonGenerationException, JsonMappingException, IOException {
		if (jsonObj == null) {
			return "";
		}
		if (jsonObj instanceof String) {
			return (String) jsonObj;
		} else {
			return (new ObjectMapper()).writeValueAsString(jsonObj);
		}
	}
	
	/*
	 * Convenience method to read a list of string out of a JSON string.
	 */
	protected List<String> asStringList(String jsonString) throws JsonParseException, JsonMappingException, IOException {
		List<Object> list = (new ObjectMapper()).readValue(jsonString, List.class);
		
		List<String> stringList = new ArrayList<String>();
		for (Object o : list) {
			stringList.add(asString(o));
		}
		return stringList;
	}
	
	/*
	 * An error is logged when the JSON value cannot be parsed. However this should
	 * never happen since this error would have prevented the loading of the
	 * key-value file in the first place.
	 */
	@Override
	public String getValueFromKey(String key) {
		Object value = keyValueCache.get(key);
		try {
			return asString(value);
		}
		catch (Exception e) {
			log.error(e);
		}
		return "";
	}
	
	@Override
	public Concept getConceptFromKey(String key, Concept defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		Concept instance = Utils.fetchConcept(val, Context.getConceptService());
		if (instance != null) {
			return instance;
		} else {
			return defaultInstance;
		}
	}
	
	@Override
	public Concept getConceptFromKey(String key) {
		return getConceptFromKey(key, null);
	}
	
	@Override
	public List<Concept> getConceptsFromKey(String key) {
		List<String> ids;
		try {
			ids = asStringList(getValueFromKey(key));
		}
		catch (Exception e) {
			log.error("The JSON value for key '" + key + "' could not be parsed as a list of concept identifiers.", e);
			return Collections.emptyList();
		}
		List<Concept> concepts = new ArrayList<Concept>();
		for (String id : ids) {
			concepts.add(Utils.fetchConcept(id, Context.getConceptService()));
		}
		return concepts;
	}
	
	@Override
	public PersonAttributeType getPersonAttributeTypeFromKey(String key, PersonAttributeType defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		PersonAttributeType instance = Utils.fetchPersonAttributeType(val, Context.getPersonService());
		if (instance != null) {
			return instance;
		} else {
			return defaultInstance;
		}
	}
	
	@Override
	public PersonAttributeType getPersonAttributeTypeFromKey(String key) {
		return getPersonAttributeTypeFromKey(key, null);
	}
	
	@Override
	public Boolean getBooleanFromKey(String key, Boolean defaultInstance) {
		String val = getValueFromKey(key);
		if (StringUtils.isEmpty(val)) {
			return defaultInstance;
		}
		try {
			return BooleanUtils.toBoolean(val, "1", "0");
		}
		catch (IllegalArgumentException e) {
			return BooleanUtils.toBooleanObject(val);
		}
	}
	
	@Override
	public Boolean getBooleanFromKey(String key) {
		return getBooleanFromKey(key, null);
	}
}

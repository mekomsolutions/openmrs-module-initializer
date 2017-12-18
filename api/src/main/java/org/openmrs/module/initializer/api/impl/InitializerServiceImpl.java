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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	protected Map<String, String> keyValueCache = new HashMap<String, String>();
	
	@Override
	public String getConfigDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_CONFIG).toString();
	}
	
	@Override
	public String getChecksumsDirPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.DIR_NAME_CHECKSUM).toString();
	}
	
	@Override
	public void loadConcepts() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getChecksumsDirPath(), InitializerConstants.DOMAIN_C);
	}
	
	@Override
	public void loadDrugs() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getChecksumsDirPath(), InitializerConstants.DOMAIN_DRUGS);
	}
	
	@Override
	public void loadPersonAttributeTypes() {
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getChecksumsDirPath(), InitializerConstants.DOMAIN_PAT);
	}
	
	@Override
	public void loadGlobalProperties() {
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getChecksumsDirPath(),
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
		ConfigDirUtil.loadCsvFiles(getConfigDirPath(), getChecksumsDirPath(), InitializerConstants.DOMAIN_IDGEN);
	}
	
	@Override
	public void importMetadataSharingPackages() {
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getChecksumsDirPath(),
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
		
		final ConfigDirUtil util = new ConfigDirUtil(getConfigDirPath(), getChecksumsDirPath(),
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
	
	@Override
	public String getValueFromKey(String key) {
		return keyValueCache.get(key);
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

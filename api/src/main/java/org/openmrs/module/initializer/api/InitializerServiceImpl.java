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

import static org.openmrs.module.initializer.InitializerConstants.DIR_NAME_CHECKSUM;
import static org.openmrs.module.initializer.InitializerConstants.DIR_NAME_CONFIG;
import static org.openmrs.module.initializer.InitializerConstants.GP_INITIALIZER_SIGNATURE;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private InitializerConfig cfg;
	
	private Map<String, Object> keyValueCache = new HashMap<String, Object>();
	
	private InitializerDAO initializerDAO;
	
	public void setConfig(InitializerConfig cfg) {
		this.cfg = cfg;
	}
	
	/**
	 * Sets the data access object. The initializerDao is used for saving and getting entities to/from
	 * the database
	 * 
	 * @param initializerDAO The data access object to use
	 */
	public void setInitializerDAO(InitializerDAO initializerDAO) {
		this.initializerDAO = initializerDAO;
	}
	
	public Path getBasePath() {
		return Paths.get(new File(OpenmrsUtil.getApplicationDataDirectory()).toURI());
	}
	
	//Path based concatenation using Path/Paths resolve method
	//https://docs.oracle.com/javase/tutorial/essential/io/pathOps.html#resolve
	
	@Override
	public String getConfigDirPath() {
		return getBasePath().resolve(DIR_NAME_CONFIG).toString();
	}
	
	@Override
	public String getChecksumsDirPath() {
		return getBasePath().resolve(DIR_NAME_CHECKSUM).toString();
	}
	
	@Override
	public List<Loader> getLoaders() {
		return Context.getRegisteredComponents(Loader.class).stream().filter(l -> !l.isPreLoader()).sorted()
		        .collect(Collectors.toList());
	}
	
	@Override
	public void loadUnsafe(boolean applyFilters, boolean doThrow) throws Exception {
		
		final Set<String> specifiedDomains = applyFilters ? cfg.getFilteredDomains() : Collections.emptySet();
		final boolean includeSpecifiedDomains = !applyFilters || cfg.isInclusionList();
		
		for (Loader loader : getLoaders()) {
			boolean domainSpecified = specifiedDomains.contains(loader.getDomainName());
			if (specifiedDomains.isEmpty()
			        || ((includeSpecifiedDomains && domainSpecified) || (!includeSpecifiedDomains && !domainSpecified))) {
				
				final List<String> wildcardExclusions = applyFilters ? cfg.getWidlcardExclusions(loader.getDomainName())
				        : Collections.emptyList();
				loader.loadUnsafe(wildcardExclusions, doThrow);
			}
		}
	}
	
	/*
	 * This is a mere wrapper of the unsafe version when it doesn't throw anyway.
	 */
	@Override
	public void load() {
		try {
			loadUnsafe(true, false);
		}
		catch (Exception e) {}
	}
	
	@Override
	public void addKeyValue(String key, String value) {
		keyValueCache.put(key, value);
	}
	
	@Override
	public void addKeyValues(InputStream is) throws Exception {
		keyValueCache.putAll((new ObjectMapper()).readValue(is, Map.class));
	}
	
	@Override
	public String getValueFromKey(String key) {
		Object value = keyValueCache.get(key);
		try {
			return Utils.asString(value);
		}
		catch (Exception e) {
			log.error(null, e);
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
			ids = Utils.asStringList(getValueFromKey(key));
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
	
	@Override
	public InitializerConfig getInitializerConfig() {
		return cfg;
	}
	
	/**
	 * @see org.openmrs.module.initializer.api.InitializerService#getUnretiredConceptsByFullySpecifiedName(String)
	 */
	@Override
	public List<Concept> getUnretiredConceptsByFullySpecifiedName(String name) {
		return initializerDAO.getUnretiredConceptsByFullySpecifiedName(name);
	}
	
	@Override
	public boolean shouldRunInitializer() {
		String oldSig = Context.getAdministrationService().getGlobalProperty(GP_INITIALIZER_SIGNATURE);
		return oldSig == null || !oldSig.equals(computeSignature());
	}
	
	@Override
	public void updateInitializerRunState() {
		Context.getAdministrationService().setGlobalProperty(GP_INITIALIZER_SIGNATURE, computeSignature());
	}
	
	private String computeSignature() {
		String coreVersion = OpenmrsConstants.OPENMRS_VERSION;
		
		String moduleVersions = ModuleFactory.getLoadedModules().stream().map(m -> m.getModuleId() + ":" + m.getVersion())
		        .collect(Collectors.joining(","));
		
		ConfigDirUtil configUtil = new ConfigDirUtil(getConfigDirPath(), getChecksumsDirPath(), "");
		
		List<File> files = configUtil.getFiles("", Collections.emptyList());
		String concatenated = files.stream().filter(File::isFile).sorted(Comparator.comparing(File::getAbsolutePath))
		        .map(file -> {
			        try {
				        byte[] bytes = Files.readAllBytes(file.toPath());
				        return DigestUtils.sha256Hex(bytes) + ":" + file.getAbsolutePath();
			        }
			        catch (Exception e) {
				        return "ERR:" + file.getAbsolutePath();
			        }
		        }).collect(Collectors.joining("|"));
		
		String configChecksum = DigestUtils.sha256Hex(concatenated);
		return DigestUtils.sha256Hex(coreVersion + "|" + moduleVersions + "|" + configChecksum);
	}
}

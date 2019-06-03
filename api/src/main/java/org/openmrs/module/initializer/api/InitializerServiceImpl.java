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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.util.OpenmrsUtil;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Map<String, Object> keyValueCache = new HashMap<String, Object>();
	
	@Override
	public String getConfigDirPath() {
		return Paths.get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.DIR_NAME_CONFIG).toString();
	}
	
	@Override
	public String getChecksumsDirPath() {
		return Paths.get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.DIR_NAME_CHECKSUM).toString();
	}
	
	@Override
	public String getRejectionsDirPath() {
		return Paths.get(OpenmrsUtil.getApplicationDataDirectory(), InitializerConstants.DIR_NAME_REJECTIONS).toString();
	}
	
	@Override
	public List<Loader> getLoaders() {
		List<Loader> loaders = Context.getRegisteredComponents(Loader.class);
		Collections.sort(loaders);
		return loaders;
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
	
}

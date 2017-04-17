/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.ConfigLoaderUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This allows to perform context sensitive tests on a specific domain inside the test app data
 * directory.
 */
public abstract class DomainBaseModuleContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	public static final String appDataTestDir = "testAppDataDir";
	
	@Autowired
	private InitializerService iniz;
	
	protected InitializerService getService() {
		return iniz;
	}
	
	/*
	 * Eg. "addresshierarchy", "globalproperties", ... etc
	 */
	abstract protected String getDomain();
	
	private String getDomainDirPath() {
		String path = getClass().getClassLoader().getResource(appDataTestDir).getPath();
		return new StringBuilder(path).append(File.separator).append(InitializerConstants.CONFIG_PATH)
		        .append(File.separator).append(getDomain()).toString();
	}
	
	@Before
	public void setup() {
		
		String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
		
		OpenmrsConstants.APPLICATION_DATA_DIRECTORY = path; // The 1.10 way
		Properties prop = new Properties();
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
		
		ConfigLoaderUtil.deleteChecksums(getDomainDirPath());
	}
}

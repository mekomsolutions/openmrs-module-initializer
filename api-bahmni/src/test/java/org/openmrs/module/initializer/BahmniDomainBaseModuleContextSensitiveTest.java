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

import static org.openmrs.module.initializer.api.ConfigDirUtil.CHECKSUM_FILE_EXT;
import static org.openmrs.module.initializer.api.ConfigDirUtil.deleteFilesByExtension;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This allows to perform context sensitive tests on a specific domain inside the test app data
 * directory which expecte bahmni-core module to be loaded.
 */
public abstract class BahmniDomainBaseModuleContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String appDataTestDir = "testAppDataDir";
	
	private InitializerService iniz;
	
	@Autowired
	public void setService(InitializerService iniz) {
		this.iniz = iniz;
	}
	
	public InitializerService getService() {
		return iniz;
	}
	
	/*
	 * pre-Spring loading setup for all integration tests
	 *
	 * We start all the required conditional modules here.
	 */
	public BahmniDomainBaseModuleContextSensitiveTest() {
		super();
		{
			Module mod = new Module("", "bahmnicore", "", "", "", "0.93-SNAPSHOT");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
	}
	
	protected String getAppDataDirPath() {
		return getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
	}
	
	@Before
	public void setupAppDataDir() {
		
		String path = getAppDataDirPath();
		
		System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
		Properties prop = new Properties();
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
	}
	
	@After
	public void tearDown() {
		deleteFilesByExtension(iniz.getChecksumsDirPath(), CHECKSUM_FILE_EXT);
	}
}

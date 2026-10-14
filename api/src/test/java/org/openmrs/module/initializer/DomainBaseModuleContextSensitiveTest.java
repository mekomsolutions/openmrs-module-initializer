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
import java.util.Locale;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

/**
 * This allows to perform context sensitive tests on a specific domain inside the test app data
 * directory.
 */
@ContextConfiguration(locations = { "classpath:applicationContext-service.xml", "classpath*:TestingApplicationContext.xml",
        "classpath*:moduleApplicationContext.xml" }, loader = Platform3ModuleContextLoader.class, inheritLocations = false)
public abstract class DomainBaseModuleContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String appDataTestDir = "testAppDataDir";
	
	private InitializerService iniz;
	
	@Autowired
	MessageSourceService messageSourceService;
	
	@Autowired
	InitializerMessageSource initializerMessageSource;
	
	@Autowired
	@Qualifier("initializer.InitializerService")
	public void setService(InitializerService iniz) {
		this.iniz = iniz;
	}
	
	public InitializerService getService() {
		return iniz;
	}
	
	/*
	 * pre-Spring loading setup for all integration tests
	 *
	 * We start all the conditional modules here.
	 */
	public DomainBaseModuleContextSensitiveTest() {
		super();
		initModules();
	}
	
	protected void initModules() {
		// Only the optional modules that have a Platform 3.0 release are started, see Platform3ModuleContextLoader
		{
			Module mod = new Module("", "fhir2", "", "", "", "1.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "idgen", "", "", "", "4.6.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "metadatamapping", "", "", "", "1.3.4", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
		{
			Module mod = new Module("", "cohort", "", "", "", "3.5.0", "");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
	}
	
	protected String getAppDataDirPath() {
		return getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
	}
	
	@Override
	public Properties getRuntimeProperties() {
		Properties p = super.getRuntimeProperties();
		p.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, getAppDataDirPath());
		OpenmrsUtil.setApplicationDataDirectory(getAppDataDirPath());
		return p;
	}
	
	@BeforeEach
	public void setupAppDataDir() {
		String path = getAppDataDirPath();
		System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
		Properties prop = getRuntimeProperties();
		prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
		Context.setRuntimeProperties(prop);
		messageSourceService.setActiveMessageSource(initializerMessageSource);
		if (initializerMessageSource.getPresentations().isEmpty()) {
			initializerMessageSource.initialize();
		}
		if (!initializerMessageSource.getFallbackLanguages().containsKey("ht")) {
			initializerMessageSource.addFallbackLanguage("ht", "fr");
		}
		Locale.setDefault(Locale.ENGLISH);
	}
	
	@AfterEach
	public void tearDown() {
		deleteFilesByExtension(iniz.getChecksumsDirPath(), CHECKSUM_FILE_EXT);
	}
}

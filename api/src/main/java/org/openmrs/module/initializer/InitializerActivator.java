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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Level;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleException;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.logging.InitializerLogConfigurator;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openmrs.module.initializer.InitializerConstants.MODULE_ARTIFACT_ID;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_LOGGING_ENABLED;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_LOGGING_LEVEL;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_LOGGING_LOCATION;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_PRIMARY_STARTUP;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_DISABLED;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_FAIL_ON_ERROR;
import static org.openmrs.module.initializer.api.utils.Utils.getPropertyValue;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class InitializerActivator extends BaseModuleActivator {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * @see #started()
	 */
	public void started() {
		log.info("Start of {} module.", MODULE_ARTIFACT_ID);
		
		if (Boolean.parseBoolean(getPropertyValue(PROPS_LOGGING_ENABLED, "true"))) {
			List<InitializerLogConfigurator> logConfigurators = getInitializerLogConfigurator();
			if (logConfigurators != null && logConfigurators.size() > 0) {
				Path logFilePath = null;
				String logFileLocation = getPropertyValue(PROPS_LOGGING_LOCATION);
				if (logFileLocation != null) {
					logFilePath = Paths.get(logFileLocation);
					
					Path applicationDataDirectory = Paths.get(OpenmrsUtil.getApplicationDataDirectory());
					if (!logFilePath.isAbsolute()) {
						logFilePath = applicationDataDirectory.resolve(logFilePath);
					} else {
						try {
							logFilePath = logFilePath.toRealPath();
						}
						catch (IOException e) {
							logFilePath = null;
						}
					}
				}
				
				Level level = Level.toLevel(getPropertyValue(PROPS_LOGGING_LEVEL), Level.WARN);
				
				logConfigurators.get(0).setupLogging(level, logFilePath);
			}
		}
		
		// Set active message source
		InitializerMessageSource messageSource = getInitializerMessageSource();
		Context.getMessageSourceService().setActiveMessageSource(messageSource);
		
		boolean isPrimary = Boolean.parseBoolean(getPropertyValue(PROPS_PRIMARY_STARTUP, "false"));
		if (!isPrimary) {
			log.info("Initializer running in REPLICA mode → skipping config imports");
			return;
		}
		
		String startupLoadingMode = getInitializerService().getInitializerConfig().getStartupLoadingMode();
		
		if (PROPS_STARTUP_LOAD_DISABLED.equalsIgnoreCase(startupLoadingMode)) {
			log.info("OpenMRS config loading process disabled at initializer startup");
			return;
		}
		
		if (!shouldRunInitializerSafely()) {
			log.info("No version/config changes detected → skipping initializer execution.");
			return;
		}
		
		boolean throwError = PROPS_STARTUP_LOAD_FAIL_ON_ERROR.equalsIgnoreCase(startupLoadingMode);
		log.info("OpenMRS config loading process started...");
		
		try {
			getInitializerService().loadUnsafe(true, throwError);
			updateRunState();
			log.info("OpenMRS config loading process completed.");
		}
		catch (Exception e) {
			throw new ModuleException("An error occurred loading initializer configuration", e);
		}
	}
	
	protected InitializerService getInitializerService() {
		return Context.getService(InitializerService.class);
	}
	
	protected List<InitializerLogConfigurator> getInitializerLogConfigurator() {
		return Context.getRegisteredComponents(InitializerLogConfigurator.class);
	}
	
	protected InitializerMessageSource getInitializerMessageSource() {
		return Context.getRegisteredComponents(InitializerMessageSource.class).get(0);
	}
	
	protected boolean shouldRunInitializerSafely() {
		return getInitializerService().shouldRunInitializer();
	}
	
	protected void updateRunState() {
		getInitializerService().updateInitializerRunState();
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown of " + MODULE_ARTIFACT_ID + " module.");
	}
}

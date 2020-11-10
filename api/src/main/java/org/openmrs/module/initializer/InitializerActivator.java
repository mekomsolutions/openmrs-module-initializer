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

import static org.apache.log4j.Level.ERROR;
import static org.openmrs.module.initializer.InitializerConstants.MODULE_ARTIFACT_ID;

import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.util.OpenmrsUtil;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class InitializerActivator extends BaseModuleActivator {
	
	static Log log = LogFactory.getLog(InitializerActivator.class);
	
	/**
	 * @see #started()
	 */
	public void started() {
		
		// setting the custom logging for all Iniz errors
		try {
			String inizLogFilePath = Paths.get(OpenmrsUtil.getApplicationDataDirectory(), MODULE_ARTIFACT_ID + ".log")
			        .toString();
			
			Appender appender = Logger.getRootLogger().getAppender("DEBUGGING_FILE_APPENDER");
			Layout layout = appender == null ? new PatternLayout("%p - %C{1}.%M(%L) |%d{ISO8601}| %m%n")
			        : appender.getLayout();
			appender = new FileAppender(layout, inizLogFilePath);
			
			Logger logger = Logger.getLogger(InitializerActivator.class.getPackage().getName());
			logger.addAppender(appender);
			logger.setLevel(ERROR);
		}
		catch (Exception e) {
			log.error("The custom error log appender could not be setup for Initializer.", e);
		}
		
		// loading all domains
		InitializerService iniz = Context.getService(InitializerService.class);
		for (Loader loader : iniz.getLoaders()) {
			loader.load();
		}
		
		log.info("Start of initializer module.");
		
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		// log.info("Shutdown " + InitializerConstants.MODULE_NAME);
	}
}

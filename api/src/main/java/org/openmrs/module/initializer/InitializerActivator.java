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

import org.apache.commons.logging.Log;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class InitializerActivator extends BaseModuleActivator {
	
	static Log log = InitializerLogFactory.getLog(InitializerActivator.class);
	
	/**
	 * @see #started()
	 */
	public void started() {
		
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

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
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.initializer.api.InitializerService;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class InitializerActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see #started()
	 */
	public void started() {
		
		InitializerService iniz = Context.getService(InitializerService.class);
		
		iniz.loadJsonKeyValues();
		iniz.importMetadataSharingPackages();
		iniz.loadGlobalProperties();
		iniz.loadLocations();
		iniz.loadConcepts();
		iniz.loadPersonAttributeTypes();
		iniz.loadIdentifierSources();
		iniz.loadDrugs();
		iniz.loadOrderFrequencies();
		
		iniz.loadPatients();
		iniz.loadPersons();
		log.info("Started " + InitializerConstants.MODULE_NAME);
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown " + InitializerConstants.MODULE_NAME);
	}
}

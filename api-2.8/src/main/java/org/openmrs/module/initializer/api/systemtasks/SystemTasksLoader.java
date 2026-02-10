/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.systemtasks;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.openmrs.module.tasks.SystemTask;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Loader for SystemTask CSV files. Loads system task templates from the configuration/systemtasks/
 * directory.
 */
@OpenmrsProfile(modules = { "tasks:*" })
public class SystemTasksLoader extends BaseCsvLoader<SystemTask, SystemTasksCsvParser> {
	
	@Autowired
	public void setParser(SystemTasksCsvParser parser) {
		this.parser = parser;
	}
}

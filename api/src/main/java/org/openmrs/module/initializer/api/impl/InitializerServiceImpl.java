/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.util.OpenmrsUtil;

public class InitializerServiceImpl extends BaseOpenmrsService implements InitializerService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public String getConfigPath() {
		return new StringBuilder().append(OpenmrsUtil.getApplicationDataDirectory())
		        .append(InitializerConstants.CONFIG_PATH).toString();
	}
	
	@Override
	public String getAddressHierarchyConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_ADDR)
		        .toString();
	}
	
	@Override
	public String getGlobalPropertiesConfigPath() {
		return new StringBuilder().append(getConfigPath()).append(File.separator).append(InitializerConstants.DOMAIN_GP)
		        .toString();
	}
}

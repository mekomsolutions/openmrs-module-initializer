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

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;

import java.io.File;

/**
 * This allows to perform Spring context sensitive tests when patientflags module is a dependency.
 */
public abstract class DomainBaseModuleContextSensitive_2_4_patientflags_test extends DomainBaseModuleContextSensitiveTest {
	
	public DomainBaseModuleContextSensitive_2_4_patientflags_test() {
		super();
		{
			Module mod = new Module("", "patientflags", "", "", "", "3.0.0");
			mod.setFile(new File(""));
			ModuleFactory.getStartedModulesMap().put(mod.getModuleId(), mod);
		}
	}
	
	@Override
	public void updateSearchIndex() {
		// to prevent Data Filter's 'Illegal Record Access'
	}
	
	@Override
	public void revertContextMocks() {
		
	}
}

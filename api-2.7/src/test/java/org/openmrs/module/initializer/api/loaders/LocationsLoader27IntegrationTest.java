/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.loaders;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_7_Test;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

public class LocationsLoader27IntegrationTest extends DomainBaseModuleContextSensitive_2_7_Test {
	
	@Autowired
	private LocationsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() throws Exception {
		loader.loadUnsafe(Collections.emptyList(), true);
	}
}

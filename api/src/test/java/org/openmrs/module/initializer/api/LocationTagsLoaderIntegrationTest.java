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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loc.LocationTagsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocationTagsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService service;
	
	@Autowired
	private LocationTagsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadLocationTagsAccordingToCsvFiles() {
		
		loader.load();
		
		{ // created without uuid or description
			LocationTag tag = service.getLocationTagByName("Sparse");
			Assert.assertNotNull(tag);
		}
		{ // created with uuid and description
			LocationTag tag = service.getLocationTagByName("Filled in");
			Assert.assertNotNull(tag);
			Assert.assertEquals("b03e395c-b881-49b7-b6fc-983f6befc7fc", tag.getUuid());
			Assert.assertEquals("A tag with all its fields", tag.getDescription());
		}
		{ // retired Facility Location
			LocationTag tag = service.getLocationTagByName("Facility Location");
			Assert.assertNotNull(tag);
			Assert.assertTrue(tag.getRetired());
		}
		{ // edited to change name and description
			LocationTag tag = service.getLocationTagByUuid("a1417745-1170-5752-fc8a-dd0ba131f40e");
			Assert.assertEquals("Supply Room", tag.getName());
			Assert.assertEquals("Don't call it a shed", tag.getDescription());
		}
	}
}

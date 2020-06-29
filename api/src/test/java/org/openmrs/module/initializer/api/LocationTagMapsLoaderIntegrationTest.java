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
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loc.LocationTagMapsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocationTagMapsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService service;
	
	@Autowired
	private LocationTagMapsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		
		// to remove
		Location acme = service.getLocation("Acme Clinic");
		acme.addTag(service.getLocationTagByName("Shed"));
		
		// to add again
		Location xanadu = service.getLocation("Xanadu");
		xanadu.addTag(service.getLocationTagByName("Facility Location"));
	}
	
	@Test
	public void load_shouldLoadLocationTagsAccordingToCsvFiles() {
		
		loader.load();
		
		{ // tag a location by names
			Location loc = service.getLocation("Acme Clinic");
			LocationTag tag = service.getLocationTagByName("Facility Location");
			Assert.assertTrue(loc.getName() + " should have tag " + tag.getName(), loc.hasTag(tag.getName()));
		}
		{ // remove a tag
			Location loc = service.getLocation("Acme Clinic");
			LocationTag tag = service.getLocationTagByName("Shed");
			Assert.assertFalse(loc.getName() + " should not have tag " + tag.getName(), loc.hasTag(tag.getName()));
		}
		{ // add an already-present tag
			Location loc = service.getLocation("Xanadu");
			LocationTag tag = service.getLocationTagByName("Facility Location");
			Assert.assertTrue(loc.getName() + " should still have tag " + tag.getName(), loc.hasTag(tag.getName()));
		}
		{ // remove an already-absent tag
			Location loc = service.getLocation("Xanadu");
			LocationTag tag = service.getLocationTagByName("Shed");
			Assert.assertFalse(loc.getName() + " should still not have tag " + tag.getName(), loc.hasTag(tag.getName()));
		}
	}
}

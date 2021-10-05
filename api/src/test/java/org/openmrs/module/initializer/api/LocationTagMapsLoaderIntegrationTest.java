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

import java.util.Set;

public class LocationTagMapsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private LocationTagMapsLoader loader;
	
	LocationTag facilityLocation;
	
	LocationTag shed;
	
	LocationTag appointmentLocation;
	
	LocationTag visitLocation;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		facilityLocation = ls.getLocationTagByName("Facility Location");
		shed = ls.getLocationTagByName("Shed");
		appointmentLocation = ls.getLocationTagByName("Appointment Location");
		visitLocation = ls.getLocationTagByName("Visit Location");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Pre-load verification
		Location patientHome = ls.getLocationByUuid("af939782-898b-409a-99a4-d1653484edbd");
		Set<LocationTag> tags = patientHome.getTags();
		Assert.assertEquals(tags.size(), 2);
		Assert.assertFalse(tags.contains(shed));
		Assert.assertTrue(tags.contains(facilityLocation));
		Assert.assertFalse(tags.contains(appointmentLocation));
		Assert.assertTrue(tags.contains(visitLocation));
		
		// Replay
		loader.load();
		
		// Post-load verification
		patientHome = ls.getLocationByUuid("af939782-898b-409a-99a4-d1653484edbd");
		tags = patientHome.getTags();
		Assert.assertEquals(tags.size(), 2);
		Assert.assertFalse(tags.contains(shed)); // Test tag stays absent
		Assert.assertFalse(tags.contains(facilityLocation)); // Test tag is removed
		Assert.assertTrue(tags.contains(appointmentLocation)); // Test tag is added
		Assert.assertTrue(tags.contains(visitLocation)); // Test tag remains present, even if not in CSV
	}
}

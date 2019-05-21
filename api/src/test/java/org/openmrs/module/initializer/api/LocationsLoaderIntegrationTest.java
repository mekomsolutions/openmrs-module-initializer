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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loc.LocationsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocationsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private LocationsLoader loader;
	
	@Before
	public void setup() {
		
		LocationTag tag = ls.saveLocationTag(new LocationTag("Facility Location", ""));
		
		// location to edit
		{
			Location loc = new Location();
			loc.setUuid("a03e395c-b881-49b7-b6fc-983f6bddc7fc");
			loc.setName("Acme Clinic");
			loc.setTags(new HashSet<LocationTag>(Arrays.asList(tag)));
			ls.saveLocation(loc);
		}
		// location to retire
		{
			Location loc = new Location();
			loc.setUuid("cbaaaab4-d960-4ae9-9b6a-8983fbd947b6");
			loc.setName("Legacy Location");
			loc.setDescription("Legacy location that must be retired");
			ls.saveLocation(loc);
		}
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif creation
		{
			Location loc = ls.getLocation("The Lake Clinic-Cambodia");
			Assert.assertEquals("Paradise Street", loc.getAddress1());
			Assert.assertEquals("Siem Reap", loc.getCountyDistrict());
			Assert.assertEquals("Siem Reap", loc.getStateProvince());
			Assert.assertEquals("Cambodia", loc.getCountry());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(2));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Login Location")), is(true));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Facility Location")), is(true));
		}
		// Verif creation
		{
			Location loc = ls.getLocation("OPD Room");
			Assert.assertEquals(ls.getLocation("The Lake Clinic-Cambodia"), loc.getParentLocation());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(1));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Consultation Location")), is(true));
		}
		// Verif edition
		{
			Location loc = ls.getLocation("Acme Clinic");
			Assert.assertEquals("This now becomes a child of TLC", loc.getDescription());
			Assert.assertEquals(ls.getLocation("The Lake Clinic-Cambodia"), loc.getParentLocation());
			
			Set<LocationTag> tags = loc.getTags();
			Assert.assertThat(tags, notNullValue());
			Assert.assertThat(tags.size(), is(1));
			Assert.assertThat(tags.contains(ls.getLocationTagByName("Login Location")), is(true));
		}
		// Verif retire
		{
			Location loc = ls.getLocationByUuid("cbaaaab4-d960-4ae9-9b6a-8983fbd947b6");
			Assert.assertThat(loc.isRetired(), is(true));
		}
	}
}

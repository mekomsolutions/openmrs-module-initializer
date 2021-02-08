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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentServiceDefinitionsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AppointmentServiceDefinitionsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("appointmentServiceService")
	private AppointmentServiceDefinitionService apts;
	
	@Autowired
	@Qualifier("specialityService")
	private SpecialityService sps;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private AppointmentServiceDefinitionsLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		
		Location l = ls.getLocation(2); // 'Xanadu'
		LocationTag lt = ls.getLocationTag(4091); // 'Appointment Location'
		l.getTags().add(lt);
		ls.saveLocation(l);
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Verify setup
		{
			Assert.assertEquals("Orthopaedic",
			    apts.getAppointmentServiceByUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb").getSpeciality().getName());
			Assert.assertEquals("Xanadu",
			    apts.getAppointmentServiceByUuid("a1039051-6f34-420d-9779-24e77eb0ca00").getLocation().getName());
		}
		
		// Replay
		loader.load();
		
		Assert.assertThat(apts.getAllAppointmentServices(false).size(), is(7));
		// Location set by UUID
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("762e165a-af27-45fe-ad6e-1fe19db78198");
			Assert.assertEquals("Casting", def.getName());
			Assert.assertEquals(new Integer(30), def.getDurationMins());
			Assert.assertEquals(new Integer(15), def.getMaxAppointmentsLimit());
			Assert.assertEquals("Xanadu", def.getLocation().getName());
		}
		// Location set by name 
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("bfff3484-320a-4c1e-84c8-dbe8f0d44e8b");
			Assert.assertEquals("Orthopaedic Follow-up", def.getName());
			Assert.assertEquals("A follow-up appointment at the orthopedic clinic", def.getDescription());
			Assert.assertEquals("Xanadu", def.getLocation().getName());
		}
		// Speciality set by name
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("c12829d8-6bdd-426c-a386-104eed0d2c41");
			Assert.assertEquals("Bracing", def.getName());
			Assert.assertEquals("Orthopaedic", def.getSpeciality().getName());
			Assert.assertEquals("#8FBC8F", def.getColor());
		}
		// Speciality set by UUID 
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("b4b96cea-a0ed-4bbc-84f0-6c6b4e79f447");
			Assert.assertEquals("Tenotomy", def.getName());
			Assert.assertEquals("Orthopaedic", def.getSpeciality().getName());
		}
		// Service start and end times
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("fc46dedf-5e96-44d4-bd99-bec1d80d15d5");
			Assert.assertEquals("Surgery", def.getName());
			Assert.assertEquals("Appointment for surgery", def.getDescription());
			Assert.assertEquals("08:00:00", def.getStartTime().toString());
			Assert.assertEquals("17:00:00", def.getEndTime().toString());
		}
		// Removing speciality
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb");
			Assert.assertEquals("Service With Speciality", def.getName());
			Assert.assertNull(def.getSpeciality());
		}
		
		// Removing location
		{
			AppointmentServiceDefinition def = apts.getAppointmentServiceByUuid("a1039051-6f34-420d-9779-24e77eb0ca00");
			Assert.assertEquals("Service With Location", def.getName());
			Assert.assertNull(def.getLocation());
		}
	}
}

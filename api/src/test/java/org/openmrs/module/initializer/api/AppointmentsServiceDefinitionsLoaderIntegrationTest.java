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
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentsServiceDefinitionsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AppointmentsServiceDefinitionsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("appointmentServiceService")
	private AppointmentServiceDefinitionService appointmentServiceService;
	
	@Autowired
	@Qualifier("specialityService")
	private SpecialityService specialityService;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	private AppointmentsServiceDefinitionsLoader loader;
	
	@Before
	public void setup() {
		// An appointment location to be retired via CSV
		{
			LocationTag lt = new LocationTag();
			lt.setName("Appointment Location");
			lt.setUuid("a3e05b0e-c70d-11e7-aa2b-8e7070d6a3d1");
			ls.saveLocationTag(lt);
			
			Location l = ls.getLocation("Xanadu");
			l.getTags().add(lt);
			ls.saveLocation(l);
		}
		
		// A Speciality to be retired via CSV
		{
			Speciality s = new Speciality();
			s.setName("Orthopaedic");
			s.setUuid("cf213609-11ab-11ea-a6a0-080027405b36");
			specialityService.save(s);
		}
	
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif creation of appointment service definitions
		{
			Assert.assertEquals(5, appointmentServiceService.getAllAppointmentServices(false).size());
		}
		// Verif adding appointment service location
		{
			AppointmentServiceDefinition asd = appointmentServiceService.getAppointmentServiceByUuid("762e165a-af27-45fe-ad6e-1fe19db78198");
			Assert.assertEquals("Xanadu", asd.getLocation().getName());
		}
		
	}
}

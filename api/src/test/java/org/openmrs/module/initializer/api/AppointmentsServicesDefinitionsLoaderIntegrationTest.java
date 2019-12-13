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
import org.openmrs.module.initializer.api.appt.servicedefinitions.AppointmentsServicesDefinitionsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AppointmentsServicesDefinitionsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
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
	private AppointmentsServicesDefinitionsLoader loader;
	
	@Before
	public void setup() {
		// An appointment location to be retrieved via CSV
		{
			LocationTag lt = new LocationTag();
			lt.setName("Appointment Location");
			lt.setUuid("a3e05b0e-c70d-11e7-aa2b-8e7070d6a3d1");
			ls.saveLocationTag(lt);
			
			Location l = ls.getLocation("Xanadu");
			l.getTags().add(lt);
			ls.saveLocation(l);
		}
		
		// A Speciality to be retrieved via CSV
		{
			Speciality s = new Speciality();
			s.setName("Orthopaedic");
			s.setUuid("cf213609-11ab-11ea-a6a0-080027405b36");
			specialityService.save(s);
		}
		
		// Service to test removing service speciality via CSV
		{
			AppointmentServiceDefinition sd = new AppointmentServiceDefinition();
			sd.setName("ServiceWithSpeciality");
			sd.setUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb");
			sd.setSpeciality(specialityService.getSpecialityByUuid("cf213609-11ab-11ea-a6a0-080027405b36"));
			appointmentServiceService.save(sd);

		}
		
		// Service to test removing service location via CSV
		{
			AppointmentServiceDefinition sd = new AppointmentServiceDefinition();
			sd.setName("ServiceWithLocation");
			sd.setUuid("a1039051-6f34-420d-9779-24e77eb0ca00");
			sd.setLocation(ls.getLocation("Xanadu"));
			appointmentServiceService.save(sd);

		}

	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Verify the 'ServiceWithSpeciality' in #setup() was indeed created with a speciality
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb");
			Assert.assertEquals("Orthopaedic", asd.getSpeciality().getName());
		}
		
		// Verify the 'ServiceWithLocation' in #setup() was indeed created with a speciality
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("a1039051-6f34-420d-9779-24e77eb0ca00");
			Assert.assertEquals("Xanadu", asd.getLocation().getName());
		}
		
		// Replay
		loader.load();
		
		// Verify creation of appointment service definitions
		{
			Assert.assertEquals(7, appointmentServiceService.getAllAppointmentServices(false).size());
		}
		// Verify adding appointment service location using location uuid
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("762e165a-af27-45fe-ad6e-1fe19db78198");
			Assert.assertEquals("Xanadu", asd.getLocation().getName());
		}
		// Verify adding appointment service location using location name
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("bfff3484-320a-4c1e-84c8-dbe8f0d44e8b");
			Assert.assertEquals("Xanadu", asd.getLocation().getName());
		}
		// Verify adding appointment service speciality using speciality name
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("c12829d8-6bdd-426c-a386-104eed0d2c41");
			Assert.assertEquals("Orthopaedic", asd.getSpeciality().getName());
		}
		// Verify adding appointment service speciality using speciality UUID
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("b4b96cea-a0ed-4bbc-84f0-6c6b4e79f447");
			Assert.assertEquals("Orthopaedic", asd.getSpeciality().getName());
		}
		// Verify adding appointment service start time and end time
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("fc46dedf-5e96-44d4-bd99-bec1d80d15d5");
			Assert.assertEquals("08:00:00", asd.getStartTime().toString());
			Assert.assertEquals("17:00:00", asd.getEndTime().toString());
		}
		// Verify adding appointment service duration
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("762e165a-af27-45fe-ad6e-1fe19db78198");
			Assert.assertEquals(new Integer(30), asd.getDurationMins());
		}
		// Verify adding appointment service max load
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("762e165a-af27-45fe-ad6e-1fe19db78198");
			Assert.assertEquals(new Integer(15), asd.getMaxAppointmentsLimit());
		}
		// Verify adding appointment service label colour
		{
			AppointmentServiceDefinition asd = appointmentServiceService
			        .getAppointmentServiceByUuid("c12829d8-6bdd-426c-a386-104eed0d2c41");
			Assert.assertEquals("#8FBC8F", asd.getColor());
		}
		
		// Verify removing service speciality via CSV
		{
			AppointmentServiceDefinition asd = appointmentServiceService
					.getAppointmentServiceByUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb");
			Assert.assertNull(asd.getSpeciality());
		}
		
		// Verify removing service location via CSV
		{
			AppointmentServiceDefinition asd = appointmentServiceService
					.getAppointmentServiceByUuid("a1039051-6f34-420d-9779-24e77eb0ca00");
			Assert.assertNull(asd.getLocation());
		}
	}
}

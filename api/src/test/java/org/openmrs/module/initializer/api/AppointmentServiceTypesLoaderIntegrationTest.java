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
import org.openmrs.module.appointments.model.AppointmentServiceType;
import org.openmrs.module.appointments.service.AppointmentServiceDefinitionService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.servicetypes.AppointmentServiceTypesLoader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AppointmentServiceTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("appointmentServiceService")
	private AppointmentServiceDefinitionService apts;
	
	@Autowired
	private AppointmentServiceTypesLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify edition
		{
			AppointmentServiceType type = Utils.fetchBahmniAppointmentServiceType("Short follow-up", apts);
			Assert.assertEquals(Utils.fetchBahmniAppointmentServiceDefinition("On-site Appointment", apts),
			    type.getAppointmentServiceDefinition());
			Assert.assertEquals((Integer) 10, type.getDuration());
		}
		// Verify retirement
		{
			AppointmentServiceType type = apts.getAppointmentServiceTypeByUuid("4e0f61df-d1f7-4cff-8d69-6264666daf3b");
			Assert.assertTrue(type.getVoided());
		}
		
	}
}

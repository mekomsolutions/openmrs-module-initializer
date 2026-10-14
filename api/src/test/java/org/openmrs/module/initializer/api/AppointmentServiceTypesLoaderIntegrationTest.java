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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify edition
		{
			AppointmentServiceType type = Utils.fetchBahmniAppointmentServiceType("f378bec4-2d0d-4509-a56e-b709e0a53700",
			    apts);
			Assertions.assertEquals(Utils.fetchBahmniAppointmentServiceDefinition("On-site Appointment", apts),
			    type.getAppointmentServiceDefinition());
			Assertions.assertEquals((Integer) 10, type.getDuration());
		}
		{
			AppointmentServiceType type = Utils.fetchBahmniAppointmentServiceType("0a4624a6-ca81-42e2-a1ab-8f8dca033b83",
			    apts);
			Assertions.assertEquals(Utils.fetchBahmniAppointmentServiceDefinition("Specialized Appointment", apts),
			    type.getAppointmentServiceDefinition());
			Assertions.assertEquals((Integer) 45, type.getDuration());
		}
		// Verify retirement
		{
			AppointmentServiceType type = apts.getAppointmentServiceTypeByUuid("4e0f61df-d1f7-4cff-8d69-6264666daf3b");
			Assertions.assertTrue(type.getVoided());
		}
		// Verify creations
		{
			AppointmentServiceType type = apts.getAppointmentServiceTypeByUuid("ffd7e0f4-33f1-4802-b87a-8d610ba1132d");
			Assertions.assertEquals(Utils.fetchBahmniAppointmentServiceDefinition("Specialized Appointment", apts),
			    type.getAppointmentServiceDefinition());
			Assertions.assertEquals((Integer) 45, type.getDuration());
		}
		{
			AppointmentServiceType type = Utils.fetchBahmniAppointmentServiceType("Complex Bracing", apts);
			Assertions.assertEquals(apts.getAppointmentServiceByUuid("6b220700-4ba2-4846-86a7-a2afa5b6f2eb"),
			    type.getAppointmentServiceDefinition());
			Assertions.assertEquals((Integer) 75, type.getDuration());
		}
	}
}

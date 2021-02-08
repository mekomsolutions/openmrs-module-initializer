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
import org.junit.Test;
import org.openmrs.module.appointments.model.Speciality;
import org.openmrs.module.appointments.service.SpecialityService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.appt.specialities.SpecialitiesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SpecialitiesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("specialityService")
	private SpecialityService sps;
	
	@Autowired
	private SpecialitiesLoader loader;
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif speciality name
		{
			Speciality speciality = sps.getSpecialityByUuid("21ec1632-420f-473c-b380-31ed45214362");
			Assert.assertEquals("New speciality", speciality.getName());
		}
		// Verif renaming
		{
			Speciality speciality = sps.getSpecialityByUuid("c8085c78-e80e-436b-b845-caa3496058a6");
			Assert.assertEquals("Speciality B", speciality.getName());
		}
		// Verif creation without UUID
		{
			Assert.assertEquals(3, sps.getAllSpecialities().size()); //Unfortunately, there is no #specialityService.getSpecialityByName method.
		}
		
	}
}

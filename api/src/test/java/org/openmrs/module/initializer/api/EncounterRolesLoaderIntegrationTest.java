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
import org.openmrs.EncounterRole;
import org.openmrs.api.EncounterService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.er.EncounterRolesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EncounterRolesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("encounterService")
	private EncounterService es;
	
	@Autowired
	private EncounterRolesLoader loader;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadOrderFrequenciesAccordingToCsvFiles() {
		
		loader.load();
		
		{
			EncounterRole er = es.getEncounterRoleByUuid("54ce9816-1062-4636-af95-655066cd6aba");
			Assertions.assertNotNull(er);
			Assertions.assertEquals("Surgeon", er.getName());
			Assertions.assertEquals("Does surgery", er.getDescription());
		}
		{
			EncounterRole er = es.getEncounterRoleByUuid("6eb3cf82-cae3-4d50-a31c-90f9230504c8");
			Assertions.assertNotNull(er);
			Assertions.assertEquals("Plague Doctor", er.getName());
			Assertions.assertTrue(er.getRetired());
		}
		{
			EncounterRole er = es.getEncounterRoleByName("Anesthesiologist");
			Assertions.assertNotNull(er);
		}
		{
			EncounterRole er = es.getEncounterRoleByUuid("09a55bcc-2ce2-46a2-836c-4a56dce9d239");
			Assertions.assertNotNull(er);
			Assertions.assertEquals("Phlebotomist", er.getName());
			Assertions.assertEquals("Responsible for drawing blood", er.getDescription());
		}
	}
}

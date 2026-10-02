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
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.pit.PatientIdentifierTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PatientIdentifierTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("patientService")
	private PatientService ps;
	
	@Autowired
	private PatientIdentifierTypesLoader loader;
	
	@BeforeEach
	public void setup() {
	}
	
	@Test
	public void load_shouldLoadPatientIdentifierTypesAccordingToCsvFiles() {
		
		loader.load();
		
		{ // new PIT loaded with all expected fields
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByUuid("73f4f1d6-6086-41d5-a0f1-6d688a4b10af");
			Assertions.assertNotNull(pit);
			Assertions.assertEquals("Hospital ID", pit.getName());
			Assertions.assertEquals("The patient ID for the hospital. You know the one", pit.getDescription());
			Assertions.assertTrue(pit.getRequired());
			Assertions.assertEquals("H-\\d\\d\\d\\d-\\d", pit.getFormat());
			Assertions.assertEquals("Must have format H-####-C where C is the Luhn check digit", pit.getFormatDescription());
			Assertions.assertEquals("org.openmrs.patient.impl.LuhnIdentifierValidator", pit.getValidator());
			Assertions.assertEquals(PatientIdentifierType.LocationBehavior.NOT_USED, pit.getLocationBehavior());
			Assertions.assertEquals(PatientIdentifierType.UniquenessBehavior.UNIQUE, pit.getUniquenessBehavior());
		}
		{ // Old Identification Number should be voided
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByUuid("2f470aa8-1d73-43b7-81b5-01f0c0dfa53c");
			Assertions.assertNotNull(pit);
			Assertions.assertTrue(pit.getRetired());
		}
		{ // Test National ID should have fields modified
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByUuid("b0d10dc0-d8ce-11e3-9c1a-0800200c9a66");
			Assertions.assertNotNull(pit);
			Assertions.assertEquals("Test National ID No", pit.getName());
			Assertions.assertEquals("Changing all the fields", pit.getDescription());
			Assertions.assertTrue(pit.getRequired());
			Assertions.assertEquals("\\a\\a\\a\\d\\d\\d\\d", pit.getFormat());
			Assertions.assertEquals("Three letters and four numbers", pit.getFormatDescription());
			Assertions.assertEquals("org.openmrs.patient.impl.LuhnIdentifierValidator", pit.getValidator());
			Assertions.assertEquals(PatientIdentifierType.LocationBehavior.REQUIRED, pit.getLocationBehavior());
			Assertions.assertEquals(PatientIdentifierType.UniquenessBehavior.LOCATION, pit.getUniquenessBehavior());
		}
		{ // Minimal ID created with minimal required specification
			PatientIdentifierType pit = ps.getPatientIdentifierTypeByName("Minimal ID");
			Assertions.assertNotNull(pit);
		}
	}
}

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
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptSourcesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptSourcesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService service;
	
	@Autowired
	private ConceptSourcesLoader loader;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
	}
	
	@Test
	public void load_shouldLoadConceptSourcesAccordingToCsvFiles() {
		
		loader.load();
		
		{ // created with uuid and description
			ConceptSource c = service.getConceptSourceByName("Mexico");
			Assertions.assertNotNull(c);
			Assertions.assertEquals("adbd4dc1-eb52-4670-8a69-bb646cef9cd7", c.getUuid());
			Assertions.assertEquals("Reference codes for the Mexican MoH", c.getDescription());
		}
		{ // retired 
			ConceptSource c = service.getConceptSourceByName("Cambodia");
			Assertions.assertNotNull(c);
			Assertions.assertTrue(c.getRetired());
		}
		{ // created without uuid
			ConceptSource c = service.getConceptSourceByName("Peru");
			Assertions.assertNotNull(c);
			Assertions.assertEquals("Reference terms for Peru", c.getDescription());
		}
		{ // edited CIEL to change description
			ConceptSource c = service.getConceptSourceByUuid("245dd8d9-ed8e-4126-8866-d99d140d50b7");
			Assertions.assertEquals("CIEL", c.getName()); // unchanged
			Assertions.assertEquals("The people's terminology source", c.getDescription());
		}
		{ // created with HL7 code
			ConceptSource c = service.getConceptSourceByHL7Code("SCT");
			Assertions.assertNotNull(c);
			Assertions.assertEquals("SNOMED CT", c.getName());
			Assertions.assertEquals("SNOMED Preferred mapping", c.getDescription());
			Assertions.assertEquals("SCT", c.getHl7Code());
		}
		{ // created with Unique ID
			ConceptSource c = service.getConceptSourceByHL7Code("RADLEX");
			Assertions.assertNotNull(c);
			Assertions.assertEquals("RadLex", c.getName());
			Assertions.assertEquals("Radiology Terms", c.getDescription());
			Assertions.assertEquals("RADLEX", c.getHl7Code());
			Assertions.assertEquals("2.16.840.1.113883.6.256", c.getUniqueId());
		}
	}
}

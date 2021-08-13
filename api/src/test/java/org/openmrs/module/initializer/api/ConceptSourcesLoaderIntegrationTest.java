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
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
	}
	
	@Test
	public void load_shouldLoadConceptSourcesAccordingToCsvFiles() {
		
		loader.load();
		
		{ // created with uuid and description
			ConceptSource c = service.getConceptSourceByName("Mexico");
			Assert.assertNotNull(c);
			Assert.assertEquals("adbd4dc1-eb52-4670-8a69-bb646cef9cd7", c.getUuid());
			Assert.assertEquals("Reference codes for the Mexican MoH", c.getDescription());
		}
		{ // retired 
			ConceptSource c = service.getConceptSourceByName("Cambodia");
			Assert.assertNotNull(c);
			Assert.assertTrue(c.getRetired());
		}
		{ // created without uuid
			ConceptSource c = service.getConceptSourceByName("Peru");
			Assert.assertNotNull(c);
			Assert.assertEquals("Reference terms for Peru", c.getDescription());
		}
		{ // edited CIEL to change description
			ConceptSource c = service.getConceptSourceByUuid("245dd8d9-ed8e-4126-8866-d99d140d50b7");
			Assert.assertEquals("CIEL", c.getName()); // unchanged
			Assert.assertEquals("The people's terminology source", c.getDescription());
		}
		{ // created with HL7 code
			ConceptSource c = service.getConceptSourceByHL7Code("SCT");
			Assert.assertNotNull(c);
			Assert.assertEquals("SNOMED CT", c.getName());
			Assert.assertEquals("SNOMED Preferred mapping", c.getDescription());
			Assert.assertEquals("SCT", c.getHl7Code());
		}
		{ // created with Unique ID
			ConceptSource c = service.getConceptSourceByHL7Code("RADLEX");
			Assert.assertNotNull(c);
			Assert.assertEquals("RadLex", c.getName());
			Assert.assertEquals("Radiology Terms", c.getDescription());
			Assert.assertEquals("RADLEX", c.getHl7Code());
			Assert.assertEquals("2.16.840.1.113883.6.256", c.getUniqueId());
		}
	}
}

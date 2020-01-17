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
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c_class.ConceptClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptClassLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService service;
	
	@Autowired
	private ConceptClassLoader loader;
	
	@Test
	public void load_shouldLoadConceptClassesAccordingToCsvFiles() {
		
		loader.load();
		
		{ // created with uuid
			ConceptClass c = service.getConceptClassByName("Medical supply");
			Assert.assertNotNull(c);
			Assert.assertEquals("69d620da-93c4-4767-916e-48f5fe8824c4", c.getUuid());
		}
		{ // retired Procedure
			ConceptClass c = service.getConceptClassByName("Procedure");
			Assert.assertNotNull(c);
			Assert.assertTrue(c.getRetired());
		}
		{ // created without uuid
			ConceptClass c = service.getConceptClassByName("Animal");
			Assert.assertNotNull(c);
		}
	}
}

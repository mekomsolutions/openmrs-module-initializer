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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.c.ConceptMapTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptMapTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService service;
	
	@Autowired
	private ConceptMapTypesLoader loader;
	
	@Test
	public void load_shouldLoadConceptMapTypesAccordingToCsvFiles() {
		
		loader.load();
		
		{ // created with uuid and description
			ConceptMapType t = service.getConceptMapTypeByName("ASSOCIATED-WITH");
			Assert.assertNotNull(t);
			Assert.assertEquals("d3a5e8b1-6c2f-4e7a-9b0d-1f2a3c4d5e6f", t.getUuid());
			Assert.assertEquals("Loosely associated concepts", t.getDescription());
			Assert.assertFalse(t.getIsHidden());
		}
		{ // created without uuid, explicitly not hidden
			ConceptMapType t = service.getConceptMapTypeByName("NARROWER-THAN");
			Assert.assertNotNull(t);
			Assert.assertEquals("Target is narrower than the source", t.getDescription());
			Assert.assertFalse(t.getIsHidden());
		}
		{ // created as hidden
			ConceptMapType t = service.getConceptMapTypeByName("INTERNAL-ONLY");
			Assert.assertNotNull(t);
			Assert.assertTrue(t.getIsHidden());
		}
		{ // edited by uuid: renamed 'related-to' to 'RELATED-TO' and set a description
			ConceptMapType t = service.getConceptMapTypeByUuid("1ccba764-49d6-11e0-8fed-18a905e044dc");
			Assert.assertNotNull(t);
			Assert.assertEquals("RELATED-TO", t.getName());
			Assert.assertEquals("Related but not equivalent", t.getDescription());
			
			List<ConceptMapType> relatedTo = service.getConceptMapTypes(true, true).stream()
			        .filter(m -> "related-to".equalsIgnoreCase(m.getName())).collect(Collectors.toList());
			Assert.assertEquals(1, relatedTo.size()); // renamed in place, no orphan created
		}
		{ // edited by name: an empty 'Is hidden' cell un-hides an existing hidden map type
			ConceptMapType t = service.getConceptMapTypeByUuid("be91ed88-64b1-11e0-b901-18a905e044dc");
			Assert.assertNotNull(t);
			Assert.assertEquals("hidden", t.getName());
			Assert.assertEquals("No longer hidden", t.getDescription());
			Assert.assertFalse(t.getIsHidden());
		}
		{ // retired
			ConceptMapType t = service.getConceptMapTypeByName("is-parent-to");
			Assert.assertNotNull(t);
			Assert.assertTrue(t.getRetired());
			Assert.assertEquals(InitializerConstants.DEFAULT_RETIRE_REASON, t.getRetireReason());
		}
	}
}

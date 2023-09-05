/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.cohort.cat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.cohort.CohortAttributeType;
import org.openmrs.module.cohort.api.CohortService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CohortAttributeTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("cohort.cohortService")
	private CohortService cs;
	
	@Autowired
	private CohortAttributeTypeLoader loader;
	
	@Before
	public void setup() {
	}
	
	@Test
	public void load_shouldLoadCohortAttributeTypesAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verify
		CohortAttributeType cat = cs.getCohortAttributeTypeByName("Test");
		Assert.assertNotNull(cat);
		Assert.assertEquals("This is a test group.", cat.getDescription());
		Assert.assertEquals("09790099-9190-429d-811a-aac9edb8d98e", cat.getUuid());
		Assert.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", cat.getDatatypeClassname());
	}
}

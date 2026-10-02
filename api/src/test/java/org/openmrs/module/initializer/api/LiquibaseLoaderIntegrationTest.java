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
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.LiquibaseLoader;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class LiquibaseLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private LiquibaseLoader loader;
	
	@BeforeEach
	public void setup() {
		System.setProperty("useInMemoryDatabase", "true");
	}
	
	@Test
	public void load_shouldLoadStructuredLiquibaseChangesets() throws Exception {
		// TODO This test fails on GitHub Actions but the failure cannot be reproduced so for now, skip it
		assumeTrue(System.getenv("GITHUB_ENV") == null);
		
		// Replay
		loader.load();
		
		// Verify
		Assertions.assertNotNull(Context.getConceptService().getConceptByUuid("fbb05a72-b923-4b35-bbb6-5cbcfdc295ed"));
		Assertions.assertNotNull(Context.getConceptService().getConceptByUuid("ae848d15-6a04-4ad5-b711-a4cf711a566e"));
		Assertions
		        .assertNotNull(Context.getEncounterService().getEncounterTypeByUuid("13c7556b-e868-4612-a631-bfdbed24c9f0"));
		Assertions
		        .assertNotNull(Context.getEncounterService().getEncounterTypeByUuid("4c384d33-6fc4-4b99-a3b3-efc285409e7f"));
		Assertions
		        .assertNotNull(Context.getEncounterService().getEncounterTypeByUuid("4bf982f0-8053-4757-a45e-5da777ffe0f6"));
	}
}

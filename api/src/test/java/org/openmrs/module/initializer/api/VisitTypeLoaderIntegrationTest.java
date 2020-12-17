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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.visittypes.VisitTypeLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class VisitTypeLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {

	@Autowired
	@Qualifier("visitService")
	private VisitService vs;

	@Autowired
	private VisitTypeLoader loader;

	@Before
	public void setup() {

		// VisitType added directly
		{
			VisitType v = new VisitType();
			v.setUuid("2bcf7212-d218-4572-8893-25c4pob71934");
			v.setName("Malnutrition");
			v.setDescription("Malnutrition Visit");
			v = vs.saveVisitType(v);
		}

	}

	@Test
	public void load_shouldLoadVisitTypeAccordingToCsvFiles() {

		// Replay
		loader.load();

		// Test to find visitTypes with the phrase TB...
		{
			List<VisitType> v = vs.getVisitTypes("TB");
			Assert.assertNotNull(v);
			Assert.assertEquals(2, v.size());
		}
		// Test to confirm the VisitService Test added new VisitType
		{
			VisitType v = vs.getVisitTypeByUuid("2bcf7212-d218-4572-8893-25c4pob71934");
			Assert.assertNotNull(v);
			Assert.assertEquals("Malnutrition Visit", v.getDescription());
		}

		// Test to confirm Description is added when none is given in csv
		{
			VisitType v = vs.getVisitTypeByUuid("l3cf7209-d218-4572-8893-25c4b5b71934");
			Assert.assertEquals("ANTENATAL Visit", v.getDescription());
		}
		// Test to show that we can override the description using only the Name without
		// uuid
		// Not working yet...Instead a new Visit Type is created
		{
			VisitType v = vs.getVisitTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e4");
			// Need to make this pass
			// Assert.assertEquals("TB Description", v.getDescription());
		}

		// Test to show that Iniz can retire VisitType using uuid only
		{
			VisitType v = vs.getVisitTypeByUuid("l3cf7209-d218-4572-8893-25c4b5b71934");
			// We need to make this pass
			// Assert.assertNull(v);
		}

		// Test to show that Iniz can retire VisitType using name only
		{
			VisitType v = vs.getVisitTypeByUuid("2bcf7212-d218-4572-88o9-25c4b5b71934");
			// We need to make this pass
			// Assert.assertNull(v);
		}
	}
}

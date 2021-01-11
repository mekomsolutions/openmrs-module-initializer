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

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.visittypes.VisitTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class VisitTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("visitService")
	private VisitService vs;
	
	@Autowired
	private VisitTypesLoader loader;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadVisitTypeAccordingToCsvFiles() {
		// Pre-asserts
		{
			VisitType vt = vs.getVisitType(16000);
			Assert.assertEquals("OPD", vt.getName());
			Assert.assertEquals("Legacy OPD visit Description", vt.getDescription());
		}
		
		// Replay
		loader.load();
		
		// Verify fetch by name
		{
			List<VisitType> visitTypes = vs.getVisitTypes("TB");
			Assert.assertNotNull(visitTypes);
			Assert.assertThat(visitTypes.size(), is(1));
			
			VisitType vt = visitTypes.get(0);
			Assert.assertEquals("Return TB Clinic Visit", vt.getName());
		}
		// Verify fetch by UUID
		{
			VisitType vt = vs.getVisitTypeByUuid("2bcf7212-d218-4572-8893-25c4pob71934");
			Assert.assertNotNull(vt);
			Assert.assertEquals("Malnutrition", vt.getName());
			Assert.assertEquals("Malnutrition Visit", vt.getDescription());
		}
		{
			VisitType vt = vs.getVisitTypeByUuid("abcf7209-d218-4572-8893-25c4b5b71934");
			Assert.assertEquals("General", vt.getName());
			Assert.assertNull(vt.getDescription());
		}
		// Verify edition (description)
		{
			VisitType vt = vs.getVisitTypeByUuid("287463d3-2233-4c69-9851-5841a1f5e109");
			Assert.assertEquals("OPD", vt.getName());
			Assert.assertEquals("OPD Visit", vt.getDescription());
		}
		// Verify edition using name as pivot in CSV
		{
			VisitType vt = vs.getVisitTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e4");
			Assert.assertEquals("Return TB Clinic Visit", vt.getName());
			Assert.assertEquals("Edited Return TB Clinic Visit Description", vt.getDescription());
		}
		// Verify retirement using UUID as pivot in CSV
		{
			VisitType vt = vs.getVisitTypeByUuid("e1d02b2e-cc85-48ac-a5bd-b0e4beea96e0");
			Assert.assertEquals(true, vt.getRetired());
		}
		
		// Verify retirement using name as pivot in CSV
		{
			List<VisitType> visitTypes = vs.getVisitTypes("Initial HIV");
			Assert.assertNotNull(visitTypes);
			Assert.assertThat(visitTypes.size(), is(1));
			
			VisitType vt = visitTypes.get(0);
			Assert.assertTrue(vt.getRetired());
		}
		
	}
}

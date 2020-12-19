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
import org.openmrs.module.initializer.api.visittypes.VisitTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class VisitTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("visitService")
	private VisitService vs;
	
	@Autowired
	private VisitTypesLoader loader;
	
	@Test
	public void load_shouldLoadVisitTypeAccordingToCsvFiles() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
		
		// Replay
		loader.load();
		
		// Test to find visitTypes with the phrase TB...
		{
			List<VisitType> vts = vs.getVisitTypes("TB");
			Assert.assertNotNull(vts);
			Assert.assertEquals(2, vts.size());
		}
		// Test to confirm the VisitService added new VisitType
		{
			VisitType vt = vs.getVisitTypeByUuid("2bcf7212-d218-4572-8893-25c4pob71934");
			Assert.assertNotNull(vt);
			Assert.assertEquals("Malnutrition Visit", vt.getDescription());
		}
		
		// Test to confirm Description is added when none is given in csv
		{
			VisitType vt = vs.getVisitTypeByUuid("abcf7209-d218-4572-8893-25c4b5b71934");
			Assert.assertEquals("General Visit", vt.getDescription());
		}
		// Test to confirm Description is changed when uuid is specified in csv
		{
			VisitType vt = vs.getVisitTypeByUuid("xyzf7209-d218-4572-8893-xyz4b5b71934");
			//need to make this pass and add the line to the csv
			//Assert.assertEquals("RANDOM V Description", vt.getDescription());
		}
		// Test to show that we can override the description using only the Name without
		// uuid
		// Not working yet...Instead a new Visit Type is created
		{
			VisitType vt = vs.getVisitTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e4");
			// Need to make this pass
			// Assert.assertEquals("TB Description", vt.getDescription());
		}
		
		// Test to show that Iniz can retire VisitType using uuid only
		{
			VisitType vt = vs.getVisitTypeByUuid("l3cf7209-d218-4572-8893-25c4b5b71934");
			Assert.assertEquals(true, vt.isRetired());
		}
		
		// Test to show that Iniz can retire VisitType using name only
		{
			VisitType vt = vs.getVisitTypeByUuid("2bcf7212-d218-4572-88o9-25c4b5b71934");
			// We need to make this pass
			// Assert.assertNull(vt);
		}
	}
}

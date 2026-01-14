/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.BillableServiceService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.initializer.api.billing.BillableServicesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BillableServicesLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_7_Test {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService conceptService;
	
	@Autowired
	private BillableServicesLoader loader;
	
	@Autowired
	private BillableServiceService billableServiceService;
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-concepts-2.4.xml");
		{
			// To be edited
			Concept concept = conceptService.getConceptByUuid("3f6f6c92-8d5c-4a9e-bb1c-d3e00e4f8b71");
			
			BillableService service = new BillableService();
			service.setUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
			service.setName("Orthopedic Therapy");
			service.setShortName("OTHS");
			service.setConcept(concept);
			service.setServiceStatus(BillableServiceStatus.ENABLED);
			billableServiceService.saveBillableService(service);
		}
		
		{
			// To be retired
			Concept concept = conceptService.getConceptByUuid("550e8400-e29b-41d4-a716-446655440000");
			
			BillableService service = new BillableService();
			service.setUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
			service.setName("Nutrition");
			service.setShortName("NUC");
			service.setRetired(false);
			service.setConcept(concept);
			service.setServiceType(concept);
			service.setServiceStatus(BillableServiceStatus.ENABLED);
			billableServiceService.saveBillableService(service);
		}
	}
	
	@Test
	public void load_shouldLoadBillableServicesAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify creation
		{
			BillableService service = billableServiceService
			        .getBillableServiceByUuid("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
			Assert.assertNotNull(service);
			Assert.assertNotNull(service.getServiceType());
			Assert.assertEquals("Antenatal Care", service.getName());
			Assert.assertEquals(conceptService.getConceptByUuid("d4b4b6ef-6f3e-43a4-a3b9-9c56f3a1e2d8").getId(),
			    service.getConcept().getId());
			Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
		}
		
		// Verify edition
		{
			BillableService service = billableServiceService
			        .getBillableServiceByUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
			Assert.assertNotNull(service);
			Assert.assertNotNull(service.getServiceType());
			Assert.assertEquals("Orthopedic Modified", service.getName());
			Assert.assertEquals(conceptService.getConceptByUuid("3f6f6c92-8d5c-4a9e-bb1c-d3e00e4f8b71").getId(),
			    service.getConcept().getId());
			Assert.assertEquals(BillableServiceStatus.DISABLED, service.getServiceStatus());
		}
		
		// Verify retirement
		{
			BillableService service = billableServiceService
			        .getBillableServiceByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
			Assert.assertTrue(service.getRetired());
			
		}
	}
}

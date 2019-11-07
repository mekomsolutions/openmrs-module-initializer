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
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.ot.OrderTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class OrderTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("orderService")
	private OrderService os;
	
	@Autowired
	private OrderTypesLoader loader;
	
	@Before
	public void setup() {
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif creation of order type
		{
			OrderType ot = os.getOrderTypeByName("New Lab Order");
			Assert.assertEquals("8189b409-3f10-11e4-adec-0800271c1b75", ot.getUuid());
			Assert.assertEquals("New Lab Order", ot.getName());
			Assert.assertEquals("An order for laboratory tests created by Iniz", ot.getDescription());
		}
		
		// Verif parent order type
		{
			OrderType ot = os.getOrderTypeByName("New Lab Order");
			Assert.assertEquals("01727040-a587-484d-b66a-f0afbae6c281", ot.getParent().getUuid());
		}
		
		// Verif renaming
		{
			OrderType ot = os.getOrderTypeByUuid("8be5f714-ee92-4d09-939e-2d1897bb2f95");
			Assert.assertEquals("New Name", ot.getName());
		}
		
		// Verif retiring
		{
			OrderType ot = os.getOrderTypeByUuid("96f94b64-6c9e-489d-b258-633878b9af69");
			Assert.assertEquals(true, ot.getRetired());
		}
		
		// Verif bootstrapping by name
		{
			OrderType ot = os.getOrderTypeByName("No Uuid");
			Assert.assertNotNull(ot);
			Assert.assertEquals("For testing processing order type by name", ot.getDescription());
		}
	}
}

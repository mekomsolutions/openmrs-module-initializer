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

	private String parentOrderTypeUuid = "01727040-a587-484d-b66a-f0afbae6c281";
	
	private String newLabOrderTypeUuid = "8189b409-3f10-11e4-adec-0800271c1b75";
	
	private String newLabOrderTypeName = "New Lab Order";
	
	private String newLabOrderTypeDesc = "An order for laboratory tests created by Iniz";

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
			Assert.assertEquals(newLabOrderTypeUuid, ot.getUuid());
			Assert.assertEquals(newLabOrderTypeName, ot.getName());
			Assert.assertEquals(newLabOrderTypeDesc, ot.getDescription());
		}

		// Verif parent order type
		{
			OrderType ot = os.getOrderTypeByName("Drug Order");
			Assert.assertEquals(parentOrderTypeUuid, ot.getParent().getUuid());
		}
	}
}

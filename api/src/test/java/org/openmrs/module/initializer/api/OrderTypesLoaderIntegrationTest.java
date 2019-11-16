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

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.ConceptService;
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
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private OrderTypesLoader loader;
	
	@Before
	public void setup() {
		// An order type to rename
		{
			OrderType ot = new OrderType("Old Order Type Name", "For testing renaming an order type.", "org.openmrs.Order");
			ot.setUuid("8be5f714-ee92-4d09-939e-2d1897bb2f95");
			os.saveOrderType(ot);
		}
		{
			OrderType ot = new OrderType("Order Type To Retire", "For testing retiring an order type.", "org.openmrs.Order");
			ot.setUuid("96f94b64-6c9e-489d-b258-633878b9af69");
			ot.setRetired(false);
			os.saveOrderType(ot);
		}
		
		// A couple of concept classes to use with order types
		{
			ConceptClass cc = new ConceptClass();
			cc.setName("Lab orders #1");
			cc.setDescription("Panels");
			cc.setUuid("c652c923-552f-4634-9418-17692a856f03");
			cs.saveConceptClass(cc);
		}
		{
			ConceptClass cc = new ConceptClass();
			cc.setName("Lab orders #2");
			cc.setDescription("Lab Tests");
			cc.setUuid("9ce20038-c1de-4856-b2b1-297d06e58326");
			cs.saveConceptClass(cc);
		}
		
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif creation of an order type with concept classes and a parent
		{
			OrderType ot = os.getOrderTypeByName("Iniz Lab Order");
			Assert.assertEquals("8189b409-3f10-11e4-adec-0800271c1b75", ot.getUuid());
			Assert.assertEquals("An order for laboratory tests created by Iniz", ot.getDescription());
			Assert.assertEquals("org.openmrs.Order", ot.getJavaClassName());
			Assert.assertEquals(Order.class, ot.getJavaClass());
			Assert.assertEquals(2, ot.getConceptClasses().size());
			List<String> classesUuids = ot.getConceptClasses().stream().map(cc -> cc.getUuid()).collect(Collectors.toList());
			assertThat(classesUuids,
			    hasItems("c652c923-552f-4634-9418-17692a856f03", "9ce20038-c1de-4856-b2b1-297d06e58326"));
			List<String> classesNames = ot.getConceptClasses().stream().map(cc -> cc.getName()).collect(Collectors.toList());
			assertThat(classesNames, hasItems("Lab orders #1", "Lab orders #2"));
			Assert.assertEquals("01727040-a587-484d-b66a-f0afbae6c281", ot.getParent().getUuid());
		}
		
		// Verif renaming an existing  order type
		{
			OrderType ot = os.getOrderTypeByUuid("8be5f714-ee92-4d09-939e-2d1897bb2f95");
			Assert.assertEquals("New Order Type Name", ot.getName());
		}
		
		// Verif retiring an existing order type
		{
			OrderType ot = os.getOrderTypeByUuid("96f94b64-6c9e-489d-b258-633878b9af69");
			Assert.assertEquals(true, ot.getRetired());
		}
		
		// Verif bootstrapping by name only
		{
			OrderType ot = os.getOrderTypeByName("Order Type Without UUID");
			Assert.assertNotNull(ot);
			Assert.assertEquals("For testing loading order types by name.", ot.getDescription());
		}
		
		// Verif another Java class name than 'org.openmrs.Order'
		{
			OrderType ot = os.getOrderTypeByUuid("6721493b-ec7c-4e3f-980a-5be3a09585ce");
			Assert.assertNotNull(ot);
			Assert.assertEquals("org.openmrs.DrugOrder", ot.getJavaClassName());
			Assert.assertEquals(DrugOrder.class, ot.getJavaClass());
		}
	}
}

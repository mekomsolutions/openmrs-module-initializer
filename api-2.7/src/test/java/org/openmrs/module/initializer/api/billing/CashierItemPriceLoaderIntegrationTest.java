/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.billing.api.BillableServiceService;
import org.openmrs.module.billing.api.CashierItemPriceService;
import org.openmrs.module.billing.api.PaymentModeService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.billing.api.model.CashierItemPrice;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_7_Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CashierItemPriceLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_7_Test {
	
	private static final String BILLABLE_SERVICE_UUID = "a0f7d8a1-4fa2-418c-aa8a-cccccccc0001";
	
	private static final String PAYMENT_MODE_UUID = "526bf278-ba81-4436-b867-c2f6641d060a";
	
	private static final String UUID_TO_CREATE = "c1c1c1c1-0000-0000-0000-000000000001";
	
	private static final String UUID_TO_EDIT = "c1c1c1c1-0000-0000-0000-000000000002";
	
	private static final String UUID_TO_RETIRE = "c1c1c1c1-0000-0000-0000-000000000003";
	
	@Autowired
	private CashierItemPriceService cashierItemPriceService;
	
	@Autowired
	private BillableServiceService billableServiceService;
	
	@Autowired
	private PaymentModeService paymentModeService;
	
	@Autowired
	private CashierItemPriceLoader loader;
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-concepts-2.7.xml");
		
		BillableService billableService = new BillableService();
		billableService.setUuid(BILLABLE_SERVICE_UUID);
		billableService.setName("Generic Billable Service");
		billableService.setServiceStatus(BillableServiceStatus.ENABLED);
		billableServiceService.saveBillableService(billableService);
		
		PaymentMode paymentMode = paymentModeService.getPaymentModeByUuid(PAYMENT_MODE_UUID);
		
		CashierItemPrice toEdit = new CashierItemPrice();
		toEdit.setUuid(UUID_TO_EDIT);
		toEdit.setName("Ortho Service Price");
		toEdit.setPrice(new BigDecimal("200.00"));
		toEdit.setBillableService(billableService);
		toEdit.setPaymentMode(paymentMode);
		cashierItemPriceService.saveCashierItemPrice(toEdit);
		
		CashierItemPrice toRetire = new CashierItemPrice();
		toRetire.setUuid(UUID_TO_RETIRE);
		toRetire.setName("Nutrition Service Price");
		toRetire.setPrice(new BigDecimal("75.00"));
		toRetire.setBillableService(billableService);
		toRetire.setPaymentMode(paymentMode);
		toRetire.setRetired(false);
		cashierItemPriceService.saveCashierItemPrice(toRetire);
	}
	
	@Test
	public void load_shouldLoadCashierItemPricesAccordingToCsvFiles() {
		loader.load();
		
		CashierItemPrice created = cashierItemPriceService.getCashierItemPriceByUuid(UUID_TO_CREATE);
		assertNotNull(created);
		assertEquals("ANC Service Price", created.getName());
		assertEquals(new BigDecimal("150.00"), created.getPrice());
		assertNotNull(created.getBillableService());
		assertEquals(BILLABLE_SERVICE_UUID, created.getBillableService().getUuid());
		assertNotNull(created.getPaymentMode());
		assertEquals(PAYMENT_MODE_UUID, created.getPaymentMode().getUuid());
		
		CashierItemPrice edited = cashierItemPriceService.getCashierItemPriceByUuid(UUID_TO_EDIT);
		assertNotNull(edited);
		assertEquals("Ortho Service Price (Modified)", edited.getName());
		assertEquals(new BigDecimal("250.50"), edited.getPrice());
		assertEquals("2b1b9aae-5d35-43dd-9214-3fd370fd7737", edited.getPaymentMode().getUuid());
		
		CashierItemPrice retired = cashierItemPriceService.getCashierItemPriceByUuid(UUID_TO_RETIRE);
		assertNotNull(retired);
		assertTrue(retired.getRetired());
	}
}

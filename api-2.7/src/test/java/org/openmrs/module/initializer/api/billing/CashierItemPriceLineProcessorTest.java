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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.billing.api.BillableServiceService;
import org.openmrs.module.billing.api.PaymentModeService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.CashierItemPrice;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.stockmanagement.api.StockManagementService;
import org.openmrs.module.stockmanagement.api.model.StockItem;

public class CashierItemPriceLineProcessorTest {

	private static final String PAYMENT_MODE_UUID = "pm-0000-0000-0000-000000000001";

	private static final String STOCK_ITEM_UUID = "si-0000-0000-0000-000000000001";

	private static final String BILLABLE_SERVICE_UUID = "bs-0000-0000-0000-000000000001";

	private static final String[] HEADERS = new String[] { "Uuid", "name", "price", "payment mode", "stock item",
	        "billable service" };

	@Mock
	private PaymentModeService paymentModeService;

	@Mock
	private StockManagementService stockManagementService;

	@Mock
	private BillableServiceService billableServiceService;

	private CashierItemPriceLineProcessor processor;

	private PaymentMode paymentMode;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		processor = new CashierItemPriceLineProcessor(paymentModeService, stockManagementService, billableServiceService);
		paymentMode = new PaymentMode();
		paymentMode.setUuid(PAYMENT_MODE_UUID);
		when(paymentModeService.getPaymentModeByUuid(PAYMENT_MODE_UUID)).thenReturn(paymentMode);
	}

	@Test
	public void fill_shouldClearBillableServiceWhenSwitchingToStockItem() {
		CashierItemPrice instance = new CashierItemPrice();
		BillableService existing = new BillableService();
		existing.setUuid(BILLABLE_SERVICE_UUID);
		instance.setBillableService(existing);

		StockItem stockItem = new StockItem();
		stockItem.setUuid(STOCK_ITEM_UUID);
		when(stockManagementService.getStockItemByUuid(STOCK_ITEM_UUID)).thenReturn(stockItem);

		CsvLine line = new CsvLine(HEADERS,
		        new String[] { "", "Switched Price", "100.00", PAYMENT_MODE_UUID, STOCK_ITEM_UUID, "" });

		processor.fill(instance, line);

		assertSame(stockItem, instance.getItem());
		assertNull(instance.getBillableService());
	}

	@Test
	public void fill_shouldClearItemWhenSwitchingToBillableService() {
		CashierItemPrice instance = new CashierItemPrice();
		StockItem existing = new StockItem();
		existing.setUuid(STOCK_ITEM_UUID);
		instance.setItem(existing);

		BillableService billableService = new BillableService();
		billableService.setUuid(BILLABLE_SERVICE_UUID);
		when(billableServiceService.getBillableServiceByUuid(BILLABLE_SERVICE_UUID)).thenReturn(billableService);

		CsvLine line = new CsvLine(HEADERS,
		        new String[] { "", "Switched Price", "100.00", PAYMENT_MODE_UUID, "", BILLABLE_SERVICE_UUID });

		processor.fill(instance, line);

		assertSame(billableService, instance.getBillableService());
		assertNull(instance.getItem());
	}

	@Test
	public void fill_shouldSetBasicFields() {
		CashierItemPrice instance = new CashierItemPrice();
		BillableService billableService = new BillableService();
		billableService.setUuid(BILLABLE_SERVICE_UUID);
		when(billableServiceService.getBillableServiceByUuid(BILLABLE_SERVICE_UUID)).thenReturn(billableService);

		CsvLine line = new CsvLine(HEADERS,
		        new String[] { "", "ANC Service Price", "150.00", PAYMENT_MODE_UUID, "", BILLABLE_SERVICE_UUID });

		processor.fill(instance, line);

		assertEquals("ANC Service Price", instance.getName());
		assertEquals(new BigDecimal("150.00"), instance.getPrice());
		assertSame(paymentMode, instance.getPaymentMode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldRejectWhenBothAssociationsSet() {
		CashierItemPrice instance = new CashierItemPrice();
		CsvLine line = new CsvLine(HEADERS, new String[] { "", "Price", "100.00", PAYMENT_MODE_UUID, STOCK_ITEM_UUID,
		        BILLABLE_SERVICE_UUID });
		processor.fill(instance, line);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fill_shouldRejectWhenNeitherAssociationSet() {
		CashierItemPrice instance = new CashierItemPrice();
		CsvLine line = new CsvLine(HEADERS, new String[] { "", "Price", "100.00", PAYMENT_MODE_UUID, "", "" });
		processor.fill(instance, line);
	}
}
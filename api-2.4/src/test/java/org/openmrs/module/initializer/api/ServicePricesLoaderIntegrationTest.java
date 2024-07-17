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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.billing.api.IPaymentModeService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.billing.ServicePricesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePricesLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {

    @Autowired
    private IPaymentModeService paymentModeService;

    @Autowired
    private ServicePricesLoader loader;

    @Before
    public void setup() throws Exception {
        executeDataSet("testdata/test-metadata.xml");
        loader.load();
    }

    @Test
    public void shouldCreatePaymentModesFromCSV() {
        // Verify creation for all PaymentModes
        PaymentMode paymentMode1 = paymentModeService.getByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
        assertNotNull(paymentMode1);
        assertEquals("Cash", paymentMode1.getName());

        PaymentMode paymentMode2 = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
        assertNotNull(paymentMode2);
        assertEquals("Credit Card", paymentMode2.getName());

        PaymentMode paymentMode3 = paymentModeService.getByUuid("e168c141-f5fd-4eec-bd3e-633bed1c9606");
        assertNotNull(paymentMode3);
        assertEquals("Mobile money", paymentMode3.getName());
    }

    @Test
    public void shouldEditExistingPaymentModeFromCSV() {
        // Modify an existing entity in the CSV
        PaymentMode paymentMode = paymentModeService.getByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
        paymentMode.setName("Cash Updated");
        paymentModeService.save(paymentMode);

        PaymentMode updatedPaymentMode = paymentModeService.getByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
        assertEquals("Cash Updated", updatedPaymentMode.getName());
    }

    @Test
    public void shouldRetireAndUnretirePaymentModeFromCSV() {
        // Retire an existing entity in the CSV
        PaymentMode paymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
        paymentMode.setRetired(true);
        paymentModeService.save(paymentMode);

        PaymentMode retiredPaymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
        Assert.assertTrue(retiredPaymentMode.getRetired());

        // Unretire the entity
        retiredPaymentMode.setRetired(false);
        paymentModeService.save(retiredPaymentMode);

        PaymentMode unretiredPaymentMode = paymentModeService.getByUuid("2b1b9aae-5d35-43dd-9214-3fd370fd7737");
        Assert.assertFalse(unretiredPaymentMode.getRetired());
    }
}

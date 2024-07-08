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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.billing.api.IPaymentModeService;
import org.openmrs.module.billing.api.model.PaymentMode;
import org.openmrs.module.initializer.api.billing.ServicePricesLoader;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ServicePricesLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {
    
    @Autowired
    private IPaymentModeService paymentModeService;
    
    @Autowired
    private ServicePricesLoader loader;
    
    @Before
    public void setup() {
        PaymentMode paymentMode = new PaymentMode();
        paymentMode.setUuid("526bf278-ba81-4436-b867-c2f6641d060a");
        paymentMode.setName("Cash");
        paymentMode.setDescription("Cash payment mode");
        paymentModeService.save(paymentMode);
    }
    
    @Test
    public void load_shouldLoadServicePricesFromCSV() throws Exception {        
        // Replay
        loader.load();
        
        // Verify
        PaymentMode paymentMode = paymentModeService.getByUuid("526bf278-ba81-4436-b867-c2f6641d060a");
        assertNotNull(paymentMode);
        assertEquals("Cash", paymentMode.getName());
        assertEquals("Cash payment mode", paymentMode.getDescription());
    }
}

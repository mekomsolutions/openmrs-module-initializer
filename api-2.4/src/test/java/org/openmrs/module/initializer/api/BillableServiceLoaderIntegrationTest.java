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
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.IBillableItemsService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.initializer.api.billing.BillableServiceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BillableServiceLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Autowired
    private BillableServiceLoader loader;

    @Autowired
    private IBillableItemsService billableItemsService;

    @Test
    public void load_shouldLoadBillableServicesAccordingToCsvFiles() {
        // Replay
        loader.load();

        // Verify fetch
        {
            BillableService service1 = billableItemsService.getByUuid("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            Assert.assertNotNull(service1);
            Assert.assertEquals(conceptService.getConceptByUuid("1592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), service1.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service1.getServiceStatus());

            BillableService service2 = billableItemsService.getByUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            Assert.assertNotNull(service2);
            Assert.assertEquals(conceptService.getConceptByUuid("164949AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), service2.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service2.getServiceStatus());

            BillableService service3 = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertNotNull(service3);
            Assert.assertEquals("Nutrition counseling", service3.getName());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service3.getServiceStatus());
        }

        // Modify an existing entity in the CSV
        {
            BillableService service = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            service.setName("Nutrition counseling updated");
            service.setServiceStatus(BillableServiceStatus.DISABLED);
            billableItemsService.save(service);

            BillableService updatedService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals("Nutrition counseling updated", updatedService.getName());
            Assert.assertEquals(BillableServiceStatus.DISABLED, updatedService.getServiceStatus());
        }

        // Retire and un-retire an existing entity via CSV
        {
            BillableService service = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            service.setServiceStatus(BillableServiceStatus.DISABLED);
            billableItemsService.save(service);

            BillableService retiredService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals(BillableServiceStatus.DISABLED, retiredService.getServiceStatus());

            // Unretire the entity
            retiredService.setServiceStatus(BillableServiceStatus.ENABLED);
            billableItemsService.save(retiredService);

            BillableService unretiredService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals(BillableServiceStatus.ENABLED, unretiredService.getServiceStatus());
        }
    }
}

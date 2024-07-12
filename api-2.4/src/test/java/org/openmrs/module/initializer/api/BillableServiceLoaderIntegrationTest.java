package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
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
            BillableService service = billableItemsService.getByUuid("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByUuid("1592AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), service.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }
        {
            BillableService service = billableItemsService.getByUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByUuid("164949AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), service.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }
        {
            BillableService service = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertNotNull(service);
            Assert.assertEquals("Nutrition counseling", service.getName());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }

        // Modify an existing entity in the CSV
        {
            BillableService service = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            service.setName("Nutrition counseling updated");
            service.setServiceStatus(BillableServiceStatus.DISABLED);
            billableItemsService.save(service);

            loader.load();

            BillableService updatedService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals("Nutrition counseling updated", updatedService.getName());
            Assert.assertEquals(BillableServiceStatus.ENABLED, updatedService.getServiceStatus());
        }

        // Retire and un-retire an existing entity via CSV (assuming retirement is done by setting status to DISABLED)
        {
            BillableService service = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            service.setServiceStatus(BillableServiceStatus.DISABLED);
            billableItemsService.save(service);

            loader.load();

            BillableService retiredService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals(BillableServiceStatus.DISABLED, retiredService.getServiceStatus());

            retiredService.setServiceStatus(BillableServiceStatus.ENABLED);
            billableItemsService.save(retiredService);

            loader.load();

            BillableService unretiredService = billableItemsService.getByUuid("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertEquals(BillableServiceStatus.ENABLED, unretiredService.getServiceStatus());
        }
    }
}

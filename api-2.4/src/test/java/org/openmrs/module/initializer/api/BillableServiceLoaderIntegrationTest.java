package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.initializer.api.DomainBaseModuleContextSensitive_2_4_test;
import org.openmrs.module.initializer.api.billing.BillableServiceLoader;
import org.openmrs.module.billing.web.rest.resource.BillableServiceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BillableServiceLoaderIntegrationTest extends DomainBaseModuleContextSensitive_2_4_test {

    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Autowired
    private BillableServiceLoader loader;

    @Autowired
    private BillableServiceResource billableServiceResource;

    @Before
    public void setup() {
        {
            Concept concept = conceptService.getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216");

            BillableService service = new BillableService();
            service.setUuid("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            service.setName("Antenatal Care");
            service.setShortName("ANTC");
            service.setConcept(concept);
            service.setServiceStatus(BillableServiceStatus.ENABLED);
            billableServiceResource.save(service);
        }
        {
            Concept concept = conceptService.getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");

            BillableService service = new BillableService();
            service.setUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            service.setName("Orthopedic Therapy");
            service.setShortName("OTHS");
            service.setConcept(concept);
            service.setServiceStatus(BillableServiceStatus.ENABLED);
            billableServiceResource.save(service);
        }
    }

    @Test
    public void load_shouldLoadBillableServicesAccordingToCsvFiles() {
        //replay
        loader.load();

        //verify
        {
            BillableService service = billableServiceResource.getByUniqueId("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216"), service.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }
        {
            BillableService service = billableServiceResource.getByUniqueId("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f"), service.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }
        {
            BillableService service = billableServiceResource.getByUniqueId("16435ab4-27c3-4d91-b21e-52819bd654d8");
            Assert.assertNotNull(service);
            Assert.assertEquals("Updated Service", service.getName());
            Assert.assertEquals(BillableServiceStatus.DISABLED, service.getServiceStatus());
        }
    }
}

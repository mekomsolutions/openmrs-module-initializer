package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.billing.api.model.BillableService;
import org.openmrs.module.billing.api.model.BillableServiceStatus;
import org.openmrs.module.initializer.api.DomainBaseModuleContextSensitive_2_4_test;
import org.openmrs.module.initializer.api.billing.BillableServiceLoader;
import org.openmrs.module.billing.web.rest.resource.BillableServiceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;

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
            Concept concept = new Concept();
            concept.setShortName(new ConceptName("Antenatal Services", Locale.ENGLISH));
            concept.setConceptClass(conceptService.getConceptClassByName("Misc"));
            concept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
            concept = conceptService.saveConcept(concept);
        }
        {
            Concept concept = new Concept();
            concept.setShortName(new ConceptName("Orthopedic Services", Locale.ENGLISH));
            concept.setConceptClass(conceptService.getConceptClassByName("Misc"));
            concept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
            concept = conceptService.saveConcept(concept);
        }

        {
            BillableService service = new BillableService();
            service.setUuid("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            service.setName("Antenatal Care");
            service.setShortName("ANTC");
            service.setConcept(conceptService.getConceptByName("Antenatal Services"));
            service.setServiceStatus(BillableServiceStatus.ENABLED);
            billableServiceResource.save(service);
        }
        {
            BillableService service = new BillableService();
            service.setUuid("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            service.setName("Orthopedic Therapy");
            service.setShortName("OTHS");
            service.setConcept(conceptService.getConceptByName("Orthopedic Services"));
            service.setServiceStatus(BillableServiceStatus.ENABLED);
            billableServiceResource.save(service);
        }
    }

    @Test
    public void load_shouldLoadBillableServicesAccordingToCsvFiles() {
        loader.load();

        {
            BillableService service = billableServiceResource.getByUniqueId("44ebd6cd-04ad-4eba-8ce1-0de4564bfd17");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByName("Antenatal Services"), service.getConcept());
            Assert.assertEquals(BillableServiceStatus.ENABLED, service.getServiceStatus());
        }
        {
            BillableService service = billableServiceResource.getByUniqueId("a0f7d8a1-4fa2-418c-aa8a-9b358f43d605");
            Assert.assertNotNull(service);
            Assert.assertEquals(conceptService.getConceptByName("Orthopedic Services"), service.getConcept());
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

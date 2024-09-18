package org.openmrs.module.initializer.api;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptReferenceRangeLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptReferenceRangeIntegrationTest extends DomainBaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private ConceptReferenceRangeLoader conceptReferenceRangeLoader;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Test
    public void load_shouldLoadConceptReferenceRangeFromCsvFiles() {
        conceptReferenceRangeLoader.load();

        Session session = sessionFactory.getCurrentSession();
        Query getObsCategoryByCategoryQuery = session.createQuery("from " + ConceptReferenceRange.class.getSimpleName()
                + " where observationCategory = :observationCategory");

        {
            ConceptReferenceRange referenceRange = conceptService.getConceptReferenceRangeByUuid("69d620da-93c4-4767-916e-48f5fe8824c4");
            Assert.assertNotNull(referenceRange);
            Assert.assertEquals("Reference range 1", referenceRange.getName());
        }

        {
            conceptService.getConceptReferenceRangesByConcept();
            assertThat(observationCategories, hasSize(1));
        }
    }
}

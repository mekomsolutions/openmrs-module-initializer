package org.openmrs.module.initializer.api;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir2.model.FhirObservationCategoryMap;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.ocm.FhirObservationCategoryMapLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

public class FhirObservationCategoryMapIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private FhirObservationCategoryMapLoader loader;
	
	@Before
	public void setup() {
		{
			ConceptClass conceptClass = conceptService.getConceptClassByName("Test");
			if (conceptClass == null) {
				conceptClass = new ConceptClass();
				conceptClass.setName("Test");
				conceptClass.setUuid(ConceptClass.TEST_UUID);
				sessionFactory.getCurrentSession().saveOrUpdate(conceptClass);
			}
		}
		
		{
			ConceptClass conceptClass = conceptService.getConceptClassByName("Finding");
			if (conceptClass == null) {
				conceptClass = new ConceptClass();
				conceptClass.setName("Finding");
				conceptClass.setUuid(ConceptClass.FINDING_UUID);
				sessionFactory.getCurrentSession().saveOrUpdate(conceptClass);
			}
		}
		
		{
			FhirObservationCategoryMap observationCategoryMap = new FhirObservationCategoryMap();
			observationCategoryMap.setObservationCategory("laboratory");
			observationCategoryMap.setConceptClass(conceptService.getConceptClassByName("Test"));
			observationCategoryMap.setUuid("2309836d-bf13-4fea-b2d4-87bb997425c7");
			sessionFactory.getCurrentSession().saveOrUpdate(observationCategoryMap);
		}
		
		Context.flushSession();
	}
	
	@Test
	public void loader_shouldLoadFhirObservationCategoryMapsAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify
		Session session = sessionFactory.getCurrentSession();
		Query getObsCategoryByCategoryQuery = session.createQuery("from " + FhirObservationCategoryMap.class.getSimpleName()
		        + " where observationCategory = :observationCategory");
		
		{
			List<FhirObservationCategoryMap> observationCategories = getObsCategoryByCategoryQuery
			        .setParameter("observationCategory", "laboratory").list();
			
			assertThat(observationCategories, hasSize(2));
			assertThat(observationCategories,
			    hasItem(allOf(hasProperty("uuid", equalTo("e518de2a-be31-4202-9772-cc65c3ef7227")),
			        hasProperty("conceptClass", hasProperty("name", equalTo("Test"))))));
			assertThat(observationCategories,
			    hasItem(allOf(hasProperty("uuid", equalTo("2374215a-8808-4eee-b5a5-9190423862a0")),
			        hasProperty("conceptClass", hasProperty("name", equalTo("LabSet"))))));
		}
		
		{
			List<FhirObservationCategoryMap> observationCategories = getObsCategoryByCategoryQuery
			        .setParameter("observationCategory", "exam").list();
			
			assertThat(observationCategories, hasSize(1));
			assertThat(observationCategories.get(0), hasProperty("uuid", equalTo("5f8e2dd2-ce1f-42d3-bb34-55acb3f58c5d")));
			assertThat(observationCategories.get(0).getConceptClass(), hasProperty("name", equalTo("Finding")));
		}
	}
}

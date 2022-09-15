package org.openmrs.module.initializer.api;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.api.dao.FhirConceptSourceDao;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.cs.FhirConceptSourcesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static uk.co.probablyfine.matchers.OptionalMatchers.contains;
import static uk.co.probablyfine.matchers.OptionalMatchers.empty;

public class FhirConceptSourcesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static final String TEST_CONCEPT_SOURCE_NAME = "Test Concept Source";
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private FhirConceptSourceDao dao;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private FhirConceptSourcesLoader loader;
	
	@Before
	public void setup() {
		{
			ConceptSource conceptSource = new ConceptSource();
			conceptSource.setName("CIEL");
			conceptSource.setDescription("Test CIEL Source");
			conceptService.saveConceptSource(conceptSource);
		}
		
		{
			ConceptSource conceptSource = new ConceptSource();
			conceptSource.setName(TEST_CONCEPT_SOURCE_NAME);
			conceptSource.setDescription(TEST_CONCEPT_SOURCE_NAME);
			
			conceptService.saveConceptSource(conceptSource);
			
			FhirConceptSource fhirConceptSource = new FhirConceptSource();
			fhirConceptSource.setName(TEST_CONCEPT_SOURCE_NAME);
			fhirConceptSource.setConceptSource(conceptSource);
			fhirConceptSource.setUuid("21b4e2b5-8ef6-4d72-85c8-9203303f3052");
			fhirConceptSource.setUrl("http://example.com");
			fhirConceptSource.setRetired(false);
			
			sessionFactory.getCurrentSession().save(fhirConceptSource);
		}
	}
	
	@Test
	public void loader_shouldLoadFhirConceptSourcesAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify
		{
			Optional<FhirConceptSource> maybeConceptSource = dao.getFhirConceptSourceByConceptSourceName("CIEL");
			
			assertThat(maybeConceptSource, not(empty()));
			assertThat(maybeConceptSource, contains(hasProperty("conceptSource", hasProperty("name", is("CIEL")))));
			assertThat(maybeConceptSource,
			    contains(hasProperty("url", equalTo("http://api.openconceptlab.org/orgs/CIEL/sources/CIEL"))));
		}
		{
			Optional<FhirConceptSource> maybeConceptSource = dao.getFhirConceptSourceByConceptSourceName("SNOMED CT");
			
			assertThat(maybeConceptSource, not(empty()));
			assertThat(maybeConceptSource, contains(hasProperty("conceptSource", hasProperty("name", is("SNOMED CT")))));
			assertThat(maybeConceptSource, contains(hasProperty("url", equalTo("http://snomed.info/sct/"))));
			assertThat(maybeConceptSource, contains(hasProperty("uuid", equalTo("befca738-1704-4a47-9f6a-0fcacf786061"))));
		}
		{
			FhirConceptSource conceptSource = (FhirConceptSource) sessionFactory.getCurrentSession()
			        .createQuery("from FhirConceptSource where name = :name").setParameter("name", TEST_CONCEPT_SOURCE_NAME)
			        .uniqueResult();
			
			assertThat(conceptSource, notNullValue());
			assertThat(conceptSource.getRetired(), is(true));
		}
	}
	
}

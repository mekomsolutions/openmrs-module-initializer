package org.openmrs.module.initializer.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.api.FhirConceptSourceService;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.cs.FhirConceptSourcesLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.co.probablyfine.matchers.OptionalMatchers.contains;
import static uk.co.probablyfine.matchers.OptionalMatchers.empty;

public class FhirConceptSourcesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static final String TEST_CONCEPT_SOURCE_NAME = "Test Concept Source";
	
	@Autowired
	private FhirConceptSourceService service;
	
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
			
			service.saveFhirConceptSource(fhirConceptSource);
		}
	}
	
	@Test
	public void loader_shouldLoadFhirConceptSourcesAccordingToCsvFiles() {
		// Replay
		loader.load();
		
		// Verify
		{
			ConceptSource ciel = conceptService.getConceptSourceByName("CIEL");
			Optional<FhirConceptSource> maybeConceptSource = service.getFhirConceptSource(ciel);
			
			assertThat(maybeConceptSource, not(empty()));
			assertThat(maybeConceptSource, contains(hasProperty("conceptSource", hasProperty("name", is("CIEL")))));
			assertThat(maybeConceptSource,
			    contains(hasProperty("url", equalTo("http://api.openconceptlab.org/orgs/CIEL/sources/CIEL"))));
		}
		{
			ConceptSource snomedCt = conceptService.getConceptSourceByName("SNOMED CT");
			Optional<FhirConceptSource> maybeConceptSource = service.getFhirConceptSource(snomedCt);
			
			assertThat(maybeConceptSource, not(empty()));
			assertThat(maybeConceptSource, contains(hasProperty("conceptSource", hasProperty("name", is("SNOMED CT")))));
			assertThat(maybeConceptSource, contains(hasProperty("url", equalTo("http://snomed.info/sct/"))));
			assertThat(maybeConceptSource, contains(hasProperty("uuid", equalTo("befca738-1704-4a47-9f6a-0fcacf786061"))));
		}
		
		int numFound = 0;
		for (FhirConceptSource fhirConceptSource : service.getFhirConceptSources()) {
			if (fhirConceptSource.getName().equalsIgnoreCase(TEST_CONCEPT_SOURCE_NAME)) {
				numFound++;
				assertThat(fhirConceptSource.getRetired(), is(true));
			}
		}
		assertThat(numFound, equalTo(1));
	}
	
}

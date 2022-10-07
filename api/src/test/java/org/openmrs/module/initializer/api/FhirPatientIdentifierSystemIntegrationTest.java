package org.openmrs.module.initializer.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.FhirPatientIdentifierSystemService;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.pis.FhirPatientIdentifierSystemLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FhirPatientIdentifierSystemIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private FhirPatientIdentifierSystemService service;
	
	@Autowired
	private FhirPatientIdentifierSystemLoader loader;
	
	PatientIdentifierType firstType;
	
	PatientIdentifierType secondType;
	
	PatientIdentifierType thirdType;
	
	FhirPatientIdentifierSystem thirdSystem;
	
	@Before
	public void setup() {
		{
			firstType = new PatientIdentifierType();
			firstType.setUuid("73f4f1d6-6086-41d5-a0f1-6d688a4b10af");
			firstType.setName("First ID");
			patientService.savePatientIdentifierType(firstType);
		}
		{
			secondType = new PatientIdentifierType();
			secondType.setName("Second ID");
			patientService.savePatientIdentifierType(secondType);
		}
		{
			thirdType = new PatientIdentifierType();
			thirdType.setName("Third ID");
			patientService.savePatientIdentifierType(thirdType);
			
			thirdSystem = new FhirPatientIdentifierSystem();
			thirdSystem.setName("Third System");
			thirdSystem.setPatientIdentifierType(thirdType);
			thirdSystem.setUrl("http://example.com");
			thirdSystem.setUuid("ed2571ad-6a0b-413d-8506-362266c7595e");
			service.saveFhirPatientIdentifierSystem(thirdSystem);
		}
	}
	
	@Test
	public void loader_shouldLoadFhirPatientIdentifierSystemsAccordingToCSVFiles() {
		// Replay
		loader.load();
		
		FhirPatientIdentifierSystem firstSystem = assertSystem(firstType);
		FhirPatientIdentifierSystem secondSystem = assertSystem(secondType);
		FhirPatientIdentifierSystem thirdSystem = assertSystem(thirdType);
		
		// Confirm contents
		assertThat(firstSystem.getUrl(), equalTo("http://openmrs.org/identifier"));
		assertThat(firstSystem.getUuid(), equalTo("87c87473-b394-430b-93d3-b46d0faca26e"));
		assertThat(firstSystem.getName(), equalTo(firstType.getName()));
		assertThat(firstSystem.getRetired(), equalTo(false));
		
		assertThat(secondSystem.getUrl(), equalTo("http://openmrs.org/identifier/2"));
		assertThat(secondSystem.getName(), equalTo(secondType.getName()));
		assertThat(secondSystem.getRetired(), equalTo(false));
		
		assertThat(thirdSystem.getRetired(), equalTo(true));
	}
	
	protected FhirPatientIdentifierSystem assertSystem(PatientIdentifierType type) {
		Optional<FhirPatientIdentifierSystem> system = service.getFhirPatientIdentifierSystem(type);
		assertThat(system.isPresent(), is(true));
		return system.get();
	}
}

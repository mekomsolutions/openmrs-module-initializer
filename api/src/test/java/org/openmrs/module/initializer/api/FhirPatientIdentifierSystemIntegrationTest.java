package org.openmrs.module.initializer.api;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.dao.FhirPatientIdentifierSystemDao;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.pis.FhirPatientIdentifierSystemLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class FhirPatientIdentifierSystemIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private FhirPatientIdentifierSystemDao dao;
	
	@Autowired
	private FhirPatientIdentifierSystemLoader loader;
	
	@Before
	public void setup() {
		{
			PatientIdentifierType identifierType = new PatientIdentifierType();
			identifierType.setName("My OpenMRS ID");
			patientService.savePatientIdentifierType(identifierType);
		}
		{
			PatientIdentifierType identifierType = new PatientIdentifierType();
			identifierType.setName("My Second OpenMRS ID");
			patientService.savePatientIdentifierType(identifierType);
		}
		{
			PatientIdentifierType identifierType = new PatientIdentifierType();
			identifierType.setName("My Third OpenMRS ID");
			patientService.savePatientIdentifierType(identifierType);
			
			FhirPatientIdentifierSystem patientIdentifierSystem = new FhirPatientIdentifierSystem();
			patientIdentifierSystem.setName("My Third OpenMRS ID");
			patientIdentifierSystem.setPatientIdentifierType(identifierType);
			patientIdentifierSystem.setUrl("http://example.com");
			patientIdentifierSystem.setUuid("ed2571ad-6a0b-413d-8506-362266c7595e");
			sessionFactory.getCurrentSession().save(patientIdentifierSystem);
		}
	}
	
	@Test
	public void loader_shouldLoadFhirPatientIdentifierSystemsAccordingToCSVFiles() {
		// Replay
		loader.load();
		
		// verify
		{
			PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName("My OpenMRS ID");
			String patientIdentifierSystemUrl = dao.getUrlByPatientIdentifierType(identifierType);
			
			assertThat(patientIdentifierSystemUrl, equalTo("http://openmrs.org/identifier"));
		}
		{
			PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName("My Second OpenMRS ID");
			String patientIdentifierSystemUrl = dao.getUrlByPatientIdentifierType(identifierType);
			
			assertThat(patientIdentifierSystemUrl, equalTo("http://openmrs.org/identifier/2"));
		}
		{
			PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName("My Third OpenMRS ID");
			FhirPatientIdentifierSystem identifierSystem = (FhirPatientIdentifierSystem) sessionFactory.getCurrentSession()
			        .createQuery("from FhirPatientIdentifierSystem where patientIdentifierType = :patientIdentifierType")
			        .setParameter("patientIdentifierType", identifierType).uniqueResult();
			
			assertThat(identifierSystem, notNullValue());
			assertThat(identifierSystem.getRetired(), is(true));
		}
	}
}

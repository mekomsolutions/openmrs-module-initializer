package org.openmrs.module.initializer.api;

import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.fhir2.api.FhirContactPointMapService;
import org.openmrs.module.fhir2.model.FhirContactPointMap;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.fhir.cpm.FhirContactPointMapLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FhirPatientContactPointMapIntegrationTest extends DomainBaseModuleContextSensitiveTest {

	@Autowired
	private FhirContactPointMapService fhirContactPointMapService;
	
	@Autowired
	private FhirContactPointMapLoader fhirContactPointMapLoader;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private ProviderService providerService;
	
	FhirContactPointMap fhirContactPointMap;
	
	PersonAttributeType personAttributeType;
	
	LocationAttributeType locationAttributeType;
	
	ProviderAttributeType providerAttributeType;
	
	@Before
	public void setup() {
		personAttributeType = new PersonAttributeType();
		providerAttributeType = new ProviderAttributeType();
		locationAttributeType = new LocationAttributeType();
		
		fhirContactPointMap = new FhirContactPointMap();
		fhirContactPointMap.setUuid("fa48acc4-ef1f-46d6-b0af-150b00ddee9d");
		fhirContactPointMap.setAttributeTypeDomain("person");
		fhirContactPointMap.setAttributeTypeId(10001);
		fhirContactPointMap.setSystem(ContactPoint.ContactPointSystem.PHONE);
		fhirContactPointMap.setUse(ContactPoint.ContactPointUse.WORK);
		fhirContactPointMap.setRank(1);
		fhirContactPointMapService.saveFhirContactPointMap(fhirContactPointMap);
		personService.savePersonAttributeType(personAttributeType);
	}
	
	@Test
	public void loader_shouldLoadFhirContactPointMapAccordingToCSVFiles() {
		fhirContactPointMapLoader.load();
		
		FhirContactPointMap firstFhirContactPointMap = assertPersonAttributeType(personAttributeType);
		FhirContactPointMap secondFhirContactPointMap = assertBaseAttributeType(providerAttributeType);
		FhirContactPointMap thirdFhirContactPointMap = assertBaseAttributeType(locationAttributeType);
		
		assertThat(firstFhirContactPointMap.getAttributeTypeDomain(), equalTo("person"));
		assertThat(firstFhirContactPointMap.getSystem(), equalTo(ContactPoint.ContactPointSystem.PHONE));
		assertThat(firstFhirContactPointMap.getUse(), equalTo(ContactPoint.ContactPointUse.WORK));
		
		assertThat(secondFhirContactPointMap.getAttributeTypeDomain(), equalTo("provider"));
		assertThat(firstFhirContactPointMap.getSystem(), equalTo(ContactPoint.ContactPointSystem.EMAIL));
		assertThat(firstFhirContactPointMap.getUse(), equalTo(ContactPoint.ContactPointUse.HOME));
		
		assertThat(thirdFhirContactPointMap.getAttributeTypeDomain(), equalTo("location"));
		assertThat(firstFhirContactPointMap.getSystem(), equalTo(ContactPoint.ContactPointSystem.URL));
		assertThat(firstFhirContactPointMap.getUse(), equalTo(ContactPoint.ContactPointUse.TEMP));
	}
	
	protected FhirContactPointMap assertPersonAttributeType(PersonAttributeType attributeType) {
		Optional<FhirContactPointMap> contactPointMap = fhirContactPointMapService.getFhirContactPointMapForPersonAttributeType(attributeType);
		assertThat(contactPointMap.isPresent(), is(true));
		return contactPointMap.get();
	}
	
	protected FhirContactPointMap assertBaseAttributeType(BaseAttributeType<?> attributeType) {
		Optional<FhirContactPointMap> contactPointMap = fhirContactPointMapService.getFhirContactPointMapForAttributeType(attributeType);
		assertThat(contactPointMap.isPresent(), is(true));
		return contactPointMap.get();
	}
}

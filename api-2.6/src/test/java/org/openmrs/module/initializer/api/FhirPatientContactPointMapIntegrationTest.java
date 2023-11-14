/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
import org.openmrs.module.initializer.DomainBaseModuleContextSensitive_2_6_Test;
import org.openmrs.module.initializer.api.fhir.cpm.FhirContactPointMapLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FhirPatientContactPointMapIntegrationTest extends DomainBaseModuleContextSensitive_2_6_Test {
	
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
		{
			personAttributeType = new PersonAttributeType();
			personAttributeType.setId(10001);
			personAttributeType.setUuid("717ec942-3c4a-11ea-b024-ffc81a23382e");
			
			fhirContactPointMap = new FhirContactPointMap();
			fhirContactPointMap.setUuid("fa48acc4-ef1f-46d6-b0af-150b00ddee9d");
			fhirContactPointMap.setAttributeTypeDomain("person");
			fhirContactPointMap.setAttributeTypeId(10001);
			fhirContactPointMap.setSystem(ContactPoint.ContactPointSystem.PHONE);
			fhirContactPointMap.setUse(ContactPoint.ContactPointUse.WORK);
			fhirContactPointMap.setRank(1);
			fhirContactPointMapService.saveFhirContactPointMap(fhirContactPointMap);
		}
		{
			personAttributeType = new PersonAttributeType();
			personAttributeType.setId(10002);
			personAttributeType.setUuid("PAT_RENAME_NEW_NAME");
			
			fhirContactPointMap = new FhirContactPointMap();
			fhirContactPointMap.setUuid("");
			fhirContactPointMap.setAttributeTypeDomain("person");
			fhirContactPointMap.setAttributeTypeId(10002);
			fhirContactPointMap.setSystem(ContactPoint.ContactPointSystem.PHONE);
			fhirContactPointMap.setUse(ContactPoint.ContactPointUse.WORK);
			fhirContactPointMapService.saveFhirContactPointMap(fhirContactPointMap);
		}
		{
			providerAttributeType = new ProviderAttributeType();
			providerAttributeType.setId(10003);
			providerAttributeType.setUuid("Provider Speciality");
			
			fhirContactPointMap = new FhirContactPointMap();
			fhirContactPointMap.setUuid("bcf23315-a236-42aa-be95-b9e0931e22b0");
			fhirContactPointMap.setAttributeTypeDomain("provider");
			fhirContactPointMap.setAttributeTypeId(10003);
			fhirContactPointMap.setSystem(ContactPoint.ContactPointSystem.EMAIL);
			fhirContactPointMap.setUse(ContactPoint.ContactPointUse.HOME);
			fhirContactPointMap.setRank(2);
			fhirContactPointMapService.saveFhirContactPointMap(fhirContactPointMap);
		}
		{
			locationAttributeType = new LocationAttributeType();
			locationAttributeType.setId(10004);
			locationAttributeType.setUuid("e7aacc6e-d151-4d9e-a808-6ed9ff761212");
			
			fhirContactPointMap = new FhirContactPointMap();
			fhirContactPointMap.setUuid("800e48ba-666c-445c-b871-68e54eec6de8");
			fhirContactPointMap.setAttributeTypeDomain("location");
			fhirContactPointMap.setAttributeTypeId(10004);
			fhirContactPointMap.setSystem(ContactPoint.ContactPointSystem.PHONE);
			fhirContactPointMap.setUse(ContactPoint.ContactPointUse.TEMP);
			fhirContactPointMap.setRank(3);
			fhirContactPointMapService.saveFhirContactPointMap(fhirContactPointMap);
		}
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
		assertThat(secondFhirContactPointMap.getSystem(), equalTo(ContactPoint.ContactPointSystem.EMAIL));
		assertThat(secondFhirContactPointMap.getUse(), equalTo(ContactPoint.ContactPointUse.HOME));
		
		assertThat(thirdFhirContactPointMap.getAttributeTypeDomain(), equalTo("location"));
		assertThat(thirdFhirContactPointMap.getSystem(), equalTo(ContactPoint.ContactPointSystem.PHONE));
		assertThat(thirdFhirContactPointMap.getUse(), equalTo(ContactPoint.ContactPointUse.TEMP));
	}
	
	protected FhirContactPointMap assertPersonAttributeType(PersonAttributeType attributeType) {
		Optional<FhirContactPointMap> contactPointMap = fhirContactPointMapService
		        .getFhirContactPointMapForPersonAttributeType(attributeType);
		assertThat(contactPointMap.isPresent(), is(true));
		return contactPointMap.get();
	}
	
	protected FhirContactPointMap assertBaseAttributeType(BaseAttributeType<?> attributeType) {
		Optional<FhirContactPointMap> contactPointMap = fhirContactPointMapService
		        .getFhirContactPointMapForAttributeType(attributeType);
		assertThat(contactPointMap.isPresent(), is(true));
		return contactPointMap.get();
	}
}

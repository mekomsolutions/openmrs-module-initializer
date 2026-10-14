package org.openmrs.module.initializer.api.attributes.types;

import static org.hamcrest.CoreMatchers.is;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.LOCATION;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AttributeTypesProxyServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	private final static String LOCATION_ATT_TYPE_UUID = "9eca4f4e-707f-4bb8-8289-2f9b6e93803c";
	
	@Autowired
	public AttributeTypesProxyService service;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetAttributeType() {
		// Replay
		BaseAttributeType<?> attType = service.getAttributeTypeByUuid(LOCATION_ATT_TYPE_UUID, LOCATION);
		
		// Verif
		Assertions.assertNotNull(attType);
		MatcherAssert.assertThat(attType.getName(), is("Location Code"));
		Assertions.assertTrue(attType instanceof LocationAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveAttributeType() {
		// Setup
		BaseAttributeType<?> attType = ls.getLocationAttributeType(1089);
		
		// Replay (name edition)
		String newName = RandomStringUtils.random(30);
		attType.setName(newName);
		attType = service.saveAttributeType(attType);
		
		// Verif
		attType = service.getAttributeTypeByName(newName, LOCATION);
		Assertions.assertNotNull(attType);
		MatcherAssert.assertThat(attType.getId(), is(1089));
	}
}

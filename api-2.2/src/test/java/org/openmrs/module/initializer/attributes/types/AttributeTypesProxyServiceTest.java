package org.openmrs.module.initializer.attributes.types;

import static org.hamcrest.CoreMatchers.is;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.CONCEPT;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesProxyService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AttributeTypesProxyServiceTest extends BaseModuleContextSensitiveTest {
	
	private final static String CONCEPT_ATT_TYPE_UUID = "47666db8-a51c-4d9d-a847-98138404f1e3";
	
	@Autowired
	public AttributeTypesProxyService service;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-metadata-2.2.xml");
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetAttributeType() {
		// Replay
		BaseAttributeType<?> attType = service.getAttributeTypeByUuid(CONCEPT_ATT_TYPE_UUID, CONCEPT);
		
		// Verif
		Assert.assertNotNull(attType);
		Assert.assertThat(attType.getName(), is("Concept Family"));
		Assert.assertTrue(attType instanceof ConceptAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveAttributeType() {
		// Setup
		BaseAttributeType<?> attType = cs.getConceptAttributeType(1089);
		
		// Replay (name edition)
		String newName = RandomStringUtils.random(30);
		attType.setName(newName);
		attType = service.saveAttributeType(attType);
		
		// Verif
		attType = service.getAttributeTypeByName(newName, CONCEPT);
		Assert.assertNotNull(attType);
		Assert.assertThat(attType.getId(), is(1089));
	}
}

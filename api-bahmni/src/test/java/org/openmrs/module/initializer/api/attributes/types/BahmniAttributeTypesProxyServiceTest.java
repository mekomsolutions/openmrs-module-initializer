package org.openmrs.module.initializer.api.attributes.types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.PROGRAM;

import org.apache.commons.lang.RandomStringUtils;
import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.BahmniDomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class BahmniAttributeTypesProxyServiceTest extends BahmniDomainBaseModuleContextSensitiveTest {
	
	private final static String PROGRAM_ATT_TYPE_UUID = "b1d98f27-c058-46f2-9c12-87dd7c92f7e3";
	
	@Autowired
	public AttributeTypesProxyService service;
	
	@Autowired
	BahmniProgramWorkflowService bahmniProgramWorkflowService;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/bahmni-test-metadata.xml");
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetAttributeType() {
		// Replay
		BaseAttributeType<?> attType = service.getAttributeTypeByUuid(PROGRAM_ATT_TYPE_UUID, PROGRAM);
		
		// Verif
		assertNotNull(attType);
		assertThat(attType.getName(), is("Program Efficiency Score"));
		assertTrue(attType instanceof ProgramAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveAttributeType() {
		// Setup
		BaseAttributeType<?> attType = bahmniProgramWorkflowService.getProgramAttributeType(1089);
		
		// Replay (name edition)
		String newName = RandomStringUtils.random(25);
		attType.setName(newName);
		attType = service.saveAttributeType(attType);
		
		// Verif
		attType = service.getAttributeTypeByName(newName, PROGRAM);
		assertNotNull(attType);
		assertThat(attType.getId(), is(1089));
	}
}

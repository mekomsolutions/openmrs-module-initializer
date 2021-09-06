package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.openmrs.module.initializer.api.AttributeTypesLoaderTest.assertCustomDatatype;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class BahmniAttributeTypesLoaderTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AttributeTypesLoader loader;
	
	@Autowired
	BahmniProgramWorkflowService bahmniProgramWorkflowService;
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/bahmni-test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Verify setup
		{
			ProgramAttributeType attType = bahmniProgramWorkflowService.getProgramAttributeType(1089);
			assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			assertEquals("Program Efficiency Score", attType.getName());
			assertEquals("Metric of the program efficiency", attType.getDescription());
			assertThat(attType.getMinOccurs(), is(0));
			assertNull(attType.getMaxOccurs());
			
		}
		
		// Replay
		loader.load();
		
		// Verify creations
		{
			ProgramAttributeType attType = bahmniProgramWorkflowService
			        .getProgramAttributeTypeByUuid("3884c889-35f5-47b4-a6b7-5b1165cee218");
			assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			assertEquals("Program Assessment", attType.getName());
			assertEquals("Program Assessment's description", attType.getDescription());
			assertThat(attType.getMinOccurs(), is(1));
			assertNull(attType.getMaxOccurs());
			
			ProgramAttributeType attType2 = bahmniProgramWorkflowService
			        .getProgramAttributeTypeByUuid("9398c839-4f39-428c-9022-e457980ccfa8");
			assertNotNull(attType2);
			assertEquals("org.bahmni.module.bahmnicore.customdatatype.datatype.CodedConceptDatatype",
			    attType2.getDatatypeClassname());
			assertEquals("CodedConcept attribute type", attType2.getName());
			assertEquals("This is a Program's CodedConcept attribute type", attType2.getDescription());
			assertThat(attType2.getMinOccurs(), is(0));
			assertThat(attType2.getMaxOccurs(), is(1));
		}
		
		// Verify editions
		{
			ProgramAttributeType attType = bahmniProgramWorkflowService
			        .getProgramAttributeTypeByUuid("b1d98f27-c058-46f2-9c12-87dd7c92f7e3");
			assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			assertEquals("Program Efficiency Indicator", attType.getName());
			assertEquals("Metric of the program efficiency", attType.getDescription());
			assertThat(attType.getMinOccurs(), is(0));
			assertThat(attType.getMaxOccurs(), is(1));
		}
	}
}

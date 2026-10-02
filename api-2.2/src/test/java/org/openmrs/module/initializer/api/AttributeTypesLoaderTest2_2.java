package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.initializer.api.AttributeTypesLoaderTest.assertCustomDatatype;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.ConceptAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AttributeTypesLoaderTest2_2 extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AttributeTypesLoader loader;
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService pws;
	
	@BeforeEach
	public void setup() {
		executeDataSet("testdata/test-metadata-2.2.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Verify setup
		{
			ProgramAttributeType attType = pws.getProgramAttributeType(1089);
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Program Efficiency Score", attType.getName());
			Assertions.assertEquals("Metric of the program efficiency", attType.getDescription());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(0));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(1));
		}
		
		// Replay
		loader.load();
		
		// Verify creations
		{
			ConceptAttributeType attType = cs.getConceptAttributeTypeByUuid("7d002484-0fcd-4759-a67a-04dbf8fdaab1");
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.LocationDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Concept Location", attType.getName());
			assertThat(attType.getDescription(), isEmptyOrNullString());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(1));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(1));
		}
		{
			ProgramAttributeType attType = pws.getProgramAttributeTypeByUuid("3884c889-35f5-47b4-a6b7-5b1165cee218");
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Program Assessment", attType.getName());
			Assertions.assertEquals("Program Assessment's description", attType.getDescription());
			Assertions.assertNull(attType.getMinOccurs());
			Assertions.assertNull(attType.getMaxOccurs());
		}
		
		// Verify editions
		{
			ProgramAttributeType attType = pws.getProgramAttributeTypeByUuid("b1d98f27-c058-46f2-9c12-87dd7c92f7e3");
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Program Efficiency Indicator", attType.getName());
			Assertions.assertEquals("Metric of the program efficiency", attType.getDescription());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(0));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(1));
		}
		
		// Verify retirement using name as the pivot
		{
			ConceptAttributeType attType = cs.getConceptAttributeTypeByName("Concept Family");
			Assertions.assertNotNull(attType);
			Assertions.assertTrue(attType.getRetired());
		}
	}
}

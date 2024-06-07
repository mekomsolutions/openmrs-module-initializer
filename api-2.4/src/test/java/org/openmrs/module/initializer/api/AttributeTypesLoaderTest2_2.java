package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.initializer.api.AttributeTypesLoaderTest.assertCustomDatatype;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
	
	@Before
	public void setup() {
		executeDataSet("testdata/test-metadata-2.2.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Verify setup
		{
			ProgramAttributeType attType = pws.getProgramAttributeType(1089);
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Program Efficiency Score", attType.getName());
			Assert.assertEquals("Metric of the program efficiency", attType.getDescription());
			Assert.assertThat(attType.getMinOccurs(), is(0));
			Assert.assertThat(attType.getMaxOccurs(), is(1));
		}
		
		// Replay
		loader.load();
		
		// Verify creations
		{
			ConceptAttributeType attType = cs.getConceptAttributeTypeByUuid("7d002484-0fcd-4759-a67a-04dbf8fdaab1");
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.LocationDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Concept Location", attType.getName());
			assertThat(attType.getDescription(), isEmptyOrNullString());
			Assert.assertThat(attType.getMinOccurs(), is(1));
			Assert.assertThat(attType.getMaxOccurs(), is(1));
		}
		{
			ProgramAttributeType attType = pws.getProgramAttributeTypeByUuid("3884c889-35f5-47b4-a6b7-5b1165cee218");
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Program Assessment", attType.getName());
			Assert.assertEquals("Program Assessment's description", attType.getDescription());
			Assert.assertNull(attType.getMinOccurs());
			Assert.assertNull(attType.getMaxOccurs());
		}
		
		// Verify editions
		{
			ProgramAttributeType attType = pws.getProgramAttributeTypeByUuid("b1d98f27-c058-46f2-9c12-87dd7c92f7e3");
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Program Efficiency Indicator", attType.getName());
			Assert.assertEquals("Metric of the program efficiency", attType.getDescription());
			Assert.assertThat(attType.getMinOccurs(), is(0));
			Assert.assertThat(attType.getMaxOccurs(), is(1));
		}
		
		// Verify retirement using name as the pivot
		{
			ConceptAttributeType attType = cs.getConceptAttributeTypeByName("Concept Family");
			Assert.assertNotNull(attType);
			Assert.assertTrue(attType.getRetired());
		}
	}
}

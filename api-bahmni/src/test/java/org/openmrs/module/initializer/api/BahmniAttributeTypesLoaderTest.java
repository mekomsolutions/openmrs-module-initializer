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
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.BahmniDomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BahmniAttributeTypesLoaderTest extends BahmniDomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AttributeTypesLoader loader;
	
	@Autowired
	@Qualifier("locationService")
	private LocationService ls;
	
	@Autowired
	@Qualifier("visitService")
	private VisitService vs;
	
	@Autowired
	@Qualifier("providerService")
	private ProviderService ps;
	
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

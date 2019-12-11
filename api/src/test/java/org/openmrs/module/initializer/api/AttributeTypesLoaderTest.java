package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AttributeTypesLoaderTest extends DomainBaseModuleContextSensitiveTest {
	
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
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	public static void assertCustomDatatype(String className) {
		Assert.assertThat(className, startsWith("org.openmrs.customdatatype.datatype"));
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			Assert.fail(className + " is not a valid OpenMRS custom data type class name.");
		}
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Pre-load verif
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			Assert.assertEquals("Location Code", attType.getName());
			assertThat(attType.getDescription(), isEmptyOrNullString());
			Assert.assertThat(attType.getMinOccurs(), is(0));
			Assert.assertNull(attType.getMaxOccurs());
		}
		{
			ProviderAttributeType attType = ps.getProviderAttributeType(2089);
			Assert.assertEquals("Provider Speciality", attType.getName());
			Assert.assertEquals("Clinical speciality for this provider.", attType.getDescription());
			Assert.assertThat(attType.getMinOccurs(), is(0));
			Assert.assertThat(attType.getMaxOccurs(), is(5));
		}
		
		// Replay
		loader.load();
		
		// Verify creations
		{
			VisitAttributeType attType = vs.getVisitAttributeTypeByUuid("0bc29982-3193-11e3-93ae-92367f222671");
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Visit Color", attType.getName());
			Assert.assertEquals("Visit Color's description", attType.getDescription());
			Assert.assertThat(attType.getMinOccurs(), is(1));
			Assert.assertThat(attType.getMaxOccurs(), is(1));
		}
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("0bb29984-3193-11e7-93ae-92367f002671");
			Assert.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assert.assertEquals("Location Height", attType.getName());
		}
		
		// Verify editions
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			Assert.assertEquals("Location ISO Code", attType.getName());
			Assert.assertEquals("Location ISO Code's description", attType.getDescription());
			assertCustomDatatype(attType.getDatatypeClassname());
			Assert.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			Assert.assertThat(attType.getMinOccurs(), is(1));
			Assert.assertThat(attType.getMaxOccurs(), is(10));
		}
	}
}

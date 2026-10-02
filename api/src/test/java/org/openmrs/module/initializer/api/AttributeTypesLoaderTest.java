package org.openmrs.module.initializer.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
	
	public static void assertCustomDatatype(String className) {
		MatcherAssert.assertThat(className, startsWith("org.openmrs.customdatatype.datatype"));
		try {
			Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			Assertions.fail(className + " is not a valid OpenMRS custom data type class name.");
		}
	}
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Pre-load verif
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			Assertions.assertEquals("Location Code", attType.getName());
			MatcherAssert.assertThat(attType.getDescription(), isEmptyOrNullString());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(0));
			Assertions.assertNull(attType.getMaxOccurs());
		}
		{
			ProviderAttributeType attType = ps.getProviderAttributeType(1090);
			Assertions.assertEquals("Provider Speciality", attType.getName());
			Assertions.assertEquals("Clinical speciality for this provider", attType.getDescription());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(0));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(5));
		}
		{
			ProviderAttributeType attType = ps.getProviderAttributeType(1091);
			Assertions.assertEquals("Provider Rating", attType.getName());
			MatcherAssert.assertThat(attType.getDescription(), isEmptyOrNullString());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(1));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(1));
		}
		
		// Replay
		loader.load();
		
		// Verify creations
		{
			VisitAttributeType attType = vs.getVisitAttributeTypeByUuid("0bc29982-3193-11e3-93ae-92367f222671");
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Visit Color", attType.getName());
			Assertions.assertEquals("Visit Color's description", attType.getDescription());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(1));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(1));
		}
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("0bb29984-3193-11e7-93ae-92367f002671");
			Assertions.assertNotNull(attType);
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FloatDatatype", attType.getDatatypeClassname());
			Assertions.assertEquals("Location Height", attType.getName());
		}
		
		// Verify edition using UUID as pivot
		{
			LocationAttributeType attType = ls.getLocationAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			Assertions.assertNotNull(attType);
			Assertions.assertEquals("Location ISO Code", attType.getName());
			Assertions.assertEquals("Location ISO Code's description", attType.getDescription());
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(1));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(10));
		}
		// Verify edition using name as pivot
		{
			ProviderAttributeType attType = ps.getProviderAttributeType(1090);
			Assertions.assertNotNull(attType);
			Assertions.assertEquals("Provider Speciality", attType.getName());
			Assertions.assertEquals("Clinical speciality for this provider", attType.getDescription());
			assertCustomDatatype(attType.getDatatypeClassname());
			Assertions.assertEquals("org.openmrs.customdatatype.datatype.FreeTextDatatype", attType.getDatatypeClassname());
			MatcherAssert.assertThat(attType.getMinOccurs(), is(0));
			MatcherAssert.assertThat(attType.getMaxOccurs(), is(7));
		}
		// Verify retirement
		{
			ProviderAttributeType attType = ps.getProviderAttributeType(1091);
			Assertions.assertNotNull(attType);
			Assertions.assertTrue(attType.getRetired());
		}
	}
}

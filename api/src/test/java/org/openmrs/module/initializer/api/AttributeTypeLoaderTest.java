package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class AttributeTypeLoaderTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AttributeTypeLoader loader;
	
	private LocationService ls;
	
	private VisitService vs;
	
	@Before
	public void setup() {
		ls = Context.getLocationService();
		vs = Context.getVisitService();
		
		// A location attr. type to be edited via CSV
		LocationAttributeType lat = new LocationAttributeType();
		lat.setUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
		lat.setName("Old Name");
		lat.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		ls.saveLocationAttributeType(lat);
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		// Replay
		loader.load();
		LocationAttributeType locAttributeType = ls.getLocationAttributeTypeByUuid("0bb29984-3193-11e7-93ae-92367f002671");
		VisitAttributeType visitAttributeType = vs.getVisitAttributeTypeByUuid("0bc29982-3193-11e3-93ae-92367f222671");
		LocationAttributeType editedLocAttType = ls.getLocationAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
		
		// Verif
		Assert.assertNotNull(visitAttributeType);
		Assert.assertEquals("Test VisitAttribute Type", visitAttributeType.getName());
		
		Assert.assertNotNull(locAttributeType);
		Assert.assertEquals("Test LocationAttribute Type", locAttributeType.getName());
		
		// Verify editions
		Assert.assertEquals("New name", editedLocAttType.getName());
		Assert.assertEquals("Some description", editedLocAttType.getDescription());
		Assert.assertEquals(10, editedLocAttType.getMaxOccurs().intValue());
		Assert.assertEquals(1, editedLocAttType.getMinOccurs().intValue());
	}
}

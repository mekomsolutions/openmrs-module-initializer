package org.openmrs.module.initializer.api.loc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.LocationTagListParser;

public class LocationAttributeLineProcessorTest {
	
	private LocationService ls;
	
	private LocationAttributeLineProcessor processor;
	
	private static String PHONE_ATT_TYPE_UUID = "fb803f59-a1a8-4da9-969a-4a18df3241fe";
	
	private static String EMAIL_ATT_TYPE_NAME = "Facility Email";
	
	private static String HEADER_PREFIX = "attribute|";
	
	@Before
	public void setup() {
		ls = mock(LocationService.class);
		processor = new LocationAttributeLineProcessor(ls);
		
		LocationAttributeType phoneAttrType = new LocationAttributeType();
		phoneAttrType.setName("Facility Phone");
		phoneAttrType.setMinOccurs(0);
		phoneAttrType.setMaxOccurs(1);
		phoneAttrType.setUuid(PHONE_ATT_TYPE_UUID);
		phoneAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		
		LocationAttributeType emailAttrType = new LocationAttributeType();
		emailAttrType.setName("EMAIL_ATT_TYPE_NAME");
		emailAttrType.setMinOccurs(0);
		emailAttrType.setMaxOccurs(1);
		emailAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		
		when(ls.getLocationAttributeTypeByUuid(PHONE_ATT_TYPE_UUID)).thenReturn(phoneAttrType);
		when(ls.getLocationAttributeTypeByUuid(EMAIL_ATT_TYPE_NAME)).thenReturn(null);
		when(ls.getLocationAttributeTypeByName(EMAIL_ATT_TYPE_NAME)).thenReturn(emailAttrType);
	}
	
	@Test
	public void fill_shouldParseLocationAttributes() {
		// Setup
		String[] headerLine = { HEADER_PREFIX + PHONE_ATT_TYPE_UUID, HEADER_PREFIX + EMAIL_ATT_TYPE_NAME };
		String[] line = { "+254 703203342", "admin@facility.com" };
		
		// Replay
		Location loc = processor.fill(new Location(), new CsvLine(headerLine, line));
		
		// Verify
		Collection<LocationAttribute> attributes = loc.getActiveAttributes();
		Assert.assertEquals(2, attributes.size());
		Object[] attributesArray = attributes.toArray();
		Assert.assertThat(((LocationAttribute) attributesArray[0]).getValue(), is("+254 703203342"));
		Assert.assertThat(((LocationAttribute) attributesArray[1]).getValue(), is("admin@facility.com"));
	}
	
	@Test(expected = APIException.class)
	public void fill_shouldFailIfAttributeTypeDoesNotExistYetAttributeValueIsNotBlank() {
		// Setup
		String[] headerLine = { HEADER_PREFIX + PHONE_ATT_TYPE_UUID, HEADER_PREFIX + EMAIL_ATT_TYPE_NAME };
		String[] line = { "+254 703203342", "admin@facility.com" };
		when(ls.getLocationAttributeTypeByName(EMAIL_ATT_TYPE_NAME)).thenReturn(null);
		
		// Replay
		processor.fill(new Location(), new CsvLine(headerLine, line));
	}
	
}

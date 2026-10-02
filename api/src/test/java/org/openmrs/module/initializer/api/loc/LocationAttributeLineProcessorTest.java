package org.openmrs.module.initializer.api.loc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmrs.module.initializer.api.BaseAttributeLineProcessor.HEADER_ATTRIBUTE_PREFIX;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.initializer.api.CsvLine;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class LocationAttributeLineProcessorTest {
	
	private MockedStatic<Context> contextMock;
	
	@AfterEach
	public void closeStaticMocks() {
		contextMock.close();
	}
	
	private LocationService ls;
	
	private LocationAttributeLineProcessor processor;
	
	private static String PHONE_ATT_TYPE_UUID = "fb803f59-a1a8-4da9-969a-4a18df3241fe";
	
	private static String EMAIL_ATT_TYPE_NAME = "Facility Email";
	
	@BeforeEach
	public void setup() {
		contextMock = Mockito.mockStatic(Context.class);
		DatatypeService datatypeService = mock(DatatypeService.class);
		when(Context.getDatatypeService()).thenReturn(datatypeService);
		
		when(datatypeService.getDatatype(any(), any())).thenReturn(new FreeTextDatatype());
		
		when(Context.getRuntimeProperties()).thenReturn(new Properties());
		
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
		String[] headerLine = { HEADER_ATTRIBUTE_PREFIX + PHONE_ATT_TYPE_UUID,
		        HEADER_ATTRIBUTE_PREFIX + EMAIL_ATT_TYPE_NAME };
		String[] line = { "+1 206 555 0100", "jdoe@example.com" };
		
		// Replay
		Location loc = processor.fill(new Location(), new CsvLine(headerLine, line));
		
		// Verify
		Collection<LocationAttribute> attributes = loc.getActiveAttributes();
		Assertions.assertEquals(2, attributes.size());
		Assertions.assertTrue(attributes.removeIf(a -> a.getValue().equals("+1 206 555 0100")),
		    "Must have attribute +1 206 555 0100");
		Assertions.assertTrue(attributes.removeIf(a -> a.getValue().equals("jdoe@example.com")),
		    "Must have attribute jdoe@example.com");
	}
	
	@Test
	public void fill_shouldLeaveUnspecifiedAttributesIntact() {
		// Setup
		String[] headerLine = { HEADER_ATTRIBUTE_PREFIX + PHONE_ATT_TYPE_UUID };
		String[] line = { "+1 206 555 0100" };
		Location loc = new Location();
		LocationAttribute la = new LocationAttribute();
		la.setAttributeType(ls.getLocationAttributeTypeByName(EMAIL_ATT_TYPE_NAME));
		la.setValue("janedoe@example.com");
		loc.addAttribute(la);
		
		// Replay
		loc = processor.fill(loc, new CsvLine(headerLine, line));
		
		// Verify
		Collection<LocationAttribute> attributes = loc.getActiveAttributes();
		Assertions.assertEquals(2, attributes.size());
		Assertions.assertTrue(attributes.removeIf(a -> a.getValue().equals("+1 206 555 0100")),
		    "Must have attribute +1 206 555 0100");
		Assertions.assertTrue(attributes.removeIf(a -> a.getValue().equals("janedoe@example.com")),
		    "Must have attribute janedoe@example.com");
	}
	
	@Test
	public void fill_shouldFailIfAttributeTypeDoesNotExistAndAttributeValueIsNotBlank() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Setup
			String[] headerLine = { HEADER_ATTRIBUTE_PREFIX + PHONE_ATT_TYPE_UUID,
			        HEADER_ATTRIBUTE_PREFIX + EMAIL_ATT_TYPE_NAME };
			String[] line = { "+1 206 555 0100", "jdoe@example.com" };
			when(ls.getLocationAttributeTypeByName(EMAIL_ATT_TYPE_NAME)).thenReturn(null);
			
			// Replay
			processor.fill(new Location(), new CsvLine(headerLine, line));
			
		});
	}
}

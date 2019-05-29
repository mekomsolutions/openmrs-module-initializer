package org.openmrs.module.initializer.api.attributes.types;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AttributeTypeServiceHandler1_11_9Test extends BaseModuleContextSensitiveTest {
	
	private final static String LOCATION_ATT_TYPE_UUID = "0bb29984-3193-11e7-93ae-92367f002671";
	
	private final static String VISIT_ATT_TYPE_UUID = "0bc29982-3193-11e3-93ae-92367f222671";
	
	private final static String PROVIDER_ATT_TYPE_UUID = "75caHf4e-709f-4bb8-8289-2f9bUDJ3803M";
	
	@Autowired
	public AttributeTypeServiceCompatibility service;
	
	@Test
	public void saveAttributeType_shouldSaveLocationAttributeType() {
		// Setup
		service.saveAttributeType(createLocationAttributeType());
		BaseAttributeType savedAttributeType = Context.getLocationService()
		        .getLocationAttributeTypeByUuid(LOCATION_ATT_TYPE_UUID);
		
		// Verif
		Assert.assertNotNull(savedAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveVisitAttributeType() {
		// Setup
		service.saveAttributeType(createVisitAttributeType());
		BaseAttributeType savedAttributeType = Context.getVisitService().getVisitAttributeTypeByUuid(VISIT_ATT_TYPE_UUID);
		
		// Verif
		Assert.assertNotNull(savedAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveProviderAttributeType() {
		
		service.saveAttributeType(createProviderAttributeType());
		BaseAttributeType savedAttributeType = Context.getProviderService()
		        .getProviderAttributeTypeByUuid(PROVIDER_ATT_TYPE_UUID);
		
		// Verif
		Assert.assertNotNull(savedAttributeType);
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetLocationAttributeType() {
		// Setup
		service.saveAttributeType(createLocationAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByUuid(LOCATION_ATT_TYPE_UUID,
		    AttributeType.LOCATION);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Facility Phone");
		Assert.assertTrue(attributeTypeFromDB instanceof LocationAttributeType);
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetVisitAttributeType() {
		// Setup
		service.saveAttributeType(createVisitAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByUuid(VISIT_ATT_TYPE_UUID, AttributeType.VISIT);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Another one");
		Assert.assertTrue(attributeTypeFromDB instanceof VisitAttributeType);
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetProviderAttributeType() {
		// Setup
		service.saveAttributeType(createProviderAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByUuid(PROVIDER_ATT_TYPE_UUID,
		    AttributeType.PROVIDER);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test ProviderAttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ProviderAttributeType);
	}
	
	@Test
	public void getAttributeTypeByName_shouldReturnValueForSupportedTypes() {
		// Setup
		service.saveAttributeType(createVisitAttributeType());
		service.saveAttributeType(createLocationAttributeType());
		service.saveAttributeType(createProviderAttributeType());
		
		BaseAttributeType vat = service.getAttributeTypeByName("Another one", AttributeType.VISIT);
		BaseAttributeType lat = service.getAttributeTypeByName("Facility Phone", AttributeType.LOCATION);
		BaseAttributeType pat = service.getAttributeTypeByName("Test ProviderAttributeType", AttributeType.PROVIDER);
		
		// Verif
		Assert.assertNotNull(vat);
		Assert.assertEquals(VISIT_ATT_TYPE_UUID, vat.getUuid());
		Assert.assertNotNull(lat);
		Assert.assertEquals(LOCATION_ATT_TYPE_UUID, lat.getUuid());
		Assert.assertNotNull(pat);
		Assert.assertEquals(PROVIDER_ATT_TYPE_UUID, pat.getUuid());
	}
	
	private VisitAttributeType createVisitAttributeType() {
		VisitAttributeType visitAttributeType = new VisitAttributeType();
		visitAttributeType.setName("Another one");
		visitAttributeType.setUuid(VISIT_ATT_TYPE_UUID);
		visitAttributeType.setDatatypeClassname(FreeTextDatatype.class.getName());
		return visitAttributeType;
		
	}
	
	private LocationAttributeType createLocationAttributeType() {
		LocationAttributeType lat = new LocationAttributeType();
		lat.setName("Facility Phone");
		lat.setMinOccurs(0);
		lat.setMaxOccurs(1);
		lat.setDatatypeClassname(FreeTextDatatype.class.getName());
		lat.setUuid(LOCATION_ATT_TYPE_UUID);
		return lat;
	}
	
	private ProviderAttributeType createProviderAttributeType() {
		ProviderAttributeType pat = new ProviderAttributeType();
		pat.setName("Test ProviderAttributeType");
		pat.setDatatypeClassname(FreeTextDatatype.class.getName());
		pat.setUuid(PROVIDER_ATT_TYPE_UUID);
		return pat;
	}
}

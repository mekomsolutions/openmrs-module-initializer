package org.openmrs.module.initializer.attributes.types;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.initializer.api.attributes.types.AttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeServiceHandler1_11_9Test;

public class AttributeTypeServiceHandler2_2Test extends AttributeTypeServiceHandler1_11_9Test {
	
	private final static String CONCEPT_ATT_TYPE_UUID = "9eca4f4e-707f-4bb8-8289-2f9b6e93803c";
	
	private final static String PROGRAM_ATT_TYPE_UUID = "78caHf4e-707f-4bb8-8289-2f9bBKJ3803T";
	
	@Test
	public void getAttributeTypeByUuid_shouldGetConceptAttributeType() {
		// Setup
		Context.getConceptService().saveConceptAttributeType(createConceptAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByUuid(CONCEPT_ATT_TYPE_UUID, AttributeType.CONCEPT);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Concept AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ConceptAttributeType);
	}
	
	@Test
	public void getAttributeTypeByUuid_shouldGetProgramAttributeType() {
		// Setup
		Context.getProgramWorkflowService().saveProgramAttributeType(createProgramAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByUuid(PROGRAM_ATT_TYPE_UUID, AttributeType.PROGRAM);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Program AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ProgramAttributeType);
	}
	
	@Test
	public void getAttributeTypeByName_shouldGetConceptAttributeType() {
		// Setup
		Context.getConceptService().saveConceptAttributeType(createConceptAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByName("Test Concept AttributeType",
		    AttributeType.CONCEPT);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Concept AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ConceptAttributeType);
	}
	
	@Test
	public void getAttributeTypeByName_shouldGetProgramAttributeType() {
		// Setup
		Context.getProgramWorkflowService().saveProgramAttributeType(createProgramAttributeType());
		BaseAttributeType attributeTypeFromDB = service.getAttributeTypeByName("Test Program AttributeType",
		    AttributeType.PROGRAM);
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Program AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ProgramAttributeType);
	}
	
	@Test
	public void saveAttributeType_shouldSaveConceptAttributeType() {
		// Setup
		BaseAttributeType attributeTypeFromDB = service.saveAttributeType(createConceptAttributeType());
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Concept AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ConceptAttributeType);
		
	}
	
	@Test
	public void saveAttributeType_shouldSaveProgramAttributeType() {
		// Setup
		BaseAttributeType attributeTypeFromDB = service.saveAttributeType(createProgramAttributeType());
		
		// Verif
		Assert.assertNotNull(attributeTypeFromDB);
		Assert.assertEquals(attributeTypeFromDB.getName(), "Test Program AttributeType");
		Assert.assertTrue(attributeTypeFromDB instanceof ProgramAttributeType);
		
	}
	
	private ProgramAttributeType createProgramAttributeType() {
		ProgramAttributeType pat = new ProgramAttributeType();
		pat.setUuid(PROGRAM_ATT_TYPE_UUID);
		pat.setName("Test Program AttributeType");
		pat.setDatatypeClassname(FreeTextDatatype.class.getName());
		return pat;
	}
	
	private ConceptAttributeType createConceptAttributeType() {
		ConceptAttributeType cat = new ConceptAttributeType();
		cat.setUuid(CONCEPT_ATT_TYPE_UUID);
		cat.setName("Test Concept AttributeType");
		cat.setDatatypeClassname(FreeTextDatatype.class.getName());
		return cat;
	}
	
}

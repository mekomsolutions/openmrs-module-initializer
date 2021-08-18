package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.relationship.types.RelationshipTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RelationshipTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("personService")
	private PersonService ps;
	
	@Autowired
	private RelationshipTypesLoader loader;
	
	private static final String RELATIONSHIP_TYPE_TO_EDIT_UUID = "53d8a8f3-0084-4a52-8666-c655f5bd2689";
	
	private static final String RELATIONSHIP_TYPE_TO_RETIRE_UUID = "3982f469-cedc-4b2d-91ea-fe38f881e1a0";
	
	@Before
	public void setup() {
		// a relationship type for retirement
		{
			RelationshipType rt = new RelationshipType();
			rt.setName("Aunt/Niece");
			rt.setDescription("A relationship of an aunt and her niece");
			rt.setUuid(RELATIONSHIP_TYPE_TO_RETIRE_UUID);
			rt.setaIsToB("Aunt");
			rt.setbIsToA("Niece");
			ps.saveRelationshipType(rt);
		}
		// a relationship type for edit
		{
			RelationshipType rt = new RelationshipType();
			rt.setName("Supervisor/Supervisee");
			rt.setDescription("An old description for supervisor to supervisee relationship");
			rt.setUuid(RELATIONSHIP_TYPE_TO_EDIT_UUID);
			rt.setaIsToB("Supervisor");
			rt.setbIsToA("Supervisee");
			ps.saveRelationshipType(rt);
		}
	}
	
	@Test
	public void load_shouldLoadRelationshipTypesAccordingToCsvFiles() {
		
		// replay
		loader.load();
		
		// verify created 
		{
			RelationshipType rt = ps.getRelationshipTypeByUuid("c86d9979-b8ac-4d8c-85cf-cc04e7f16315");
			Assert.assertNotNull(rt);
			Assert.assertEquals("Uncle/Nephew", rt.getName());
			Assert.assertEquals("A relationship of an uncle and his nephew", rt.getDescription());
			Assert.assertEquals("Uncle", rt.getaIsToB());
			Assert.assertEquals("Nephew", rt.getbIsToA());
			Assert.assertEquals(true, rt.getPreferred());
			Assert.assertEquals(1, rt.getWeight().intValue());
		}
		
		// verify edited
		{
			RelationshipType rt = ps.getRelationshipTypeByUuid(RELATIONSHIP_TYPE_TO_EDIT_UUID);
			Assert.assertNotNull(rt);
			Assert.assertEquals("Supervisor/Supervisee", rt.getName());
			Assert.assertEquals("A new description for supervisor to supervisee relationship", rt.getDescription());
		}
		
		// verify retired
		{
			RelationshipType rt = ps.getRelationshipTypeByUuid(RELATIONSHIP_TYPE_TO_RETIRE_UUID);
			Assert.assertNotNull(rt);
			Assert.assertTrue(rt.getRetired());
		}
	}
	
}

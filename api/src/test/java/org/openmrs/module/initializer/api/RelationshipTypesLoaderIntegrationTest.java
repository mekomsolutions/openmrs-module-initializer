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
			RelationshipType rt = ps.getRelationshipTypeByUuid("53d8a8f3-0084-4a52-8666-c655f5bd2689");
			Assert.assertNotNull(rt);
			Assert.assertEquals("Supervisor/Supervisee", rt.getName());
			Assert.assertEquals("A new description for supervisor to supervisee relationship", rt.getDescription());
		}
		
		// verify retired
		{
			RelationshipType rt = ps.getRelationshipTypeByUuid("3982f469-cedc-4b2d-91ea-fe38f881e1a0");
			Assert.assertNotNull(rt);
			Assert.assertTrue(rt.getRetired());
		}
	}
	
}

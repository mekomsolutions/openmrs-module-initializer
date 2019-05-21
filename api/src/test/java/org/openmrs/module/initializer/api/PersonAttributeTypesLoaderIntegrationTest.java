/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer.api;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.PersonAttributeType;
import org.openmrs.Privilege;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PersonAttributeTypesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("personService")
	private PersonService ps;
	
	private int conceptForeignKey = 0;
	
	@Autowired
	private PersonAttributeTypesLoader loader;
	
	@Before
	public void setup() {
		
		ConceptService cs = Context.getConceptService();
		
		// Creating a privilege
		{
			Context.getUserService().savePrivilege(new Privilege("Edit:PAT", ""));
		}
		// A concept coded to be used as 'format'
		{
			Concept c = new Concept();
			c.setUuid("5825d19a-bafe-4348-b1ce-2ea03bfc1a10");
			c.setFullySpecifiedName(new ConceptName("FOREIGN_CONCEPT_1_FOR_PAT", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Question"));
			c.setDatatype(cs.getConceptDatatypeByName("Coded"));
			c = cs.saveConcept(c);
			conceptForeignKey = c.getId();
		}
		// A person attr. type to be renamed
		{
			PersonAttributeType pat = new PersonAttributeType();
			pat.setUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			pat.setName("PAT_RENAME_OLD_NAME");
			pat.setFormat("java.lang.String");
			ps.savePersonAttributeType(pat);
		}
		// A person attr. with a Concept foreign key to be changed via CSV
		{
			Concept c = new Concept();
			c.setUuid("3cbaf39d-3c12-439e-a846-7752d5e26fb0");
			c.setFullySpecifiedName(new ConceptName("FOREIGN_CONCEPT_2_FOR_PAT", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Question"));
			c.setDatatype(cs.getConceptDatatypeByName("Coded"));
			c = cs.saveConcept(c);
			
			PersonAttributeType pat = new PersonAttributeType();
			pat.setUuid("bd1adb89-ba65-40ad-8eb9-21751281432f");
			pat.setName("PAT_CHANGE_FOREIGNKEY");
			pat.setFormat("org.openmrs.Concept");
			pat.setForeignKey(c.getId());
			ps.savePersonAttributeType(pat);
		}
	}
	
	@Test
	public void load_shouldLoadAccordingToCsvFiles() {
		
		// Replay
		loader.load();
		
		// Verif foreign key
		{
			PersonAttributeType pat = ps.getPersonAttributeTypeByName("PAT_with_concept");
			Assert.assertEquals(conceptForeignKey, pat.getForeignKey().intValue());
			Assert.assertEquals("Edit:PAT", pat.getEditPrivilege().getName());
		}
		// Verif renaming
		{
			PersonAttributeType pat = ps.getPersonAttributeTypeByUuid("9eca4f4e-707f-4bb8-8289-2f9b6e93803c");
			Assert.assertEquals("PAT_RENAME_NEW_NAME", pat.getName());
		}
		// Verif foreign key changed
		{
			PersonAttributeType pat = ps.getPersonAttributeTypeByName("PAT_CHANGE_FOREIGNKEY");
			Assert.assertEquals(conceptForeignKey, pat.getForeignKey().intValue());
			Assert.assertTrue(pat.isSearchable());
		}
	}
}

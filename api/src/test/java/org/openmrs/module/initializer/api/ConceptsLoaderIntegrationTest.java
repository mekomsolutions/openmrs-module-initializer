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

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

public class ConceptsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private ConceptsLoader loader;
	
	@Autowired
	private DateDatatype dateDatatype;
	
	private Locale localeEn = Locale.ENGLISH;
	
	private Locale localeKm = new Locale("km", "KH");
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-concepts.xml");
		executeDataSet("testdata/test-concepts-numeric.xml");
	}
	
	@Test
	public void load_shouldLoadConceptsAccordingToCsvFiles() {
		
		// Verif setup
		Concept c = null;
		{
			c = cs.getConceptByUuid("d803e973-1010-4415-8659-c011dec707c0");
			Assert.assertEquals(2, c.getSetMembers().size());
			Assert.assertTrue(c.isSet());
			c = cs.getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			Assert.assertFalse(c.getRetired());
		}
		
		// Replay
		loader.load();
		
		// Verif 'base' CSV loading
		{
			// Verif by name
			Context.setLocale(localeEn);
			c = cs.getConceptByName("Cambodia_Nationality");
			Assert.assertNotNull(c);
			Assert.assertEquals("Nationality", c.getDescription().toString());
			Assert.assertEquals("Question", c.getConceptClass().getName());
			Assert.assertEquals("Coded", c.getDatatype().getName());
			
			// Verif by UUID
			Context.setLocale(localeEn);
			c = cs.getConceptByUuid("db2f4fc4-3171-11e7-93ae-92361f002671");
			Assert.assertNotNull(c);
			Assert.assertEquals("Phnong", c.getShortNameInLocale(localeEn).getName());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Misc", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
			
			// Verif in another locale
			Context.setLocale(localeKm);
			c = cs.getConceptByName("កម្ពុជា_ចាម");
			Assert.assertNotNull(c);
			Assert.assertEquals("ចាម", c.getDescription().toString());
			Assert.assertEquals("Misc", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
			
			// Verif in another locale
			Context.setLocale(localeKm);
			c = cs.getConceptByName("កម្ពុជា_កួយ");
			Assert.assertNotNull(c);
			Assert.assertEquals(1, c.getShortNames().size());
			Assert.assertEquals("កួយ", c.getShortNameInLocale(localeKm).getName());
			Assert.assertEquals("កួយ", c.getDescription().toString());
			Assert.assertEquals("Misc", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
			
			// Verif just one name is enough
			Context.setLocale(localeEn);
			c = cs.getConceptByName("Cambodia_Kavet");
			Assert.assertNotNull(c);
			Assert.assertEquals(1, c.getNames().size());
			Assert.assertEquals(0, c.getShortNames().size());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Misc", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
			
			// Failed ones
			Context.setLocale(localeEn);
			Assert.assertNull(cs.getConceptByUuid("db2f5104-3171-11e7-93ae-92361f002671"));
			Assert.assertNull(cs.getConceptByName("Cambodia_Krung"));
			Assert.assertNull(cs.getConceptByName("db2f5460-3171-11e7-93ae-92361f002671"));
			Assert.assertNull(cs.getConceptByName("Cambodia_Lao"));
			Assert.assertNull(cs.getConceptByUuid("00b29984-3183-11e7-93ae-92361f002671"));
			
			// Retired one
			c = cs.getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			Assert.assertTrue(c.isRetired());
			
			// Un-retire one
			Context.setLocale(localeEn);
			c = cs.getConceptByUuid("4c93c34e-37c2-11ea-bd28-d70ffe7aa802");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("MMR", c.getFullySpecifiedName(localeEn).getName());
			Assert.assertEquals("Misc", c.getConceptClass().getName());
			Assert.assertEquals("N/A", c.getDatatype().getName());
			
			// Edited one
			Context.setLocale(localeEn);
			c = cs.getConceptByUuid("276c5861-cd46-429f-9665-e067ddeca8e3");
			Assert.assertEquals("New short name", c.getShortNameInLocale(localeEn).getName());
			
			// Concept attributes
			c = cs.getConceptByUuid("4d3cfdcf-1f3f-4b41-9b31-02dfd951c582");
			Object[] attributes = c.getActiveAttributes().toArray();
			Assert.assertThat(attributes.length, is(2));
			Assert.assertEquals("jdoe@example.com", ((ConceptAttribute) attributes[0]).getValue());
			Assert.assertEquals("2020-04-06", dateDatatype.serialize((Date) ((ConceptAttribute) attributes[1]).getValue()));
			
		}
		
		Context.setLocale(localeEn);
		
		// Verif 'nested' CSV loading
		{
			Set<String> nestedUuids = new HashSet<String>(Arrays.asList(
			    new String[] { "8bc5043c-3221-11e7-93ae-92361f002671", "8bc506bc-3221-11e7-93ae-92361f002671" }));
			
			// Verif question
			c = cs.getConceptByUuid("8bc50946-3221-11e7-93ae-92361f002671");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.isSet());
			Assert.assertFalse(CollectionUtils.isEmpty(c.getAnswers()));
			Assert.assertEquals(2, c.getAnswers().size());
			for (ConceptAnswer nested : c.getAnswers()) {
				Assert.assertTrue(nestedUuids.contains(nested.getAnswerConcept().getUuid()));
			}
			
			// Verif set
			c = cs.getConceptByUuid("c84c3f88-3221-11e7-93ae-92361f002671");
			Assert.assertNotNull(c);
			Assert.assertTrue(c.isSet());
			Assert.assertFalse(CollectionUtils.isEmpty(c.getSetMembers()));
			Assert.assertEquals(2, c.getSetMembers().size());
			for (Concept nested : c.getSetMembers()) {
				Assert.assertTrue(nestedUuids.contains(nested.getUuid()));
			}
			
			// Verif not saved with missing answer(s) or member(s)
			Assert.assertNull(cs.getConceptByName("Unexisting concept answer"));
			Assert.assertNull(cs.getConceptByName("Unexisting set member"));
			
			// Verif modified
			c = cs.getConceptByUuid("d803e973-1010-4415-8659-c011dec707c0");
			Assert.assertTrue(CollectionUtils.isEmpty(c.getSetMembers()));
			Assert.assertFalse(c.isSet());
		}
		
		// Verif. 'mappings' CSV loading
		{
			// Verif mappings are added
			c = cs.getConceptByUuid("2c4da504-33d4-11e7-a919-92ebcb67fe33");
			Assert.assertNotNull(c);
			Assert.assertEquals(2, c.getConceptMappings().size());
			Set<String> names = new HashSet<String>();
			for (ConceptMap m : c.getConceptMappings()) {
				String source = m.getConceptReferenceTerm().getConceptSource().getName();
				String code = m.getConceptReferenceTerm().getCode();
				names.add(source + ":" + code);
			}
			Assert.assertTrue(names.contains("Cambodia:1234"));
			Assert.assertTrue(names.contains("CIEL:159392"));
			
			// Verif not saved with missing mapping(s)
			Assert.assertNull(cs.getConceptByName("Unexisting mapping"));
			
			// Verif the mapping used with a now retired concept is the mapping of a new
			// concept
			c = cs.getConceptByMapping("foo12bar", "Cambodia");
			Assert.assertNotNull(c);
			Assert.assertEquals("NEW_CONCEPT_REUSING_MAPPING", c.getFullySpecifiedName(localeEn).getName());
		}
		
		// Verif. 'numerics' CSV loading
		{
			ConceptNumeric cn = null;
			
			// A valid concept numeric
			c = cs.getConceptByName("CN_1");
			Assert.assertNotNull(c);
			cn = cs.getConceptNumeric(c.getId());
			Assert.assertNotNull(cn);
			Assert.assertEquals(0, cn.getLowAbsolute().compareTo(-100.5));
			Assert.assertEquals(0, cn.getLowCritical().compareTo(-85.7));
			Assert.assertEquals(0, cn.getLowNormal().compareTo(-50.3));
			Assert.assertEquals(0, cn.getHiNormal().compareTo(45.1));
			Assert.assertEquals(0, cn.getHiCritical().compareTo(78.0));
			Assert.assertEquals(0, cn.getHiAbsolute().compareTo(98.8));
			Assert.assertEquals("foo", cn.getUnits());
			Assert.assertTrue(cn.getAllowDecimal());
			Assert.assertEquals(1, cn.getDisplayPrecision().intValue());
			
			// This concept should have updated boundaries
			cn = cs.getConceptNumericByUuid("4280217a-eb93-4e2f-9684-28bed4690e7b");
			Assert.assertNotNull(cn);
			Assert.assertEquals(0, cn.getLowNormal().compareTo(45.7));
			Assert.assertEquals(0, cn.getHiNormal().compareTo(55.6));
			
			// Concept with misformatted boundaries should not have been created
			Assert.assertNull(cs.getConceptByName("CN_3_ERROR"));
		}
		
		// Verif. 'concepts complex' CSV loading
		{
			ConceptComplex cc = null;
			
			// A valid concept numeric
			c = cs.getConceptByName("CC_1");
			Assert.assertNotNull(c);
			cc = cs.getConceptComplex(c.getId());
			Assert.assertNotNull(cc);
			Assert.assertEquals("ImageHandler", cc.getHandler());
			
			// This concept should have updated boundaries
			c = cs.getConceptByUuid("b0b15817-79d6-4c33-b7e9-bfa079d46f5f");
			Assert.assertNotNull(c);
			Assert.assertNotNull(c.getConceptId());
			cc = cs.getConceptComplex(c.getId());
			Assert.assertNotNull(cc);
			Assert.assertEquals("BinaryDataHandler", cc.getHandler());
			
			// Concept with missing complex data handler should not have been created
			Assert.assertNull(cs.getConceptByName("CN_3_ERROR"));
		}
		
		// Verif. fetching and editing by FSN
		Context.setLocale(localeEn);
		{
			// Verif mappings are added
			c = cs.getConceptByName("CONCEPT_FETCH_BY_FSN");
			Assert.assertNotNull(c);
			Assert.assertEquals("New short name", c.getShortNameInLocale(localeEn).toString());
		}
		Context.setLocale(localeKm);
		{
			// Verif mappings are added
			c = cs.getConceptByName("គំនិត_ដោយ_FSN");
			Assert.assertNotNull(c);
			Assert.assertEquals("ឈ្មោះខ្លីថ្មី", c.getShortNameInLocale(localeKm).toString());
		}
	}
	
	@Test
	public void load_shouldLoadConceptNamesAccordingToCsvFiles() {
		
		ConceptNameType fsn = ConceptNameType.FULLY_SPECIFIED;
		ConceptNameType shortName = ConceptNameType.SHORT;
		Locale localeEs = new Locale("es");
		
		String yellowUuid = "c0c238b3-3061-11ec-8d2b-0242ac110002";
		String lemonUuid = "c4e56850-3061-11ec-8d2b-0242ac110002";
		
		// Initial state is that yellow and green are in the database, red and blue are not
		
		Concept yellow = cs.getConceptByUuid("4cfe07b0-3061-11ec-8d2b-0242ac110002");
		Assert.assertEquals(2, yellow.getNames(true).size());
		assertName(yellow, yellowUuid, "Yellow", localeEn, true, fsn);
		assertName(yellow, lemonUuid, "Lemon", localeEn, false, null);
		
		Concept green = cs.getConceptByUuid("61214827-303f-11ec-8d2b-0242ac110002");
		Assert.assertEquals(2, green.getNames(true).size());
		
		Assert.assertNull(cs.getConceptByUuid("58083852-303f-11ec-8d2b-0242ac110002")); // red
		Assert.assertNull(cs.getConceptByUuid("5dcaf167-303f-11ec-8d2b-0242ac110002")); // blue
		
		// Load once and test that all existing concept names are updated by uuid, and all new concept names are created
		loader.load();
		
		// These concepts are defined in concepts_names.csv and test-concepts.xml
		
		// Red and Blue are new Concepts and new Concept Names.
		// These tests confirm that new names are loaded correctly with a variety of null and not-null fields
		
		Concept red = cs.getConceptByUuid("58083852-303f-11ec-8d2b-0242ac110002");
		Assert.assertEquals(5, red.getNames(true).size());
		assertName(red, "e91ab3ad-303f-11ec-8d2b-0242ac110002", "Red", localeEn, true, fsn);
		assertName(red, "Rojo", localeEs, true, fsn);
		assertName(red, "R", localeEn, false, shortName);
		assertName(red, "R", localeEs, false, shortName);
		assertName(red, "Maroon", localeEn, false, null);
		
		Concept blue = cs.getConceptByUuid("5dcaf167-303f-11ec-8d2b-0242ac110002");
		Assert.assertEquals(7, blue.getNames(true).size());
		assertName(blue, "fe9c8c03-303f-11ec-8d2b-0242ac110002", "Blue", localeEn, false, fsn);
		assertName(blue, "Azul", localeEs, true, fsn);
		assertName(blue, "B", localeEn, false, shortName);
		assertName(blue, "A", localeEs, false, shortName);
		assertName(blue, "Navy", localeEn, true, null);
		assertName(blue, "Azulado", localeEs, false, null);
		assertName(blue, "Baby Blue", localeEn, false, null);
		
		// Green is an existing Concept
		// It has two existing names, and no uuids are specified in the CSV.
		// Since the name, type, and locale are the same in the CSV as in the DB, it should match, and not recreate.
		
		green = cs.getConceptByUuid("61214827-303f-11ec-8d2b-0242ac110002");
		Assert.assertEquals(2, green.getNames(true).size());
		assertName(green, "Green", localeEn, true, fsn);
		assertName(green, "Verde", localeEs, true, fsn);
		
		// Yellow is an existing Concept with 2 names, with Yellow as the FSN, and Lemon as a synonym
		// In the CSV:
		//   The "Yellow" FSN should be looked up by uuid, and changed to a Synonym
		//   The "Lemon" Synonym should be voided, it is not matched on uuid, and the name is changed
		//   The "Lemon Yellow" FSN should be created as a new name
		//   The "Y" short name should be created as a new name
		//   The resulting concept should have 3 non-voided, and 1 voided name
		
		yellow = cs.getConceptByUuid("4cfe07b0-3061-11ec-8d2b-0242ac110002");
		Assert.assertEquals(4, yellow.getNames(true).size());
		assertName(yellow, yellowUuid, "Yellow", localeEn, true, null);
		ConceptName lemon = assertName(yellow, lemonUuid, "Lemon", localeEn, false, null);
		Assert.assertTrue(lemon.getVoided());
		assertName(yellow, "Lemon Yellow", localeEn, false, fsn);
		assertName(yellow, "Y", localeEn, false, shortName);
	}
	
	protected ConceptName assertName(Concept c, String name, Locale locale, boolean preferred, ConceptNameType type) {
		ConceptName conceptName = null;
		for (ConceptName cn : c.getNames(true)) {
			if (cn.getName().equals(name) && cn.getConceptNameType() == type) {
				if (cn.getLocale().equals(locale) && cn.getLocalePreferred() == preferred) {
					conceptName = cn;
				}
			}
		}
		if (conceptName == null) {
			Assert.fail("No concept names found that match: " + name + "; " + locale + "; " + preferred + "; " + type);
		}
		return conceptName;
	}
	
	protected ConceptName assertName(Concept concept, String uuid, String name, Locale locale, boolean preferred,
	        ConceptNameType type) {
		ConceptName cn = assertName(concept, name, locale, preferred, type);
		Assert.assertEquals(uuid, cn.getUuid());
		return cn;
	}
}

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.c.ConceptsLoader;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConceptsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService cs;
	
	@Autowired
	private ConceptsLoader loader;
	
	private Locale localeEn = Locale.ENGLISH;
	
	private Locale localeKm = new Locale("km", "KH");
	
	@Before
	public void setup() {
		
		ConceptSource source = null;
		source = new ConceptSource();
		source.setName("Cambodia");
		source = cs.saveConceptSource(source);
		
		source = new ConceptSource();
		source.setName("CIEL");
		source = cs.saveConceptSource(source);
		
		// A concept to be retired via CSV
		{
			Concept c = new Concept();
			c.setUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			c.setFullySpecifiedName(new ConceptName("CONCEPT_RETIRE", localeEn));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			cs.saveConcept(c);
		}
		
		// A concept to be edited via CSV
		{
			Concept c = new Concept();
			c.setUuid("276c5861-cd46-429f-9665-e067ddeca8e3");
			c.setFullySpecifiedName(new ConceptName("CONCEPT_EDIT_SHORTNAME", localeEn));
			c.setShortName(new ConceptName("Old short name", Locale.ENGLISH));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			cs.saveConcept(c);
		}
		
		// A concept with a mapping to be retired via CSV
		{
			Assert.assertNull(cs.getConceptByMapping("foo12bar", "Cambodia"));
			Concept c = new Concept();
			c.setFullySpecifiedName(new ConceptName("CONCEPT_WITH_MAPPING_TO_RETIRE", localeEn));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c.addConceptMapping((new Utils.ConceptMappingWrapper("Cambodia:foo12bar", cs)).getConceptMapping());
			cs.saveConcept(c);
			Assert.assertNotNull(cs.getConceptByMapping("foo12bar", "Cambodia"));
		}
		
		// A concept with members to be removed via CSV
		{
			Concept cm1 = new Concept();
			cm1.setFullySpecifiedName(new ConceptName("member_1", localeEn));
			cm1.setConceptClass(cs.getConceptClassByName("Misc"));
			cm1.setDatatype(cs.getConceptDatatypeByName("Text"));
			cm1 = cs.saveConcept(cm1);
			Concept cm2 = new Concept();
			cm2.setFullySpecifiedName(new ConceptName("member_2", localeEn));
			cm2.setConceptClass(cs.getConceptClassByName("Misc"));
			cm2.setDatatype(cs.getConceptDatatypeByName("Text"));
			cm2 = cs.saveConcept(cm2);
			
			Concept c = new Concept();
			c.setUuid("d803e973-1010-4415-8659-c011dec707c0");
			c.setFullySpecifiedName(new ConceptName("CONCEPT_REMOVE_MEMBERS", localeEn));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			c.addSetMember(cm1);
			c.addSetMember(cm2);
			c = cs.saveConcept(c);
		}
		
		// A concept numeric to be edited
		{
			ConceptNumeric cn = new ConceptNumeric();
			cn.setUuid("4280217a-eb93-4e2f-9684-28bed4690e7b");
			cn.setFullySpecifiedName(new ConceptName("CN_2_EDIT", localeEn));
			cn.setConceptClass(cs.getConceptClassByName("Misc"));
			cn.setDatatype(cs.getConceptDatatypeByName("Numeric"));
			cn.setLowNormal(44.8);
			cn.setHiNormal(55.2);
			cs.saveConcept(cn);
		}
		
		// A concept complex to be edited
		{
			ConceptComplex cc = new ConceptComplex();
			cc.setUuid("b0b15817-79d6-4c33-b7e9-bfa079d46f5f");
			cc.setFullySpecifiedName(new ConceptName("CC_2_EDIT", localeEn));
			cc.setConceptClass(cs.getConceptClassByName("Misc"));
			cc.setDatatype(cs.getConceptDatatypeByName("Complex"));
			cc.setHandler("TextHandler");
			cs.saveConcept(cc);
		}
		
		// Concepts to be fetched and edited by FSN
		{
			Concept c = new Concept();
			c.setFullySpecifiedName(new ConceptName("CONCEPT_FETCH_BY_FSN", localeEn));
			c.setShortName(new ConceptName("Old short name", localeEn));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			cs.saveConcept(c);
		}
		{
			Concept c = new Concept();
			c.setFullySpecifiedName(new ConceptName("គំនិត_ដោយ_FSN", localeKm));
			// c.setShortName(new ConceptName("ឈ្មោះខ្លីចាស់", localeKm));
			c.setShortName(new ConceptName("old km short name", localeKm));
			c.setConceptClass(cs.getConceptClassByName("Misc"));
			c.setDatatype(cs.getConceptDatatypeByName("Text"));
			cs.saveConcept(c);
		}
	}
	
	@Test
	public void load_shouldLoadDrugsAccordingToCsvFiles() {
		
		// Setup
		Concept c = null;
		{
			c = cs.getConceptByUuid("d803e973-1010-4415-8659-c011dec707c0");
			Assert.assertEquals(2, c.getSetMembers().size());
			Assert.assertTrue(c.isSet());
			c = cs.getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			Assert.assertFalse(c.isRetired());
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
			
			// Edited one
			Context.setLocale(localeEn);
			c = cs.getConceptByUuid("276c5861-cd46-429f-9665-e067ddeca8e3");
			Assert.assertEquals("New short name", c.getShortNameInLocale(localeEn).getName());
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
}

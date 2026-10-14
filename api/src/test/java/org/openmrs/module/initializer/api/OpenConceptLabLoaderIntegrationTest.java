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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.OpenConceptLabLoader;
import org.openmrs.module.openconceptlab.OpenConceptLabActivator;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import org.openmrs.Concept;

public class OpenConceptLabLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static final Locale LOCALE_SW = new Locale("sw");
	
	private static final Locale LOCALE_HT = new Locale("ht");
	
	@Autowired
	private OpenConceptLabLoader loader;
	
	@Autowired
	private ConceptService conceptService;
	
	@SuppressWarnings("unchecked")
	@BeforeAll
	public static void setupDaemonToken() {
		Map<String, DaemonToken> daemonTokens;
		try {
			Field field = ModuleFactory.class.getDeclaredField("daemonTokens");
			field.setAccessible(true);
			daemonTokens = (Map<String, DaemonToken>) field.get(null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		DaemonToken daemonToken = new DaemonToken("openconceptlab");
		daemonTokens.put(daemonToken.getId(), daemonToken);
		new OpenConceptLabActivator().setDaemonToken(daemonToken);
	}
	
	@AfterEach
	public void deleteAllData() {
		// this is necessary or else future test cases will fail because of two sources named CIEL
		super.deleteAllData();
	}
	
	@Test
	public void load_shouldImportOCLPackages() {
		// Replay        
		loader.load();
		
		// Verify by UUID
		{
			Concept c = conceptService.getConceptByUuid("1419AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Vaccine manufacturer", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assertions.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Finding", c.getConceptClass().getName());
			Assertions.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by UUID
		{
			Concept c = conceptService.getConceptByUuid("163100AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assertions.assertEquals(1, c.getDescriptions().size());
			Assertions.assertEquals("Question", c.getConceptClass().getName());
			Assertions.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by UUID
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByUuid("5864AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assertions.assertNotNull(c);
			Assertions.assertEquals("Yellow fever vaccination", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Vaccination given for Yellow Fever.", c.getDescription().toString());
			Assertions.assertEquals("Procedure", c.getConceptClass().getName());
			Assertions.assertEquals("N/A", c.getDatatype().getName());
		}
		
		// Verify by name
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("Vaccine manufacturer");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Vaccine manufacturer", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assertions.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Finding", c.getConceptClass().getName());
			Assertions.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by name
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("Procedure received by patient");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assertions.assertEquals(1, c.getDescriptions().size());
			Assertions.assertEquals("Question", c.getConceptClass().getName());
			Assertions.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("1419", "CIEL");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Vaccine manufacturer", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assertions.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Finding", c.getConceptClass().getName());
			Assertions.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("163100", "CIEL");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assertions.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assertions.assertEquals(1, c.getDescriptions().size());
			Assertions.assertEquals("Question", c.getConceptClass().getName());
			Assertions.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("166011", "CIEL");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Immunization, non-coded", c.getName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Immunization, non-coded", c.getFullySpecifiedName(Locale.ENGLISH).getName());
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Question", c.getConceptClass().getName());
			Assertions.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify in another locale
		{
			Context.setLocale(Locale.FRENCH);
			Concept c = conceptService.getConceptByName("Fabricant du vaccin");
			Assertions.assertNotNull(c);
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Finding", c.getConceptClass().getName());
			Assertions.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify just one name is enough
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("PNEUMOCOCCAL VACCINE");
			Assertions.assertNotNull(c);
			Assertions.assertEquals(6, c.getNames().size());
			Assertions.assertEquals(0, c.getShortNames().size());
			Assertions.assertEquals(0, c.getDescriptions().size());
			Assertions.assertEquals("Drug", c.getConceptClass().getName());
			Assertions.assertEquals("N/A", c.getDatatype().getName());
		}
		
		// Verify failures
		{
			Context.setLocale(Locale.ENGLISH);
			Assertions.assertNull(conceptService.getConceptByUuid("162339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assertions.assertNull(conceptService.getConceptByName("TETANUS BOOSTERS"));
			Assertions.assertNull(conceptService.getConceptByUuid("162330AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assertions.assertNull(conceptService.getConceptByName("TVACCINE MANUFACTURERS"));
			Assertions.assertNull(conceptService.getConceptByUuid("162337AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assertions.assertNull(conceptService.getConceptByName("DIPTHERIAYY"));
		}
		
		// Verify retirement
		{
			Concept c = conceptService.getConceptByUuid("162339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assertions.assertTrue(c.getRetired());
		}
		// Verify un-retirement
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByUuid("17AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assertions.assertNotNull(c);
			Assertions.assertFalse(c.getRetired());
			Assertions.assertEquals("Diptheria tetanus booster", c.getFullySpecifiedName(Locale.ENGLISH).getName());
			Assertions.assertEquals("Drug", c.getConceptClass().getName());
			Assertions.assertEquals("N/A", c.getDatatype().getName());
		}
	}
}

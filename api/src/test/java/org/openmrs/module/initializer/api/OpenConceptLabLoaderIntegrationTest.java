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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
	@BeforeClass
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
	
	@Test
	public void load_shouldImportOCLPackages() {
		// Replay        
		loader.load();
		
		// Verify by UUID
		{
			Concept c = conceptService.getConceptByUuid("1419AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("VACCINE MANUFACTURER", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assert.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Finding", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by UUID
		{
			Concept c = conceptService.getConceptByUuid("163100AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assert.assertEquals(1, c.getDescriptions().size());
			Assert.assertEquals("Question", c.getConceptClass().getName());
			Assert.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by UUID
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByUuid("5864AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assert.assertNotNull(c);
			Assert.assertEquals("YELLOW FEVER VACCINATION", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("Vaccine given for Yellow Fever.", c.getDescription().toString());
			Assert.assertEquals("Drug", c.getConceptClass().getName());
			Assert.assertEquals("N/A", c.getDatatype().getName());
		}
		
		// Verify by name
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("VACCINE MANUFACTURER");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("VACCINE MANUFACTURER", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assert.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Finding", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by name
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("Procedure received by patient");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assert.assertEquals(1, c.getDescriptions().size());
			Assert.assertEquals("Question", c.getConceptClass().getName());
			Assert.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("1419", "CIEL");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("VACCINE MANUFACTURER", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("vaccine maker", c.getShortNameInLocale(Locale.ENGLISH).getName());
			Assert.assertEquals("Fabricant du vaccin", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Kiwanda cha kutengeneza chanjo", c.getName(LOCALE_SW).getName());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Finding", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("163100", "CIEL");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("Procedure received by patient", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("Procédure reçue par le patient", c.getName(Locale.FRENCH).getName());
			Assert.assertEquals("Pasyan te resevwa pwosedi", c.getName(LOCALE_HT).getName());
			Assert.assertEquals(1, c.getDescriptions().size());
			Assert.assertEquals("Question", c.getConceptClass().getName());
			Assert.assertEquals("Coded", c.getDatatype().getName());
		}
		
		// Verify by Mapping
		{
			Concept c = conceptService.getConceptByMapping("166011", "CIEL");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("Immunization, non-coded", c.getName(Locale.ENGLISH).getName());
			Assert.assertEquals("Immunization, non-coded", c.getFullySpecifiedName(Locale.ENGLISH).getName());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Question", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify in another locale
		{
			Context.setLocale(Locale.FRENCH);
			Concept c = conceptService.getConceptByName("Fabricant du vaccin");
			Assert.assertNotNull(c);
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Finding", c.getConceptClass().getName());
			Assert.assertEquals("Text", c.getDatatype().getName());
		}
		
		// Verify just one name is enough
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByName("PNEUMOCOCCAL VACCINE");
			Assert.assertNotNull(c);
			Assert.assertEquals(3, c.getNames().size());
			Assert.assertEquals(0, c.getShortNames().size());
			Assert.assertEquals(0, c.getDescriptions().size());
			Assert.assertEquals("Drug", c.getConceptClass().getName());
			Assert.assertEquals("N/A", c.getDatatype().getName());
		}
		
		// Verify failures
		{
			Context.setLocale(Locale.ENGLISH);
			Assert.assertNull(conceptService.getConceptByUuid("162339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assert.assertNull(conceptService.getConceptByName("TETANUS BOOSTERS"));
			Assert.assertNull(conceptService.getConceptByUuid("162330AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assert.assertNull(conceptService.getConceptByName("TVACCINE MANUFACTURERS"));
			Assert.assertNull(conceptService.getConceptByUuid("162337AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"));
			Assert.assertNull(conceptService.getConceptByName("DIPTHERIAYY"));
		}
		
		// Verify retirement
		{
			Concept c = conceptService.getConceptByUuid("162339AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assert.assertTrue(c.getRetired());
		}
		// Verify un-retirement
		{
			Context.setLocale(Locale.ENGLISH);
			Concept c = conceptService.getConceptByUuid("17AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			Assert.assertNotNull(c);
			Assert.assertFalse(c.getRetired());
			Assert.assertEquals("DIPTHERIA TETANUS BOOSTER", c.getFullySpecifiedName(Locale.ENGLISH).getName());
			Assert.assertEquals("Drug", c.getConceptClass().getName());
			Assert.assertEquals("N/A", c.getDatatype().getName());
		}
	}
}

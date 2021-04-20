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
	
	@Autowired
	private OpenConceptLabLoader loader;
	
	@Autowired
	private ConceptService conceptService;
	
	private Locale localeEn = Locale.ENGLISH;
	
	private Locale localeFr = new Locale("fr");
	
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
	    // Verif setup
	    {
	        Concept c = conceptService.getConceptByUuid("1419AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	        Assert.assertEquals("Text", c.getDatatype());
	        Assert.assertFalse(c.getRetired());
	    }

	    // Replay        
	    loader.load();

	    // Verif by name
	    {
	        Context.setLocale(localeEn);
	        Concept c = conceptService.getConceptByName("VACCINE MANUFACTURER");
	        Assert.assertNotNull(c);
	        Assert.assertEquals(0, c.getDescriptions().size());
	        Assert.assertEquals("Finding", c.getConceptClass().getName());
	        Assert.assertEquals("Text", c.getDatatype().getName());
	    }
	    // Verify by UUID
	    {
	        Context.setLocale(localeEn);
	        Concept c = conceptService.getConceptByUuid("3004BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
	        Assert.assertNotNull(c);
	        Assert.assertEquals("YELLOW FEVER VACCINATION", c.getName(localeEn).getName());
	        Assert.assertEquals("Vaccine given for Yellow Fever.", c.getDescription().toString());
	        Assert.assertEquals("Drug", c.getConceptClass().getName());
	        Assert.assertEquals("N/A", c.getDatatype().getName());
	    }
	    // Verify in another locale
	    {
	        Context.setLocale(localeFr);
	        Concept c = conceptService.getConceptByName("Fabricant du vaccin");
	        Assert.assertNotNull(c);
	        Assert.assertEquals(0, c.getDescriptions().size());
	        Assert.assertEquals("Finding", c.getConceptClass().getName());
	        Assert.assertEquals("Text", c.getDatatype().getName());;
	    }
	    // Verify just one name is enough
	    {
	        Context.setLocale(localeEn);
	        Concept c = conceptService.getConceptByName("PNEUMOCOCCAL VACCINE");
	        Assert.assertNotNull(c);
	        Assert.assertEquals(3, c.getNames().size());
	        Assert.assertEquals(0, c.getShortNames().size());
	        Assert.assertEquals(0, c.getDescriptions().size());
	        Assert.assertEquals("Drug", c.getConceptClass().getName());
	        Assert.assertEquals("N/A", c.getDatatype().getName());;
	    }
	    // Failed ones
	    {
	        Context.setLocale(localeEn);
	        Assert.assertNull(conceptService.getConceptByUuid("db2f5104-3171-11e7-93ae-92361f002670"));
	        Assert.assertNull(conceptService.getConceptByName("MEASLES VACCINATION"));
	        Assert.assertNull(conceptService.getConceptByName("db2f5460-3171-11e7-93ae-92361f002672"));
	        Assert.assertNull(conceptService.getConceptByName("HEPATITIS B VACCINATION"));
	        Assert.assertNull(conceptService.getConceptByUuid("00b29984-3183-11e7-93ae-92361f002679"));;
	    }
	    // Retired one
	    {
	        Concept c = conceptService.getConceptByUuid("83531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	        Assert.assertTrue(c.isRetired());;
	    }
	    // Un-retire one
	    {
	        Context.setLocale(localeEn);
	        Concept c = conceptService.getConceptByUuid("17AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	        Assert.assertNotNull(c);
	        Assert.assertFalse(c.getRetired());
	        Assert.assertEquals("DIPTHERIA TETANUS BOOSTER", c.getFullySpecifiedName(localeEn).getName());
	        Assert.assertEquals("Drug", c.getConceptClass().getName());
	        Assert.assertEquals("N/A", c.getDatatype().getName());
	    }
	    
	}
}
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
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.test.Verifies;

public class cDomainInitializerServiceTest extends DomainBaseModuleContextSensitiveTest {
	
	private ConceptService cs;
	
	@Override
	protected String getDomain() {
		return InitializerConstants.DOMAIN_C;
	}
	
	@Before
	public void setup() {
		cs = Context.getConceptService();
	}
	
	@Test
	@Verifies(value = "should", method = "loadConcepts()")
	public void loadConcepts_should() {
		
		// Setup
		Concept c = null;
		Locale localeEn = Locale.ENGLISH;
		Locale localeKm = new Locale("km", "KH");
		
		// Replay
		getService().loadConcepts();
		
		// Verif by name
		Context.setLocale(localeEn);
		c = cs.getConceptByName("CAMBODIA_Nationality");
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
		c = cs.getConceptByName("CAMBODIA_Kavet");
		Assert.assertNotNull(c);
		Assert.assertEquals(1, c.getNames().size());
		Assert.assertEquals(0, c.getShortNames().size());
		Assert.assertEquals(0, c.getDescriptions().size());
		Assert.assertEquals("Misc", c.getConceptClass().getName());
		Assert.assertEquals("Text", c.getDatatype().getName());
		
		// Failed ones
		Context.setLocale(localeEn);
		Assert.assertNull(cs.getConceptByUuid("db2f5104-3171-11e7-93ae-92361f002671"));
		Assert.assertNull(cs.getConceptByName("CAMBODIA_Krung"));
		Assert.assertNull(cs.getConceptByName("db2f5460-3171-11e7-93ae-92361f002671"));
		Assert.assertNull(cs.getConceptByName("CAMBODIA_Lao"));
		Assert.assertNull(cs.getConceptByUuid("00b29984-3183-11e7-93ae-92361f002671"));
	}
}

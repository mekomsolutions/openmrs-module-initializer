/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.initializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class InitializerMessageSourceIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	MessageSourceService messageSourceService;
	
	protected InitializerMessageSource inizSrc;
	
	@Before
	public void setup() {
		inizSrc = (InitializerMessageSource) messageSourceService.getActiveMessageSource();
	}
	
	@Test
	public void getCachedMessages_shouldLoadMessageProperties() {
		
		inizSrc.initialize();
		
		// Working in fr
		Context.setLocale(Locale.FRENCH);
		testMessage("Clinique", "metadata.healthcenter");
		testMessage("Ceci est la description d'une clinique.", "metadata.healthcenter.description");
		
		// Working in en
		Context.setLocale(Locale.ENGLISH);
		testMessage("Health center", "metadata.healthcenter");
		testMessage("This is the description of a health centre.", "metadata.healthcenter.description");
		
		testMessage("Kingdom of Cambodia", "addresshierarchy.cambodia");
	}
	
	@Test
	public void getCachedMessages_shouldLoadMessagePropertiesWithArguments() {
		inizSrc.initialize();
		Context.setLocale(Locale.FRENCH);
		String code = "metadata.healthcenter.description.named";
		String msg = messageSourceService.getMessage(code, new Object[] { "Azul" }, Context.getLocale());
		Assert.assertEquals("Ceci est la description de la clinique Azul.", msg);
	}
	
	@Test
	public void shouldLoadMoreSpecificTranslationFromClasspath() {
		inizSrc.initialize();
		Context.setLocale(Locale.FRANCE);
		// Defined in classpath fr as "Bonjour"
		// Defined in classpath fr_FR as "Bonjour from France"
		// Defined in Iniz fr as "Bonjour from Iniz"
		testMessage("Bonjour from France", "greeting");
	}
	
	@Test
	public void filesShouldOverrideClasspathIfTheyMatch() {
		inizSrc.initialize();
		// Defined in classpath fr as "Bonjour"
		// Defined in classpath fr_FR as "Bonjour from France"
		// Defined in Iniz fr as "Bonjour from Iniz"
		Context.setLocale(Locale.FRENCH);
		testMessage("Bonjour from Iniz", "greeting");
	}
	
	@Test
	public void shouldDefaultToSystemLocaleIfNoMessageFoundInLocale() {
		inizSrc.initialize();
		Locale.setDefault(Locale.ENGLISH);
		Context.setLocale(Locale.FRANCE);
		testMessage("Only defined in English", "metadata.healthcenter.onlyInEnglish");
	}
	
	protected void testMessage(String message, String code) {
		Assert.assertEquals(message, messageSourceService.getMessage(code));
	}
}

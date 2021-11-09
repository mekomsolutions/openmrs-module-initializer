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

import org.apache.commons.lang3.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.util.LocaleUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InitializerMessageSourceIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	public static final Locale HAITIAN_KREYOL = LocaleUtils.toLocale("ht");
	
	public static final Locale CAMBODIA_KHMER = LocaleUtils.toLocale("km_KH");
	
	@Autowired
	MessageSourceService messageSourceService;
	
	protected InitializerMessageSource inizSrc;
	
	@Before
	public void setup() {
		inizSrc = (InitializerMessageSource) messageSourceService.getActiveMessageSource();
		if (inizSrc.getPresentations().isEmpty()) {
			inizSrc.initialize();
		}
		Locale.setDefault(Locale.ENGLISH);
	}
	
	@Test
	public void shouldConfigureActiveMessageSource() {
		assertEquals(InitializerMessageSource.class, messageSourceService.getActiveMessageSource().getClass());
		assertNull(messageSourceService.getActiveMessageSource().getParentMessageSource());
	}
	
	@Test
	public void shouldIncludeMessagesInAddressHierarchyDomain() {
		Context.setLocale(Locale.ENGLISH);
		testMessage("Kingdom of Cambodia", "addresshierarchy.cambodia");
		Context.setLocale(CAMBODIA_KHMER);
		testMessage("ព្រះរាជាណាចក្រកម្ពុជា", "addresshierarchy.cambodia");
	}
	
	@Test
	public void shouldIncludeMessagesInMessagePropertiesDomain() {
		Context.setLocale(Locale.ENGLISH);
		testMessage("Health center", "metadata.healthcenter");
		testMessage("This is the description of a health centre.", "metadata.healthcenter.description");
		Context.setLocale(Locale.FRENCH);
		testMessage("Clinique", "metadata.healthcenter");
		testMessage("Ceci est la description d'une clinique.", "metadata.healthcenter.description");
	}
	
	@Test
	public void shouldIncludeMessagesOnClasspath() {
		Context.setLocale(Locale.getDefault());
		testMessage("Default", "messageFileLocale");
		Context.setLocale(Locale.FRENCH);
		testMessage("fr", "messageFileLocale");
		Context.setLocale(Locale.FRANCE);
		testMessage("fr_FR", "messageFileLocale");
		Context.setLocale(HAITIAN_KREYOL);
		testMessage("ht", "messageFileLocale");
	}
	
	@Test
	public void getCachedMessages_shouldLoadMessagePropertiesWithArguments() {
		Context.setLocale(Locale.FRENCH);
		String code = "metadata.healthcenter.description.named";
		String msg = messageSourceService.getMessage(code, new Object[] { "Azul" }, Context.getLocale());
		assertEquals("Ceci est la description de la clinique Azul.", msg);
	}
	
	@Test
	public void shouldLoadTranslationsWithAppropriateFallbacks() {
		Context.setLocale(HAITIAN_KREYOL);
		testMessage("Only In ht", "messageOnlyInHt"); // Direct match
		testMessage("messageOnlyInFrFr", "messageOnlyInFrFr"); // No match, fallback to message code
		testMessage("Only In fr", "messageOnlyInFr"); // Fallback to explicit added fallback
		testMessage("Only In Default", "messageOnlyInDefault"); // Fallback to system default locale
		
		Context.setLocale(Locale.FRANCE);
		testMessage("messageOnlyInHt", "messageOnlyInHt"); // Fallback to message code
		testMessage("Only In fr FR", "messageOnlyInFrFr"); // Direct match
		testMessage("Only In fr", "messageOnlyInFr"); // Fallback to language fallback
		testMessage("Only In Default", "messageOnlyInDefault"); // Fallback to system default locale
		
		Context.setLocale(Locale.FRENCH);
		testMessage("messageOnlyInHt", "messageOnlyInHt"); // No match, fallback to message code
		testMessage("messageOnlyInFrFr", "messageOnlyInFrFr"); // No match, fallback to message code
		testMessage("Only In fr", "messageOnlyInFr"); // Direct match
		testMessage("Only In Default", "messageOnlyInDefault"); // Fallback to system default locale
	}
	
	@Test
	public void shouldOverrideMessagesFromClasspathWithMessagesFromInitializer() {
		Context.setLocale(HAITIAN_KREYOL);
		testMessage("Bonjou", "greeting"); // Only in classpath
		Context.setLocale(Locale.FRANCE);
		testMessage("Bonjour from France", "greeting"); // Specific version on classpath over less specific in domain
		Context.setLocale(Locale.FRENCH);
		testMessage("Bonjour from Iniz", "greeting"); // Version in Iniz overrides version on classpath
		Context.setLocale(Locale.ENGLISH);
		testMessage("Hello", "greeting");
	}
	
	@Test
	public void shouldDefaultToDefaultLocaleSettingIfNoMessageFoundInLocale() {
		Locale startingDefaultLocale = LocaleUtility.getDefaultLocale();
		LocaleUtility.setDefaultLocaleCache(LocaleUtils.toLocale("es"));
		Context.setLocale(Locale.FRANCE);
		testMessage("Spanish", "englishAndSpanishOnly");
		LocaleUtility.setDefaultLocaleCache(Locale.ENGLISH);
		testMessage("English", "englishAndSpanishOnly");
		LocaleUtility.setDefaultLocaleCache(startingDefaultLocale);
		testMessage("English", "englishAndSpanishOnly");
	}
	
	@Test
	public void shouldDefaultToSystemLocaleIfNoMessageFoundInLocale() {
		Context.setLocale(Locale.FRANCE);
		testMessage("Only defined in English", "metadata.healthcenter.onlyInEnglish");
	}
	
	@Test
	public void shouldDefaultToMessageCodeIfNoMessageFound() {
		Context.setLocale(Locale.FRANCE);
		testMessage("invalid.code", "invalid.code");
	}
	
	protected void testMessage(String message, String code) {
		assertEquals(message, messageSourceService.getMessage(code));
	}
}

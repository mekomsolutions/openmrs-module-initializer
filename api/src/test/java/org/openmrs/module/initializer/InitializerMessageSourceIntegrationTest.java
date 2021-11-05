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

import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.messagesource.impl.CachedMessageSource;

public class InitializerMessageSourceIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	protected MessageSourceService ms;
	
	protected InitializerMessageSource inizSrc;
	
	protected CachedMessageSource parentSrc;
	
	@Before
	public void setup() {
		ms = Context.getMessageSourceService();
		parentSrc = new CachedMessageSource();
		inizSrc = (InitializerMessageSource) ms.getActiveMessageSource();
		inizSrc.setParentMessageSource(parentSrc);
		inizSrc.refreshCache();
	}
	
	protected void addToParent(Locale locale, String key, String value) {
		parentSrc.addPresentation(new PresentationMessage(key, locale, value, ""));
	}
	
	@Test
	public void getCachedMessages_shouldLoadMessageProperties() {
		
		// Working in fr
		Context.setLocale(Locale.FRENCH);
		Assert.assertEquals("Clinique", ms.getMessage("metadata.healthcenter"));
		Assert.assertEquals("Ceci est la description d'une clinique.", ms.getMessage("metadata.healthcenter.description"));
		
		// Working in en
		Context.setLocale(Locale.ENGLISH);
		Assert.assertEquals("Health center", ms.getMessage("metadata.healthcenter"));
		Assert.assertEquals("This is the description of a health centre.",
		    ms.getMessage("metadata.healthcenter.description"));
		
		Assert.assertEquals("Kingdom of Cambodia", ms.getMessage("addresshierarchy.cambodia"));
	}
	
	@Test
	public void getCachedMessages_shouldLoadMessagePropertiesWithArguments() {
		
		Context.setLocale(Locale.FRENCH);
		Assert.assertEquals("Ceci est la description de la clinique Azul.",
		    ms.getMessage("metadata.healthcenter.description.named", new Object[] { "Azul" }, Context.getLocale()));
	}
	
	@Test
	public void getPresentations_shouldContainParentPresentations() {
		// setup
		int initSize = inizSrc.getPresentations().size();
		MessageSourceService coreSrc = (MessageSourceService) applicationContext.getBean("messageSourceServiceTarget");
		coreSrc.addPresentation(new PresentationMessage("helloWorld.692af26f1c07", Locale.ENGLISH, "Hello World!", null));
		coreSrc.addPresentation(
		    new PresentationMessage("helloWorld.692af26f1c07", Locale.FRENCH, "Bonjour le Monde !", null));
		
		// replay
		Collection<PresentationMessage> allPres = inizSrc.getPresentations();
		
		// verify
		Assert.assertEquals(initSize + 2, allPres.size());
		Assert.assertEquals("Hello World!",
		    Context.getMessageSourceService().getMessage("helloWorld.692af26f1c07", null, Locale.ENGLISH));
		Assert.assertEquals("Bonjour le Monde !",
		    Context.getMessageSourceService().getMessage("helloWorld.692af26f1c07", null, Locale.FRENCH));
	}
	
	@Test
	public void shouldLoadFromParent() {
		addToParent(Locale.FRENCH, "greeting", "bonjour");
		Context.setLocale(Locale.FRENCH);
		Assert.assertEquals("bonjour", ms.getMessage("greeting"));
	}
	
	@Test
	public void shouldOverrideParentIfCodesAreTheSame() {
		addToParent(Locale.FRENCH, "metadata.healthcenter", "Centre de Sante");
		Context.setLocale(Locale.FRENCH);
		Assert.assertEquals("Clinique", ms.getMessage("metadata.healthcenter"));
	}
	
	@Test
	public void parentShouldOverrideInizIfMatchesAMoreSpecificLocale() {
		addToParent(Locale.FRANCE, "metadata.healthcenter", "Centre de Sante");
		Context.setLocale(Locale.FRANCE);
		Assert.assertEquals("Centre de Sante", ms.getMessage("metadata.healthcenter"));
	}
	
	@Test
	public void shouldDefaultToSystemLocaleIfNoMessageFoundInLocale() {
		Locale.setDefault(Locale.ENGLISH);
		Context.setLocale(Locale.FRANCE);
		Assert.assertEquals("Only defined in English", ms.getMessage("metadata.healthcenter.onlyInEnglish"));
	}
}

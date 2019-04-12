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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;

public class InitializerMessageSourceIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	protected InitializerMessageSource inizSrc;
	
	@Before
	public void setup() {
		inizSrc = (InitializerMessageSource) Context.getMessageSourceService().getActiveMessageSource();
	}
	
	@Test
	public void refreshCache_shouldLoadMessageProperties() {
		
		// Setup
		MessageSourceService ms = Context.getMessageSourceService();
		
		// Replay
		inizSrc.refreshCache();
		
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
}

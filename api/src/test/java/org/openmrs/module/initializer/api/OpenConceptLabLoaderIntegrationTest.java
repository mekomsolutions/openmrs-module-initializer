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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.OpenConceptLabLoader;
import org.openmrs.module.openconceptlab.OpenConceptLabActivator;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Map;
import org.openmrs.Concept;

public class OpenConceptLabLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private OpenConceptLabLoader loader;
	
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
		{
			Concept c = Context.getConceptService().getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			Assert.assertNull(c);
		}
		
		loader.load();
		
		{
			Concept c = Context.getConceptService().getConceptByUuid("4421da0d-42d0-410d-8ffd-47ec6f155d8f");
			Assert.assertEquals(2, c.getSetMembers().size());
			Assert.assertTrue(c.getSet());
		}
	}
}

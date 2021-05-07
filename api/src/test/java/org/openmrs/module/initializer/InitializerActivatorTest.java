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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.InitializerServiceImpl;
import org.openmrs.module.initializer.api.MockLoader;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.openmrs.module.initializer.Domain.CONCEPTS;
import static org.openmrs.module.initializer.Domain.DRUGS;
import static org.openmrs.module.initializer.Domain.ENCOUNTER_TYPES;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_DISABLED;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_STARTUP_LOAD_FAIL_ON_ERROR;

public class InitializerActivatorTest {
	
	private InitializerService iniz;
	
	private InitializerActivator activator;
	
	private MockLoader conceptsLoader = Mockito.spy(new MockLoader(CONCEPTS));
	
	private MockLoader encounterTypesLoader = Mockito.spy(new MockLoader(ENCOUNTER_TYPES, true));
	
	private MockLoader drugsLoader = Mockito.spy(new MockLoader(DRUGS));
	
	private InitializerConfig cfg = new InitializerConfig();
	
	@BeforeEach
	public void before() {
		final List<Loader> loaders = Arrays.asList(conceptsLoader, encounterTypesLoader, drugsLoader);
		iniz = new InitializerServiceImpl() {
			
			@Override
			public List<Loader> getLoaders() {
				return loaders;
			}
		};
		((InitializerServiceImpl) iniz).setConfig(cfg);
		activator = new InitializerActivator() {
			
			@Override
			protected InitializerService getInitializerService() {
				return iniz;
			}
		};
	}
	
	@Test
	public void started_shouldLoadAllDomainsEvenIfOneThrowsAnErrorByDefault() throws Exception {
		Properties props = new Properties();
		Context.setRuntimeProperties(props);
		cfg.init();
		activator.started();
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
	}
	
	@Test
	public void started_shouldLoadAllDomainsEvenIfOneThrowsAnErrorIfConfigured() throws Exception {
		Properties props = new Properties();
		props.put(PROPS_STARTUP_LOAD, PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR);
		Context.setRuntimeProperties(props);
		cfg.init();
		activator.started();
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
	}
	
	@Test
	public void started_shouldStopLoadingDomainsOnFailureIfConfigured() throws Exception {
		Properties props = new Properties();
		props.put(PROPS_STARTUP_LOAD, PROPS_STARTUP_LOAD_FAIL_ON_ERROR);
		Context.setRuntimeProperties(props);
		cfg.init();
		Exception exception = null;
		try {
			activator.started();
		}
		catch (Exception e) {
			exception = e;
		}
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNotNull(exception);
		Assertions.assertEquals(ModuleException.class, exception.getClass());
	}
	
	@Test
	public void started_shouldNotLoadAnyDomainsIfConfigured() throws Exception {
		Properties props = new Properties();
		props.put(PROPS_STARTUP_LOAD, PROPS_STARTUP_LOAD_DISABLED);
		Context.setRuntimeProperties(props);
		cfg.init();
		Assertions.assertEquals(0, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
	}
}

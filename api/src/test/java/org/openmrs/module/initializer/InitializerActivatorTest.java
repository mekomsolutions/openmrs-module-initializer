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
import org.openmrs.module.initializer.api.logging.InitializerLogConfigurator;
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
	
	private Properties props;
	
	private Exception exceptionThrown;
	
	@BeforeEach
	public void setup() {
		final List<Loader> loaders = Arrays.asList(conceptsLoader, encounterTypesLoader, drugsLoader);
		iniz = new InitializerServiceImpl() {
			
			private boolean locked = false;
			
			@Override
			public List<Loader> getLoaders() {
				return loaders;
			}
			
			@Override
			public String getOrCreateNodeId() {
				return "test-node";
			}
			
			@Override
			public Boolean isConfigChanged() {
				return true;
			}
			
			@Override
			public void updateChecksums() {
				// unit test
			}
			
			@Override
			public Boolean tryAcquireLock(String nodeId) {
				if (locked) {
					return false;
				}
				locked = true;
				return true;
			}
			
			@Override
			public Boolean isLocked() {
				return locked;
			}
			
			@Override
			public void releaseLock(String nodeId) {
				locked = false;
			}
			
		};
		((InitializerServiceImpl) iniz).setConfig(cfg);
		activator = new InitializerActivator() {
			
			@Override
			protected InitializerService getInitializerService() {
				return iniz;
			}
			
			@Override
			protected List<InitializerLogConfigurator> getInitializerLogConfigurator() {
				return null;
			}
			
			@Override
			protected InitializerMessageSource getInitializerMessageSource() {
				return new InitializerMessageSource();
			}
		};
		props = new Properties();
		System.clearProperty(PROPS_STARTUP_LOAD);
	}
	
	protected void startActivator(String startupLoadConfiguration) {
		startActivator(startupLoadConfiguration, false);
	}
	
	protected void startActivator(String startupLoadConfiguration, boolean useSystemProperty) {
		if (startupLoadConfiguration != null) {
			if (useSystemProperty) {
				System.setProperty(PROPS_STARTUP_LOAD, startupLoadConfiguration);
			} else {
				props.put(PROPS_STARTUP_LOAD, startupLoadConfiguration);
			}
		}
		Context.setRuntimeProperties(props);
		cfg.init();
		exceptionThrown = null;
		try {
			activator.started();
		}
		catch (Exception e) {
			exceptionThrown = e;
		}
	}
	
	@Test
	public void started_shouldLoadAllDomainsEvenIfOneThrowsAnErrorByDefault() {
		
		startActivator(null);
		Assertions.assertNull(System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertNull(props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldLoadAllDomainsEvenIfOneThrowsAnErrorIfConfigured() {
		
		startActivator(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR);
		Assertions.assertNull(System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertEquals(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldStopLoadingDomainsOnFailureIfConfigured() {
		
		startActivator(PROPS_STARTUP_LOAD_FAIL_ON_ERROR);
		Assertions.assertNull(System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertEquals(PROPS_STARTUP_LOAD_FAIL_ON_ERROR, props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNotNull(exceptionThrown);
		Assertions.assertEquals(ModuleException.class, exceptionThrown.getClass());
	}
	
	@Test
	public void started_shouldNotLoadAnyDomainsIfConfigured() {
		
		startActivator(PROPS_STARTUP_LOAD_DISABLED);
		Assertions.assertNull(System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertEquals(PROPS_STARTUP_LOAD_DISABLED, props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(0, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldLoadAllDomainsEvenIfOneThrowsAnErrorIfConfiguredWithSystemProperty() {
		
		startActivator(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, true);
		Assertions.assertEquals(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertNull(props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldNotLoadAnyDomainsIfConfiguredWithSystemProperty() {
		
		startActivator(PROPS_STARTUP_LOAD_DISABLED, true);
		Assertions.assertEquals(PROPS_STARTUP_LOAD_DISABLED, System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertNull(props.get(PROPS_STARTUP_LOAD));
		
		Assertions.assertEquals(0, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldNotRunWhenLocked() {
		Assertions.assertFalse(iniz.isLocked());
		
		// node1 acquires the lock. This should prevent the activator from executing the initializer.
		boolean acquired = iniz.tryAcquireLock("node1");
		Assertions.assertTrue(acquired);
		Assertions.assertTrue(iniz.isLocked());
		
		// Attempt to start the activator while lock is already held.
		startActivator(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, true);
		Assertions.assertEquals(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertNull(props.get(PROPS_STARTUP_LOAD));
		
		// Lock should still be held (activator must NOT release it)
		Assertions.assertTrue(iniz.isLocked());
		
		// No loaders should have been executed
		Assertions.assertEquals(0, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(0, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
	}
	
	@Test
	public void started_shouldRunWhenLockIsAvailable() {
		Assertions.assertFalse(iniz.isLocked());
		
		// Start activator
		startActivator(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, true);
		Assertions.assertEquals(PROPS_STARTUP_LOAD_CONTINUE_ON_ERROR, System.getProperty(PROPS_STARTUP_LOAD));
		Assertions.assertNull(props.get(PROPS_STARTUP_LOAD));
		
		// Loaders should have been executed
		Assertions.assertEquals(1, conceptsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, encounterTypesLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertEquals(1, drugsLoader.getNumberOfTimesLoadUnsafeCompleted());
		Assertions.assertNull(exceptionThrown);
		
		// Lock should have been released after execution
		Assertions.assertFalse(iniz.isLocked());
	}
}

package org.openmrs.module.initializer.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.openmrs.module.initializer.Domain.CONCEPTS;
import static org.openmrs.module.initializer.Domain.DRUGS;
import static org.openmrs.module.initializer.Domain.ENCOUNTER_TYPES;
import static org.openmrs.module.initializer.InitializerConstants.PROPS_DOMAINS;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.InitializerConfig;
import org.openmrs.module.initializer.api.loaders.Loader;

public class InitializerServiceImplTest {
	
	private InitializerService iniz;
	
	private Loader conceptsLoader = Mockito.spy(new MockLoader(CONCEPTS));
	
	private Loader encounterTypesLoader = Mockito.spy(new MockLoader(ENCOUNTER_TYPES));
	
	private Loader drugsLoader = Mockito.spy(new MockLoader(DRUGS));
	
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
	}
	
	@Test
	public void load_shouldFollowInclusionList() throws Exception {
		// setup
		Properties props = new Properties();
		props.put(PROPS_DOMAINS, "concepts,encountertypes");
		Context.setRuntimeProperties(props);
		cfg.init();
		
		// replay
		iniz.load();
		
		// verify
		verify(conceptsLoader, times(1)).loadUnsafe(anyList(), anyBoolean());
		verify(encounterTypesLoader, times(1)).loadUnsafe(anyList(), anyBoolean());
		verify(drugsLoader, never()).loadUnsafe(anyList(), anyBoolean());
	}
	
	@Test
	public void load_shouldSkipExclusionList() throws Exception {
		// setup
		Properties props = new Properties();
		props.put(PROPS_DOMAINS, "!concepts,drugs");
		Context.setRuntimeProperties(props);
		cfg.init();
		
		// replay
		iniz.load();
		
		// verify
		verify(conceptsLoader, never()).loadUnsafe(anyList(), anyBoolean());
		verify(encounterTypesLoader, times(1)).loadUnsafe(anyList(), anyBoolean());
		verify(drugsLoader, never()).loadUnsafe(anyList(), anyBoolean());
	}
	
	@Test
	public void addKeyValues_shouldFillKeyValuesCache() throws Exception {
		
		InputStream is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/jsonKeyValues.json");
		iniz.addKeyValues(is);
		
		Assertions.assertEquals("value1", iniz.getValueFromKey("key1"));
		Assertions.assertEquals("value2", iniz.getValueFromKey("key2"));
		Assertions.assertEquals("value3", iniz.getValueFromKey("key3"));
		
		is = IOUtils.toInputStream("{\"key1\":\"value12\"}");
		iniz.addKeyValues(is);
		
		Assertions.assertEquals("value12", iniz.getValueFromKey("key1"));
	}
	
	@Test
	public void getBooleanFromKey_shouldHandleAllCases() {
		
		final String KEY = "key.to.bool.value";
		
		iniz.addKeyValue(KEY, "true");
		Assertions.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "false");
		Assertions.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "yes");
		Assertions.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "no");
		Assertions.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "1");
		Assertions.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "0");
		Assertions.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "foo");
		Assertions.assertNull(iniz.getBooleanFromKey(KEY));
	}
}

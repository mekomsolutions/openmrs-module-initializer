package org.openmrs.module.initializer.api;

import static org.mockito.Matchers.any;
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
import org.junit.Assert;
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
		verify(conceptsLoader, times(1)).load(any());
		verify(encounterTypesLoader, times(1)).load(any());
		verify(drugsLoader, never()).load(any());
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
		verify(conceptsLoader, never()).load(any());
		verify(encounterTypesLoader, times(1)).load(any());
		verify(drugsLoader, never()).load(any());
	}
	
	@Test
	public void addKeyValues_shouldFillKeyValuesCache() throws Exception {
		
		InputStream is = getClass().getClassLoader()
		        .getResourceAsStream("org/openmrs/module/initializer/include/jsonKeyValues.json");
		iniz.addKeyValues(is);
		
		Assert.assertEquals("value1", iniz.getValueFromKey("key1"));
		Assert.assertEquals("value2", iniz.getValueFromKey("key2"));
		Assert.assertEquals("value3", iniz.getValueFromKey("key3"));
		
		is = IOUtils.toInputStream("{\"key1\":\"value12\"}");
		iniz.addKeyValues(is);
		
		Assert.assertEquals("value12", iniz.getValueFromKey("key1"));
	}
	
	@Test
	public void getBooleanFromKey_shouldHandleAllCases() {
		
		final String KEY = "key.to.bool.value";
		
		iniz.addKeyValue(KEY, "true");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "false");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "yes");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "no");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "1");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.addKeyValue(KEY, "0");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.addKeyValue(KEY, "foo");
		Assert.assertNull(iniz.getBooleanFromKey(KEY));
	}
}

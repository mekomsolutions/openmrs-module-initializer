package org.openmrs.module.initializer.api.impl;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class InitializerServiceImplTest {
	
	@Test
	public void addKeyValues_shouldFillKeyValuesCache() throws Exception {
		
		InitializerServiceImpl iniz = new InitializerServiceImpl();
		
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
		InitializerServiceImpl iniz = new InitializerServiceImpl();
		
		iniz.keyValueCache.put(KEY, "true");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.keyValueCache.put(KEY, "false");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.keyValueCache.put(KEY, "yes");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.keyValueCache.put(KEY, "no");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.keyValueCache.put(KEY, "1");
		Assert.assertTrue(iniz.getBooleanFromKey(KEY));
		iniz.keyValueCache.put(KEY, "0");
		Assert.assertFalse(iniz.getBooleanFromKey(KEY));
		
		iniz.keyValueCache.put(KEY, "foo");
		Assert.assertNull(iniz.getBooleanFromKey(KEY));
	}
}

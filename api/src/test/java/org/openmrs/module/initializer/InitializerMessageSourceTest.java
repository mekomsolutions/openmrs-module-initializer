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

import java.io.File;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InitializerMessageSourceTest {
	
	private String dirPath = "";
	
	@Before
	public void setup() {
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder
		        .append(
		            getClass().getClassLoader().getResource(DomainBaseModuleContextSensitiveTest.appDataTestDir).getPath())
		        .append(File.separator).append(InitializerConstants.DIR_NAME_CONFIG).append(File.separator)
		        .append(InitializerConstants.DOMAIN_MSGPROP);
		dirPath = pathBuilder.toString();
	}
	
	@Test
	public void getMessageProperties_shouldScanMessagesFiles() {
		
		InitializerMessageSource src = new InitializerMessageSource();
		src.addMessageProperties(dirPath);
		
		File propFile;
		Locale locale;
		
		propFile = new File((new StringBuilder(dirPath)).append(File.separator).append("metadata_en.properties").toString());
		Map<File, Locale> msgPropMap = src.getMessagePropertiesMap();
		Assert.assertTrue(msgPropMap.containsKey(propFile));
		locale = msgPropMap.get(propFile);
		Assert.assertEquals(new Locale("en"), locale);
		
		propFile = new File((new StringBuilder(dirPath)).append(File.separator).append("metadata_fr.properties").toString());
		Assert.assertTrue(msgPropMap.containsKey(propFile));
		locale = msgPropMap.get(propFile);
		Assert.assertEquals(new Locale("fr"), locale);
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldInferLocaleOrFallback() {
		
		InitializerMessageSource src = new InitializerMessageSource();
		
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("basename_fr", Locale.ENGLISH));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("my_base_name_fr", Locale.ENGLISH));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr", Locale.ENGLISH));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr_", Locale.ENGLISH));
		Assert.assertEquals(new Locale("fr", "FR"), src.getLocaleFromFileBaseName("my_base_name_fr_FR", Locale.ENGLISH));
		Assert.assertEquals(Locale.ENGLISH, src.getLocaleFromFileBaseName("my_base_name", Locale.ENGLISH));
		Assert.assertEquals(Locale.ENGLISH, src.getLocaleFromFileBaseName("my_base_name_fr_XX", Locale.ENGLISH));
		Assert.assertEquals(new Locale("fr", "BE"), src.getLocaleFromFileBaseName("my_base_name_fr_BE", Locale.ENGLISH));
	}
}

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
import org.openmrs.test.Verifies;

public class InitializerMessageSourceTest {
	
	private String dirPath = "";
	
	@Before
	public void setup() {
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder
		        .append(
		            getClass().getClassLoader().getResource(DomainBaseModuleContextSensitiveTest.appDataTestDir).getPath())
		        .append(File.separator).append(InitializerConstants.DIR_NAME_CONFIG).append(File.separator)
		        .append(InitializerConstants.DOMAIN_ADDR);
		dirPath = pathBuilder.toString();
	}
	
	@Test
	@Verifies(value = "should save messages files as keys and create appropriate as values", method = "getMessageProperties(String dirPath)")
	public void getMessageProperties_shouldScanMessagesFiles() {
		
		Map<File, Locale> propertiesMap = InitializerMessageSource.getMessageProperties(dirPath);
		
		File propFile;
		Locale locale;
		
		propFile = new File((new StringBuilder(dirPath)).append(File.separator).append("addresshierarchy_en.properties")
		        .toString());
		Assert.assertTrue(propertiesMap.containsKey(propFile));
		locale = propertiesMap.get(propFile);
		Assert.assertEquals(new Locale("en"), locale);
		
		propFile = new File((new StringBuilder(dirPath)).append(File.separator).append("addresshierarchy_km_KH.properties")
		        .toString());
		Assert.assertTrue(propertiesMap.containsKey(propFile));
		locale = propertiesMap.get(propFile);
		Assert.assertEquals(new Locale("km", "KH"), locale);
	}
}

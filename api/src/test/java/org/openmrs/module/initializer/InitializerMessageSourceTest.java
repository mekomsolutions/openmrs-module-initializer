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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

public class InitializerMessageSourceTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void getLocaleFromFileBaseName_shouldInferValidLocale() {
		
		InitializerMessageSource src = new InitializerMessageSource();
		
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("basename_fr"));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("my_base_name_fr"));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr"));
		Assert.assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr_"));
		Assert.assertEquals(new Locale("fr", "FR"), src.getLocaleFromFileBaseName("my_base_name_fr_FR"));
		Assert.assertEquals(new Locale("fr", "BE"), src.getLocaleFromFileBaseName("my_base_name_fr_BE"));
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldThrowIfNoValidLocaleAsSuffixToFileBaseName() {
		
		InitializerMessageSource src = new InitializerMessageSource();
		
		try {
			src.getLocaleFromFileBaseName("my_base_name");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage()
			        .equals("No valid locale could be inferred from the following file base name: 'my_base_name'."));
		}
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldThrowIfNoSuffixInFileBaseName() {
		
		InitializerMessageSource src = new InitializerMessageSource();
		
		try {
			src.getLocaleFromFileBaseName("my-base-name");
		}
		catch (IllegalArgumentException e) {
			Assert.assertTrue(
			    e.getMessage().equals("'my-base-name' is not suffixed with the string representation of a locale."));
		}
	}
}

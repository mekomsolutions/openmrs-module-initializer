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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InitializerMessageSourceTest {
	
	InitializerMessageSource src = new InitializerMessageSource();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void getLocaleFromFileBaseName_shouldInferValidLocale() {
		assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("basename_fr"));
		assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("my_base_name_fr"));
		assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr"));
		assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("_my_base_name_fr_"));
		assertEquals(new Locale("fr", "FR"), src.getLocaleFromFileBaseName("my_base_name_fr_FR"));
		assertEquals(new Locale("fr", "BE"), src.getLocaleFromFileBaseName("my_base_name_fr_BE"));
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldReturnNullIfNoValidLocaleAsSuffixToFileBaseName() {
		assertNull(src.getLocaleFromFileBaseName("my_base_name"));
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldAssumeEnglishLocaleLanguageIfNoLocaleSuffixProvided() {
		assertEquals(Locale.ENGLISH, src.getLocaleFromFileBaseName("my-base-name"));
	}
	
	@Test
	public void getLocaleFromFileBaseName_shouldAllowUnderscoresInDirectory() {
		assertEquals(Locale.FRENCH, src.getLocaleFromFileBaseName("/tmp/test_dir/basename_fr"));
		Locale.setDefault(Locale.US);
		assertEquals(Locale.ENGLISH, src.getLocaleFromFileBaseName("/tmp/test_dir/basename"));
	}
}

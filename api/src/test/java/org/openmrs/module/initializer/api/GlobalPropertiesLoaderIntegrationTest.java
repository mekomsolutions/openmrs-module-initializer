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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.gp.GlobalPropertiesLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalPropertiesLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private GlobalPropertiesLoader loader;
	
	@Test
	public void loadGlobalProperties_shouldLoadGlobalProperties() {
		
		// Replay
		loader.load();
		
		// Verif
		Assertions.assertEquals("GP one one", Context.getAdministrationService().getGlobalProperty("gp.gp11"));
		Assertions.assertEquals("GP one two", Context.getAdministrationService().getGlobalProperty("gp.gp12"));
		Assertions.assertEquals("GP two one", Context.getAdministrationService().getGlobalProperty("gp.gp21"));
		Assertions.assertEquals("GP three one", Context.getAdministrationService().getGlobalProperty("gp.gp31"));
		Assertions.assertEquals("GP three two", Context.getAdministrationService().getGlobalProperty("gp.gp32"));
		Assertions.assertEquals("GP three three", Context.getAdministrationService().getGlobalProperty("gp.gp33"));
	}
	
	@Test
	public void load_shouldOverrideGlobalProperties() {
		
		// Setup
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("gp.gp11", "foobar"));
		Assertions.assertEquals("foobar", Context.getAdministrationService().getGlobalProperty("gp.gp11"));
		
		// Replay
		loader.load();
		
		// Verif
		Assertions.assertEquals("GP one one", Context.getAdministrationService().getGlobalProperty("gp.gp11"));
	}
	
	@Test
	public void load_shouldNotAffectOtherGlobalProperties() {
		
		// Setup
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("gp.foo", "Foo"));
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("gp.bar", "Bar"));
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("gp.baz", "Baz"));
		
		// Replay
		loader.load();
		
		// Verif
		Assertions.assertEquals("Foo", Context.getAdministrationService().getGlobalProperty("gp.foo"));
		Assertions.assertEquals("Bar", Context.getAdministrationService().getGlobalProperty("gp.bar"));
		Assertions.assertEquals("Baz", Context.getAdministrationService().getGlobalProperty("gp.baz"));
	}
}

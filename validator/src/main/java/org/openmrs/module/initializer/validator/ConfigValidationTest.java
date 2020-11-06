package org.openmrs.module.initializer.validator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

@SkipBaseSetup
public class ConfigValidationTest extends DomainBaseModuleContextSensitiveTest {
	
	public ConfigValidationTest() {
		super();
	}
	
	@Test
	public void should() {
		Assert.assertTrue(true);
	}
}

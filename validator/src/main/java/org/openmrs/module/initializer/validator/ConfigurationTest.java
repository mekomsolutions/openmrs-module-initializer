package org.openmrs.module.initializer.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.Loader;

public class ConfigurationTest extends DomainBaseModuleContextSensitiveTest {
	
	private String configDirPath;
	
	@Override
	protected String getAppDataDirPath() {
		return Paths.get(configDirPath).getParent().toString();
	}
	
	public ConfigurationTest() {
		super();
		
		assertThat("No arguments were provided to the configuration validator.", Validator.arguments,
		    not(emptyCollectionOf(String.class)));
		assertThat("At least the path to a configuration to be tested should be provided.", Validator.arguments.size(),
		    greaterThanOrEqualTo(1));
		
		configDirPath = Validator.arguments.get(0);
	}
	
	@Test
	public void loadConfiguration() {
		for (Loader loader : getService().getLoaders()) {
			loader.load();
		}
		
		Assert.assertTrue(true);
	}
}

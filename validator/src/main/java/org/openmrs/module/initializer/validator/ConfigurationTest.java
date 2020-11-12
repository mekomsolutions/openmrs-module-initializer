package org.openmrs.module.initializer.validator;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.nio.file.Paths;

import org.hsqldb.cmdline.SqlFile;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.test.TestUtil;

public class ConfigurationTest extends DomainBaseModuleContextSensitiveTest {
	
	private String configDirPath;
	
	private String sqlScriptPath;
	
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
		
		if (Validator.arguments.size() > 1) {
			sqlScriptPath = Validator.arguments.get(1);
		}
	}
	
	@Before
	public void prepare() throws Exception {
		if (!isEmpty(sqlScriptPath)) {
			SqlFile sqlFile = new SqlFile(new File(sqlScriptPath));
			sqlFile.setConnection(getConnection());
			sqlFile.execute();
			TestUtil.printOutTableContents(getConnection(), "concept");
		}
	}
	
	@Test
	public void loadConfiguration() {
		for (Loader loader : getService().getLoaders()) {
			loader.load();
		}
	}
}

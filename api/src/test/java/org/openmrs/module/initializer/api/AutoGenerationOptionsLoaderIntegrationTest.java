package org.openmrs.module.initializer.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.idgen.autogen.AutoGenerationOptionsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;

public class AutoGenerationOptionsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AutoGenerationOptionsLoader loader;
	
	@Autowired
	private IdentifierSourceService identifierSourceService;
	
	private static String OPTION_TO_EDIT_UUID = "eade77b6-3365-47ed-9ee3-2324598629eb";
	
	@Before
	public void setup() throws Exception {
		executeDataSet("testdata/test-metadata.xml");
	}
	
	@Test
	public void load_shouldLoadAutoGenerationOptionsFromCSVLines() {
		// Setup
		AutoGenerationOption option = null;
		
		// Replay
		loader.load();
		
		// Verify created
		option = identifierSourceService.getAutoGenerationOptionByUuid("ca5cc0bf-38f0-41a6-9759-dd2a6763155a");
		Assert.assertNotNull(option);
		Assert.assertTrue(option.isAutomaticGenerationEnabled());
		Assert.assertFalse(option.isManualEntryEnabled());
		
		//Verify edited
		option = identifierSourceService.getAutoGenerationOptionByUuid(OPTION_TO_EDIT_UUID);
		Assert.assertFalse(option.isAutomaticGenerationEnabled());
		Assert.assertTrue(option.isManualEntryEnabled());
		Assert.assertThat(option.getIdentifierType().getName(), is("Legacy ID"));
	}
}

package org.openmrs.module.initializer.api.form;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.AmpathFormsLoader;
import org.openmrs.module.initializer.api.loaders.AmpathFormsTranslationsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmpathFormsTranslationsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static final String RESOURCE_UUID = "c5bf3efe-3798-4052-8dcb-09aacfcbabdc";
	
	@Autowired
	private AmpathFormsTranslationsLoader ampathFormsTranslationsLoader;
	
	
	@Autowired
	private AmpathFormsLoader ampathFormsLoader;
	
	@Autowired
	private FormService formService;
	
	@Test
	public void load_shouldLoadAFormTranslationsFileWithAllAttributesSpecifiedAsFormResource() throws Exception {
		// Setup
		ampathFormsLoader.load();
		
		// Replay
		ampathFormsTranslationsLoader.load();
		
		FormResource formResource = formService.getFormResourceByUuid(RESOURCE_UUID);
		
		// Verify clob
		Assert.assertNotNull(formResource);
		Assert.assertEquals("c5bf3efe-3798-4052-8dcb-09aacfcbabdc", formResource.getUuid());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree((String)formResource.getValue());
		Assert.assertEquals("\"c5bf3efe-3798-4052-8dcb-09aacfcbabdc\"", actualObj.get("uuid").toString());
		Assert.assertEquals("\"French Translations\"", actualObj.get("description").toString());
		Assert.assertEquals("\"fr\"", actualObj.get("language").toString());
		Assert.assertEquals("\"Encontre\"", actualObj.get("translations").get("Encounter").toString());
		Assert.assertEquals("\"Autre\"", actualObj.get("translations").get("Other").toString());
		Assert.assertEquals("\"Enfant\"", actualObj.get("translations").get("Child").toString());
		
	}
	
	@Test
	public void load_shouldLoadAndUpdateAFormTranslationsFileAsFormResource() throws Exception {
		// Setup
		ampathFormsLoader.load();
		
		// Test that initial version loads in with expected values
		// Replay
		ampathFormsTranslationsLoader.load();
		
		FormResource formResource = formService.getFormResourceByUuid(RESOURCE_UUID);
		
		// Verify clob
		Assert.assertNotNull(formResource);
		Assert.assertEquals("c5bf3efe-3798-4052-8dcb-09aacfcbabdc", formResource.getUuid());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode ampathTranslations = mapper.readTree((String)formResource.getValue());
		Assert.assertEquals("\"c5bf3efe-3798-4052-8dcb-09aacfcbabdc\"", ampathTranslations.get("uuid").toString());
		Assert.assertEquals("\"French Translations\"", ampathTranslations.get("description").toString());
		Assert.assertEquals("\"fr\"", ampathTranslations.get("language").toString());
		Assert.assertEquals("\"Encontre\"", ampathTranslations.get("translations").get("Encounter").toString());
		Assert.assertEquals("\"Autre\"", ampathTranslations.get("translations").get("Other").toString());
		Assert.assertEquals("\"Enfant\"", ampathTranslations.get("translations").get("Child").toString());
		
		String test_file_updated = "src/test/resources/testdata/testAmpathformstranslations/test_form_updated_translations_fr.json";
		File srcFile = new File(test_file_updated);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		ampathFormsTranslationsLoader.load();
		FormResource formResourceUpdated = formService.getFormResourceByUuid(RESOURCE_UUID);
		
		// Verify clob changed
		Assert.assertNotNull(formResourceUpdated);
		ObjectMapper mapperUpdated = new ObjectMapper();
		JsonNode ampathTranslationsUpdated = mapperUpdated.readTree((String)formResourceUpdated.getValue());
		Assert.assertEquals("\"c5bf3efe-3798-4052-8dcb-09aacfcbabdc\"", ampathTranslationsUpdated.get("uuid").toString());
		Assert.assertEquals("\"French Translations Updated\"", ampathTranslationsUpdated.get("description").toString());
		Assert.assertEquals("\"fr\"", ampathTranslationsUpdated.get("language").toString());
		Assert.assertEquals("\"Tante\"", ampathTranslationsUpdated.get("translations").get("Aunt").toString());
		Assert.assertEquals("\"Oncle\"", ampathTranslationsUpdated.get("translations").get("Uncle").toString());
		Assert.assertEquals("\"Neveu\"", ampathTranslationsUpdated.get("translations").get("Nephew").toString());
	}
	
}

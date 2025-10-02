package org.openmrs.module.initializer.api.form;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runners.MethodSorters;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.AmpathFormsLoader;
import org.openmrs.module.initializer.api.loaders.AmpathFormsTranslationsLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AmpathFormsTranslationsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final String FORM_TRANSLATIONS_FOLDER_PATH = "src/test/resources/ampathformstranslations/";
	
	@Autowired
	private AmpathFormsTranslationsLoader ampathFormsTranslationsLoader;
	
	@Autowired
	private AmpathFormsLoader ampathFormsLoader;
	
	@Autowired
	private FormService formService;
	
	@After
	public void clean() throws IOException {
		
		// Delete created form files
		FileUtils.deleteDirectory(new File(FORM_TRANSLATIONS_FOLDER_PATH));
		FileUtils.deleteQuietly(new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_ampath_translations_updated.json"));
	}
	
	@Test
	public void load_shouldLoadAFormTranslationsFileWithAllAttributesSpecifiedAsFormResource() throws Exception {
		// Setup
		ampathFormsLoader.load();
		
		// Replay
		ampathFormsTranslationsLoader.load();
		
		// Verify
		Form form = formService.getForm("Test Form 1");
		FormResource formResource = formService.getFormResource(form, "Test Form 1_translations_fr");
		Assert.assertNotNull(formResource);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree((String) formResource.getValue());
		Assert.assertEquals("French Translations", actualObj.get("description").getTextValue());
		Assert.assertEquals("fr", actualObj.get("language").getTextValue());
		Assert.assertEquals("Encontre", actualObj.get("translations").get("Encounter").getTextValue());
		Assert.assertEquals("Autre", actualObj.get("translations").get("Other").getTextValue());
		Assert.assertEquals("Enfant", actualObj.get("translations").get("Child").getTextValue());
		
		// verify form name translation
		Assert.assertEquals("Formulaire d'essai 1", Context.getMessageSourceService()
		        .getMessage("ui.i18n.Form.name." + formResource.getForm().getUuid(), null, Locale.CANADA_FRENCH));
		Assert.assertEquals("Formulaire d'essai 1", Context.getMessageSourceService()
		        .getMessage("org.openmrs.Form." + formResource.getForm().getUuid(), null, Locale.CANADA_FRENCH));
		
	}
	
	@Test
	public void load_shouldLoadAndUpdateAFormTranslationsFileAsFormResource() throws Exception {
		// Setup
		ampathFormsLoader.load();
		
		// Replay
		// Test that initial version loads in with expected values
		ampathFormsTranslationsLoader.load();
		
		// Verify
		Form form = formService.getForm("Test Form 1");
		FormResource formResource = formService.getFormResource(form, "Test Form 1_translations_fr");
		
		Assert.assertNotNull(formResource);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode ampathTranslations = mapper.readTree((String) formResource.getValue());
		Assert.assertEquals("French Translations", ampathTranslations.get("description").getTextValue());
		Assert.assertEquals("fr", ampathTranslations.get("language").getTextValue());
		Assert.assertEquals("Encontre", ampathTranslations.get("translations").get("Encounter").getTextValue());
		Assert.assertEquals("Autre", ampathTranslations.get("translations").get("Other").getTextValue());
		Assert.assertEquals("Enfant", ampathTranslations.get("translations").get("Child").getTextValue());
		
		String test_file_updated = "src/test/resources/testdata/testAmpathformstranslations/test_form_updated_translations_fr.json";
		File srcFile = new File(test_file_updated);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		// Now load updated values
		ampathFormsTranslationsLoader.load();
		
		Form formUpdated = formService.getForm("Test Form 1");
		FormResource formResourceUpdated = formService.getFormResource(formUpdated, "Test Form 1_translations_fr");
		
		// Verify
		Assert.assertNotNull(formResourceUpdated);
		ObjectMapper mapperUpdated = new ObjectMapper();
		JsonNode ampathTranslationsUpdated = mapperUpdated.readTree((String) formResourceUpdated.getValue());
		Assert.assertEquals("French Translations Updated", ampathTranslationsUpdated.get("description").getTextValue());
		Assert.assertEquals("fr", ampathTranslationsUpdated.get("language").getTextValue());
		Assert.assertEquals("Tante", ampathTranslationsUpdated.get("translations").get("Aunt").getTextValue());
		Assert.assertEquals("Oncle", ampathTranslationsUpdated.get("translations").get("Uncle").getTextValue());
		Assert.assertEquals("Neveu", ampathTranslationsUpdated.get("translations").get("Nephew").getTextValue());
	}
	
	@Test
	public void load_shouldThrowGivenInvalidFormAssociatedWithFormTranslations() throws Exception {
		// Setup
		thrown.expectMessage(
		    "IllegalArgumentException: Could not find a form named 'Test Form 1'. Please ensure an existing form is configured.");
		
		// Replay
		ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true);
		
	}
	
	@Test
	public void load_shouldThrowGivenMissingFormFieldInFormTranslationsDef() throws Exception {
		// Setup
		thrown.expectMessage("IllegalArgumentException: 'form' property is required for AMPATH forms translations loader.");
		
		String missingUuidTranslationDefFile = "src/test/resources/testdata/testAmpathformstranslations/invalid_form_missing_formName_translations_fr.json";
		File srcFile = new File(missingUuidTranslationDefFile);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true);
		
	}
	
	@Test
	public void load_shouldThrowGivenMissingLanguageFieldInFormTranslationsDef() throws Exception {
		// Setup
		thrown.expectMessage(
		    "IllegalArgumentException: 'language' property is required for AMPATH forms translations loader.");
		
		String missingUuidTranslationDefFile = "src/test/resources/testdata/testAmpathformstranslations/invalid_form_missing_language_translations_fr.json";
		File srcFile = new File(missingUuidTranslationDefFile);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		ampathFormsLoader.load();
		ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true);
		
	}
}

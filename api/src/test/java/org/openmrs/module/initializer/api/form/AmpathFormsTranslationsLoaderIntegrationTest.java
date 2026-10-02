package org.openmrs.module.initializer.api.form;

import org.apache.commons.io.FileUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AmpathFormsTranslationsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private static final String FORM_TRANSLATIONS_FOLDER_PATH = "src/test/resources/ampathformstranslations/";
	
	@Autowired
	private AmpathFormsTranslationsLoader ampathFormsTranslationsLoader;
	
	@Autowired
	private AmpathFormsLoader ampathFormsLoader;
	
	@Autowired
	private FormService formService;
	
	@AfterEach
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
		Assertions.assertNotNull(formResource);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree((String) formResource.getValue());
		Assertions.assertEquals("French Translations", actualObj.get("description").textValue());
		Assertions.assertEquals("fr", actualObj.get("language").textValue());
		Assertions.assertEquals("Encontre", actualObj.get("translations").get("Encounter").textValue());
		Assertions.assertEquals("Autre", actualObj.get("translations").get("Other").textValue());
		Assertions.assertEquals("Enfant", actualObj.get("translations").get("Child").textValue());
		
		// verify form name translation
		Assertions.assertEquals("Formulaire d'essai 1", Context.getMessageSourceService()
		        .getMessage("ui.i18n.Form.name." + formResource.getForm().getUuid(), null, Locale.CANADA_FRENCH));
		Assertions.assertEquals("Formulaire d'essai 1", Context.getMessageSourceService()
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
		
		Assertions.assertNotNull(formResource);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode ampathTranslations = mapper.readTree((String) formResource.getValue());
		Assertions.assertEquals("French Translations", ampathTranslations.get("description").textValue());
		Assertions.assertEquals("fr", ampathTranslations.get("language").textValue());
		Assertions.assertEquals("Encontre", ampathTranslations.get("translations").get("Encounter").textValue());
		Assertions.assertEquals("Autre", ampathTranslations.get("translations").get("Other").textValue());
		Assertions.assertEquals("Enfant", ampathTranslations.get("translations").get("Child").textValue());
		
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
		Assertions.assertNotNull(formResourceUpdated);
		ObjectMapper mapperUpdated = new ObjectMapper();
		JsonNode ampathTranslationsUpdated = mapperUpdated.readTree((String) formResourceUpdated.getValue());
		Assertions.assertEquals("French Translations Updated", ampathTranslationsUpdated.get("description").textValue());
		Assertions.assertEquals("fr", ampathTranslationsUpdated.get("language").textValue());
		Assertions.assertEquals("Tante", ampathTranslationsUpdated.get("translations").get("Aunt").textValue());
		Assertions.assertEquals("Oncle", ampathTranslationsUpdated.get("translations").get("Uncle").textValue());
		Assertions.assertEquals("Neveu", ampathTranslationsUpdated.get("translations").get("Nephew").textValue());
	}
	
	@Test
	public void load_shouldThrowGivenInvalidFormAssociatedWithFormTranslations() throws Exception {
		// Replay
		Exception e = Assertions.assertThrows(Exception.class,
		    () -> ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true));
		MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(
		    "IllegalArgumentException: Could not find a form named 'Test Form 1'. Please ensure an existing form is configured."));
		
	}
	
	@Test
	public void load_shouldThrowGivenMissingFormFieldInFormTranslationsDef() throws Exception {
		// Setup
		String missingUuidTranslationDefFile = "src/test/resources/testdata/testAmpathformstranslations/invalid_form_missing_formName_translations_fr.json";
		File srcFile = new File(missingUuidTranslationDefFile);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		Exception e = Assertions.assertThrows(Exception.class,
		    () -> ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true));
		MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(
		    "IllegalArgumentException: 'form' property is required for AMPATH forms translations loader."));
		
	}
	
	@Test
	public void load_shouldThrowGivenMissingLanguageFieldInFormTranslationsDef() throws Exception {
		// Setup
		String missingUuidTranslationDefFile = "src/test/resources/testdata/testAmpathformstranslations/invalid_form_missing_language_translations_fr.json";
		File srcFile = new File(missingUuidTranslationDefFile);
		File dstFile = new File(
		        ampathFormsTranslationsLoader.getDirUtil().getDomainDirPath() + "/test_form_translations_fr.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		ampathFormsLoader.load();
		Exception e = Assertions.assertThrows(Exception.class,
		    () -> ampathFormsTranslationsLoader.loadUnsafe(Collections.emptyList(), true));
		MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString(
		    "IllegalArgumentException: 'language' property is required for AMPATH forms translations loader."));
		
	}
}

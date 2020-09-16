package org.openmrs.module.initializer.api.form;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class BahmniFormsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	private String formFolderPath = "src/test/resources/forms/";
	
	private String formTranslationPath = "src/test/resources/forms/translations/";
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	@Autowired
	private BahmniFormTranslationService bahmniFormTranslationService;
	
	@Autowired
	private BahmniFormsLoader bahmniFormsLoader;
	
	@Autowired
	private FormService formService;
	
	@Before
	public void setup() {
		
		// Set default directory for saving Bahmni form and Bahmni form translation files
		administrationService.saveGlobalProperty(new GlobalProperty("bahmni.forms.directory", formFolderPath));
		administrationService
		        .saveGlobalProperty(new GlobalProperty("bahmni.formTranslations.directory", formTranslationPath));
	}
	
	@After
	public void clean() throws IOException {
		
		// Delete created form files
		FileUtils.deleteDirectory(new File(formFolderPath));
		FileUtils.deleteDirectory(new File(formTranslationPath));
		FileUtils.deleteQuietly(new File(bahmniFormsLoader.getDirUtil().getDomainDirPath() + "/test_form1_updated.json"));
	}
	
	@Test
	public void load_shouldLoadFormAccordingToJsonFile() {
		
		// Replay
		bahmniFormsLoader.load();
		Form form = formService.getForm("form1");
		
		// Verify
		Assert.assertEquals("form1", form.getName());
		Assert.assertEquals(true, form.getPublished());
		Assert.assertEquals("1", form.getVersion());
	}
	
	@Test
	public void load_shouldLoadAndUpdateForm() throws Exception {
		
		// Setup
		bahmniFormsLoader.load();
		Form form = formService.getForm("form1");
		
		Assert.assertEquals(true, form.getPublished());
		
		String test_file_updated = "src/test/resources/testdata/testBahmniforms/test_form1_updated.json";
		File srcFile = new File(test_file_updated);
		File dstFile = new File(bahmniFormsLoader.getDirUtil().getDomainDirPath() + "/test_form1_updated.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		bahmniFormsLoader.load();
		Form updatedForm = formService.getForm("form1");
		List<FormTranslation> bahmniFormTranslation = bahmniFormTranslationService.getFormTranslations(form.getName(),
		    form.getVersion(), null, form.getUuid());
		
		// Verify
		
		Assert.assertEquals(false, updatedForm.getPublished());
		Map<String, String> labels = bahmniFormTranslation.get(0).getLabels();
		Assert.assertEquals("updated label", labels.get("LABEL_2"));
	}
	
	@Test
	public void load_shouldNotCreateDuplicates() {
		
		// Setup
		bahmniFormsLoader.load();
		
		// Replay
		bahmniFormsLoader.load();
		
		//Verify
		Assert.assertNull(formService.getForm("form1", "2"));
		Assert.assertEquals(1,
		    formService.getAllForms().stream().filter(form -> form.getName().equals("form1")).toArray().length);
	}
	
	@Test
	public void load_shouldSaveFormTranslation() {
		
		// Replay
		bahmniFormsLoader.load();
		Form form = formService.getForm("form1");
		
		List<FormTranslation> bahmniFormTranslation = bahmniFormTranslationService.getFormTranslations(form.getName(), "1",
		    null, form.getUuid());
		
		// Verify
		Assert.assertEquals(bahmniFormTranslation.size(), 1);
		Assert.assertEquals("en", bahmniFormTranslation.get(0).getLocale());
		Map<String, String> labels = bahmniFormTranslation.get(0).getLabels();
		Assert.assertEquals("just a label", labels.get("LABEL_2"));
		Map<String, String> concepts = bahmniFormTranslation.get(0).getConcepts();
		Assert.assertEquals("test_upload", concepts.get("TEST_UPLOAD_3"));
		Assert.assertEquals("form1", bahmniFormTranslation.get(0).getFormName());
	}
	
	@Test
	public void load_shouldCreateFormJsonFile() throws IOException {
		
		// Replay
		bahmniFormsLoader.load();
		
		// Verify
		Form form = formService.getForm("form1");
		File testFile = new File(formFolderPath + form.getUuid() + ".json");
		
		Assert.assertTrue(testFile.exists());
		
		String jsonString = FileUtils.readFileToString(testFile);
		JSONObject jsonObject = new JSONObject(jsonString);
		
		Assert.assertEquals("form1", jsonObject.get("name"));
	}
	
	@Test
	public void load_shouldCreateFormTranslationJsonFile() throws IOException {
		
		// Replay
		bahmniFormsLoader.load();
		
		// Verify
		Form form = formService.getForm("form1");
		File testFile = new File(formTranslationPath + form.getUuid() + ".json");
		
		Assert.assertTrue(testFile.exists());
		
		String jsonString = FileUtils.readFileToString(testFile);
		JSONObject fileContent = new JSONObject(jsonString);
		
		JSONObject localeContent = (JSONObject) fileContent.get("en");
		Assert.assertEquals(localeContent.get("concepts").toString(), "{\"TEST_UPLOAD_3\":\"test_upload\"}");
		Assert.assertEquals(localeContent.get("labels").toString(), "{\"LABEL_2\":\"just a label\"}");
	}
}

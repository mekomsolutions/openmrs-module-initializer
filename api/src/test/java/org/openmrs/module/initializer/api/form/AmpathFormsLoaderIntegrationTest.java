package org.openmrs.module.initializer.api.form;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.FormService;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.AmpathFormsLoader;
import org.springframework.beans.factory.annotation.Autowired;

public class AmpathFormsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private AmpathFormsLoader ampathFormsLoader;
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private FormService formService;
	
	private String formFolderPath = "src/test/resources/forms/";
	
	@After
	public void clean() throws IOException {
		
		// Delete created form files
		FileUtils.deleteDirectory(new File(formFolderPath));
		FileUtils
		        .deleteQuietly(new File(ampathFormsLoader.getDirUtil().getDomainDirPath() + "/test_form_clob_changed.json"));
		FileUtils.deleteQuietly(new File(ampathFormsLoader.getDirUtil().getDomainDirPath() + "/test_form_new_version.json"));
	}
	
	@Test
	public void load_shouldLoadFormWithAllAttributesSpecified() throws Exception {
		
		// Replay
		ampathFormsLoader.load();
		Form form = formService.getForm("Test Form 1");
		FormResource formResource = formService.getFormResource(form, "JSON schema");
		ClobDatatypeStorage clob = datatypeService
		        .getClobDatatypeStorageByUuid(formService.getFormResource(form, "JSON schema").getValueReference());
		// Verify form
		Assert.assertEquals("Test Form 1", form.getName());
		Assert.assertEquals(Boolean.TRUE, form.getPublished());
		Assert.assertEquals("1", form.getVersion());
		Assert.assertEquals(Boolean.FALSE, form.getRetired());
		Assert.assertEquals("Emergency", form.getEncounterType().getName());
		Assert.assertEquals("Test 1 Description", form.getDescription());
		// Verify clob
		Assert.assertNotNull(clob);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(clob.getValue());
		Assert.assertEquals("\"Page 1\"", actualObj.get("pages").getElements().next().get("label").toString());
		// Verify Form Resource 
		Assert.assertNotNull(formResource);
		Assert.assertEquals(clob.getUuid(), formResource.getValueReference());
		
	}
	
	@Test
	public void load_shouldLoadAndUpdateClobButNotForm() throws Exception {
		
		// Test that initial version loads in with expected values
		// Replay
		ampathFormsLoader.load();
		Form form = formService.getForm("Test Form 1");
		FormResource formResource = formService.getFormResource(form, "JSON schema");
		ClobDatatypeStorage clob = datatypeService
		        .getClobDatatypeStorageByUuid(formService.getFormResource(form, "JSON schema").getValueReference());
		// Verify form
		Assert.assertEquals("Test Form 1", form.getName());
		Assert.assertEquals(Boolean.TRUE, form.getPublished());
		Assert.assertEquals("1", form.getVersion());
		Assert.assertEquals(Boolean.FALSE, form.getRetired());
		Assert.assertEquals("Emergency", form.getEncounterType().getName());
		Assert.assertEquals("Test 1 Description", form.getDescription());
		// Verify clob
		Assert.assertNotNull(clob);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(clob.getValue());
		Assert.assertEquals("\"Page 1\"", actualObj.get("pages").getElements().next().get("label").toString());
		// Verify Form Resource
		Assert.assertNotNull(formResource);
		Assert.assertEquals(clob.getUuid(), formResource.getValueReference());
		
		String test_file_updated = "src/test/resources/testdata/testAmpathforms/test_form_clob_changed.json";
		File srcFile = new File(test_file_updated);
		File dstFile = new File(ampathFormsLoader.getDirUtil().getDomainDirPath() + "/test_form_clob_changed.json");
		
		FileUtils.copyFile(srcFile, dstFile);
		
		// Replay
		ampathFormsLoader.load();
		form = formService.getForm("Test Form 1");
		
		// Verify form unchanged
		Assert.assertEquals("Test Form 1", form.getName());
		Assert.assertEquals(Boolean.TRUE, form.getPublished());
		Assert.assertEquals("1", form.getVersion());
		Assert.assertEquals(Boolean.FALSE, form.getRetired());
		Assert.assertEquals("Emergency", form.getEncounterType().getName());
		Assert.assertEquals("Test 1 Description", form.getDescription());
		// Verify clob changed
		Assert.assertNotNull(clob);
		ObjectMapper mapper2 = new ObjectMapper();
		JsonNode actualObj1 = mapper2.readTree(clob.getValue());
		Assert.assertEquals("\"Page 1 changed\"", actualObj1.get("pages").getElements().next().get("label").toString());
	}
	
	@Test
	public void load_shouldRetireAndCreateNewForm() throws Exception {
		
		// Test that initial version loads in with expected values
		// Replay
		ampathFormsLoader.load();
		Form form = formService.getForm("Test Form 1");
		FormResource formResource = formService.getFormResource(form, "JSON schema");
		ClobDatatypeStorage clob = datatypeService
		        .getClobDatatypeStorageByUuid(formService.getFormResource(form, "JSON schema").getValueReference());
		// Verify Form
		Assert.assertEquals("Test Form 1", form.getName());
		Assert.assertEquals(Boolean.TRUE, form.getPublished());
		Assert.assertEquals("1", form.getVersion());
		Assert.assertEquals(Boolean.FALSE, form.getRetired());
		Assert.assertEquals("Emergency", form.getEncounterType().getName());
		Assert.assertEquals("Test 1 Description", form.getDescription());
		// Verify Clob
		Assert.assertNotNull(clob);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(clob.getValue());
		Assert.assertEquals("\"Page 1\"", actualObj.get("pages").getElements().next().get("label").toString());
		// Verify Form Resource 
		Assert.assertNotNull(formResource);
		Assert.assertEquals(clob.getUuid(), formResource.getValueReference());
		
		String test_file_updated = "src/test/resources/testdata/testAmpathforms/test_form_new_version.json";
		File srcFile = new File(test_file_updated);
		File dstFile = new File(ampathFormsLoader.getDirUtil().getDomainDirPath() + "/test_form_new_version.json");
		FileUtils.copyFile(srcFile, dstFile);
		
		// Test that new version loads in with expected values
		// Replay
		ampathFormsLoader.load();
		Form form2 = formService.getForm("Test Form 1");
		Form initialForm = formService.getFormByUuid(form.getUuid());
		Assert.assertEquals(Boolean.TRUE, initialForm.getRetired());
		
		// Verify Form Changed
		Assert.assertEquals("Test Form 1", form2.getName());
		Assert.assertEquals(Boolean.TRUE, form2.getPublished());
		Assert.assertEquals("2", form2.getVersion());
		Assert.assertEquals(Boolean.FALSE, form2.getRetired());
		Assert.assertEquals("Emergency", form2.getEncounterType().getName());
		Assert.assertEquals("Test 1 Description Updated", form2.getDescription());
		
		// Verify Clob Changed
		ClobDatatypeStorage clob2 = datatypeService
		        .getClobDatatypeStorageByUuid(formService.getFormResource(form2, "JSON schema").getValueReference());
		Assert.assertNotNull(clob2);
		assertNotEquals(clob, clob2);
		ObjectMapper mapper2 = new ObjectMapper();
		JsonNode actualObj1 = mapper2.readTree(clob2.getValue());
		Assert.assertEquals("\"Page X\"", actualObj1.get("pages").getElements().next().get("label").toString());
		
		// Verify Form Resource 
		FormResource formResource2 = formService.getFormResource(form2, "JSON schema");
		Assert.assertNotNull(formResource2);
		Assert.assertEquals(clob2.getUuid(), formResource2.getValueReference());
		
		List<Form> forms = formService.getAllForms(true);
		// There is an initial Basic form
		Assert.assertEquals(3, forms.size());
	}
	
}

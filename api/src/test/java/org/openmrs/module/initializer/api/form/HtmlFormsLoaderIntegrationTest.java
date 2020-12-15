package org.openmrs.module.initializer.api.form;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.htmlformentry.schema.HtmlFormSchema;
import org.openmrs.module.initializer.DomainBaseModuleContextSensitiveTest;
import org.openmrs.module.initializer.api.loaders.HtmlFormsLoader;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class HtmlFormsLoaderIntegrationTest extends DomainBaseModuleContextSensitiveTest {
	
	@Autowired
	private HtmlFormsLoader htmlFormsLoader;
	
	@Autowired
	private HtmlFormEntryService htmlFormEntryService;
	
	@Test
	public void load_shouldLoadFormWithAllAttributesSpecified() throws Exception {
		htmlFormsLoader.load();
		HtmlForm f = htmlFormEntryService.getHtmlFormByUuid("26ddfe02-28f3-11eb-bc37-0242ac110002");
		Assert.assertNotNull(f);
		Assert.assertEquals("203fa4f8-28f3-11eb-bc37-0242ac110002", f.getForm().getUuid());
		Assert.assertEquals("Test Form 1", f.getName());
		Assert.assertEquals("Test Form With All Attributes", f.getDescription());
		Assert.assertEquals("1.3", f.getForm().getVersion());
		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", f.getForm().getEncounterType().getUuid());
		HtmlFormSchema schema = HtmlFormEntryUtil.getHtmlFormSchema(f, FormEntryContext.Mode.VIEW);
		Assert.assertEquals(1, schema.getAllFields().size());
		String expectedFormXml = getHtmlFormResourceAsString("allAttributeForm.xml");
		Assert.assertEquals(expectedFormXml.trim(), f.getXmlData().trim());
	}
	
	@Test
	public void load_shouldLoadAndUpdateForm() throws Exception {
		
		// Test that initial version loads in with expected values
		htmlFormsLoader.load();
		HtmlForm f1 = htmlFormEntryService.getHtmlFormByUuid("26ddfe02-28f3-11eb-bc37-0242ac110002");
		Assert.assertNotNull(f1);
		Assert.assertEquals("Test Form 1", f1.getName());
		Assert.assertEquals("Test Form With All Attributes", f1.getDescription());
		Assert.assertEquals("1.3", f1.getForm().getVersion());
		
		File formFile = new File(htmlFormsLoader.getDirUtil().getDomainDirPath(), "allAttributeForm.xml");
		String originalXml = f1.getXmlData();
		
		try {
			// Modify the loaded form's xml, and save it back to the configuration directory
			Document doc = HtmlFormEntryUtil.stringToDocument(f1.getXmlData());
			updateHtmlFormAttribute(doc, HtmlFormsLoader.FORM_NAME_ATTRIBUTE, "Revised Form Name");
			updateHtmlFormAttribute(doc, HtmlFormsLoader.FORM_DESCRIPTION_ATTRIBUTE, "Revised Form Description");
			updateHtmlFormAttribute(doc, HtmlFormsLoader.FORM_VERSION_ATTRIBUTE, "2.0");
			FileUtils.writeStringToFile(formFile, HtmlFormEntryUtil.documentToString(doc));
			
			// Now, reload configuration and test that the form has the new values
			htmlFormsLoader.load();
			HtmlForm f2 = htmlFormEntryService.getHtmlFormByUuid("26ddfe02-28f3-11eb-bc37-0242ac110002");
			Assert.assertNotNull(f2);
			Assert.assertEquals("Revised Form Name", f2.getName());
			Assert.assertEquals("Revised Form Description", f2.getDescription());
			Assert.assertEquals("2.0", f2.getForm().getVersion());
		}
		finally {
			// Clean up by putting the original file xml back
			FileUtils.writeStringToFile(formFile, originalXml);
		}
	}
	
	@Test
	public void load_shouldNotCreateDuplicates() {
		htmlFormsLoader.load();
		htmlFormsLoader.load();
		HtmlForm f1 = htmlFormEntryService.getHtmlFormByUuid("26ddfe02-28f3-11eb-bc37-0242ac110002");
		Assert.assertNotNull(f1);
		Assert.assertEquals(1, htmlFormEntryService.getAllHtmlForms().size());
	}
	
	protected void updateHtmlFormAttribute(Document doc, String attributeName, String attributeValue) throws Exception {
		Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, HtmlFormsLoader.HTML_FORM_TAG);
		NamedNodeMap atts = htmlFormNode.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node attribute = atts.item(i);
			if (attribute.getNodeName().equalsIgnoreCase(attributeName)) {
				attribute.setTextContent(attributeValue);
			}
		}
	}
	
	protected String getHtmlFormResourceAsString(String formPath) throws Exception {
		String resourcePath = "testAppDataDir/configuration/htmlforms/" + formPath;
		try (InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourcePath)) {
			return IOUtils.toString(is, "UTF-8");
		}
	}
}

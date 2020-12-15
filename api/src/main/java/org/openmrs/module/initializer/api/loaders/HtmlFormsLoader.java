package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.FormService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@OpenmrsProfile(modules = { "htmlformentry:*" })
public class HtmlFormsLoader extends BaseLoader {
	
	private final Log log = LogFactory.getLog(getClass());
	
	public static final String HTML_FORM_TAG = "htmlform";
	
	public static final String FORM_UUID_ATTRIBUTE = "formUuid";
	
	public static final String FORM_NAME_ATTRIBUTE = "formName";
	
	public static final String FORM_DESCRIPTION_ATTRIBUTE = "formDescription";
	
	public static final String FORM_VERSION_ATTRIBUTE = "formVersion";
	
	public static final String FORM_ENCOUNTER_TYPE_ATTRIBUTE = "formEncounterType";
	
	public static final String HTML_FORM_UUID_ATTRIBUTE = "htmlformUuid";
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private HtmlFormEntryService htmlFormEntryService;
	
	@Override
	protected Domain getDomain() {
		return Domain.HTML_FORMS;
	}
	
	@Override
	public void load(List<String> wildcardExclusions) {
		ConfigDirUtil dirUtil = getDirUtil();
		for (File file : dirUtil.getFiles("xml", wildcardExclusions)) { // processing all the XML files inside the domain
			
			String fileName = dirUtil.getFileName(file.getPath());
			String checksum = dirUtil.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			
			try {
				String xmlData = FileUtils.readFileToString(file, "UTF-8");
				Document doc = HtmlFormEntryUtil.stringToDocument(xmlData);
				Node htmlFormNode = HtmlFormEntryUtil.findChild(doc, HTML_FORM_TAG);
				
				String formUuid = getAttributeValue(htmlFormNode, FORM_UUID_ATTRIBUTE);
				if (formUuid == null) {
					throw new IllegalArgumentException(FORM_UUID_ATTRIBUTE + " is required");
				}
				Form form = formService.getFormByUuid(formUuid);
				boolean needToSaveForm = false;
				if (form == null) {
					form = new Form();
					form.setUuid(formUuid);
					needToSaveForm = true;
				}
				
				String formName = getAttributeValue(htmlFormNode, FORM_NAME_ATTRIBUTE);
				if (!OpenmrsUtil.nullSafeEquals(form.getName(), formName)) {
					form.setName(formName);
					needToSaveForm = true;
				}
				
				String formDescription = getAttributeValue(htmlFormNode, FORM_DESCRIPTION_ATTRIBUTE);
				if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
					form.setDescription(formDescription);
					needToSaveForm = true;
				}
				
				String formVersion = getAttributeValue(htmlFormNode, FORM_VERSION_ATTRIBUTE);
				if (!OpenmrsUtil.nullSafeEquals(form.getVersion(), formVersion)) {
					form.setVersion(formVersion);
					needToSaveForm = true;
				}
				
				String formEncounterType = getAttributeValue(htmlFormNode, FORM_ENCOUNTER_TYPE_ATTRIBUTE);
				EncounterType encounterType = null;
				if (formEncounterType != null) {
					encounterType = HtmlFormEntryUtil.getEncounterType(formEncounterType);
				}
				if (encounterType != null && !OpenmrsUtil.nullSafeEquals(form.getEncounterType(), encounterType)) {
					form.setEncounterType(encounterType);
					needToSaveForm = true;
				}
				
				if (needToSaveForm) {
					formService.saveForm(form);
				}
				
				HtmlForm htmlForm = htmlFormEntryService.getHtmlFormByForm(form);
				boolean needToSaveHtmlForm = false;
				if (htmlForm == null) {
					htmlForm = new HtmlForm();
					htmlForm.setForm(form);
					needToSaveHtmlForm = true;
				}
				
				// if there is a html form uuid specified, make sure the htmlform uuid is set to that value
				String htmlformUuid = getAttributeValue(htmlFormNode, HTML_FORM_UUID_ATTRIBUTE);
				if (StringUtils.isNotBlank(htmlformUuid) && !OpenmrsUtil.nullSafeEquals(htmlformUuid, htmlForm.getUuid())) {
					htmlForm.setUuid(htmlformUuid);
					needToSaveHtmlForm = true;
				}
				
				if (!StringUtils.trimToEmpty(htmlForm.getXmlData()).equals(StringUtils.trimToEmpty(xmlData))) {
					// trim because if the file ends with a newline the db will have trimmed it
					htmlForm.setXmlData(xmlData);
					needToSaveHtmlForm = true;
				}
				if (needToSaveHtmlForm) {
					htmlFormEntryService.saveHtmlForm(htmlForm);
				}
				
				dirUtil.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The form file has been processed: " + fileName);
				
			}
			catch (Exception e) {
				log.error("Could not load the htmlform from: " + file.getPath(), e);
			}
		}
	}
	
	private static String getAttributeValue(Node htmlForm, String attributeName) {
		Node item = htmlForm.getAttributes().getNamedItem(attributeName);
		return item == null ? null : item.getNodeValue();
	}
}

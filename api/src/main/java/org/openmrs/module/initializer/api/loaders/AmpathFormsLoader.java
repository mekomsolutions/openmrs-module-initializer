package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AmpathFormsLoader extends BaseFileLoader {
	
	public static final String AMPATH_FORMS_UUID = "794c4598-ab82-47ca-8d18-483a8abe6f4f";
	
	public static final String JSON_EXTENSION = "json";
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private FormService formService;
	
	@Override
	protected Domain getDomain() {
		return Domain.AMPATH_FORMS;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	protected void load(File file) throws Exception {
		String jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
		Map<String, Object> jsonFile = new ObjectMapper().readValue(jsonString, Map.class);
		
		String formName = (String) jsonFile.get("name");
		if (StringUtils.isBlank(formName)) {
			throw new Exception("Form Name is required");
		}
		
		String formDescription = (String) jsonFile.get("description");
		String formUuid = (String) jsonFile.get("uuid");
		boolean formPublished = (Boolean) jsonFile.get("published");
		boolean formRetired = (Boolean) jsonFile.get("retired");
		
		String formProcessor = (String) jsonFile.get("processor");
		boolean isEncounterForm = formProcessor == null || StringUtils.isBlank(formProcessor)
		        || formProcessor.equalsIgnoreCase("EncounterFormProcessor");
		
		EncounterType encounterType = null;
		String formEncounterType = (String) jsonFile.get("encounter");
		if (formEncounterType != null) {
			encounterType = encounterService.getEncounterType(formEncounterType);
			if (encounterType == null) {
				throw new Exception("Form Encounter type " + formEncounterType + " could not be found. Please ensure that "
				        + "this encountertype is either loaded by Iniz or loaded in the system before Iniz runs.");
			}
		}
		
		if (isEncounterForm && encounterType == null) {
			throw new Exception("No encounter was found for this form. You should have an \"encounter\" entry whose value "
			        + "is the id of the encounter type to use for this form, e.g., \"encounter\": \"Emergency\".");
		}
		
		String formVersion = (String) jsonFile.get("version");
		if (formVersion == null) {
			throw new Exception("Form Version is required");
		}
		
		// Delete Checksum Files for the translation files associated with the form
		ConfigDirUtil configDirUtil = new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(),
		        Domain.AMPATH_FORMS_TRANSLATIONS.getName(), true);
		
		for (File translationFile : configDirUtil.getFiles(JSON_EXTENSION)) {
			String js = FileUtils.readFileToString(translationFile, StandardCharsets.UTF_8.toString());
			Map<String, Object> jf = new ObjectMapper().readValue(js, Map.class);
			String translationForm = (String) jf.get("form");
			
			if (StringUtils.equals(translationForm, formName)) {
				configDirUtil
				        .deleteChecksumFile(replaceExtension(translationFile.getName(), ConfigDirUtil.CHECKSUM_FILE_EXT));
			}
		}
		
		String uuid = Utils.generateUuidFromObjects(AMPATH_FORMS_UUID, formName, formVersion);
		// Process Form
		// ISSUE-150 If form with uuid present then update it
		if (formService.getFormByUuid(formUuid) != null) {
			Form form = formService.getFormByUuid(formUuid);
			
			if (OpenmrsUtil.nullSafeEquals(form.getUuid(), formUuid)) {
				
				// Form resource name for forms resulting from HtmlFormEntry is hffeXMPath (HtmlFormEntry)
				FormResource formRes = formService.getFormResource(form, "JSON schema");
				if (formRes != null) {
					
					// Retrieve the form resource and update with the JSON schema and create clob data.	
					ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(formService.getFormResource(
					    form, "JSON schema").getValueReference());
					if (clobData != null) {
						clobData.setValue(jsonString);
						datatypeService.saveClobDatatypeStorage(clobData);
						
					}
					
				} else {
					String clobUuid = UUID.randomUUID().toString();
					FormResource formResource;
					formResource = new FormResource();
					formResource.setName("JSON schema");
					formResource.setForm(form);
					formResource.setValueReferenceInternal(clobUuid);
					formResource.setDatatypeClassname("AmpathJsonSchema");
					formService.saveFormResource(formResource);
					
					ClobDatatypeStorage newClobData = new ClobDatatypeStorage();
					newClobData.setUuid(clobUuid);
					newClobData.setValue(jsonString);
					datatypeService.saveClobDatatypeStorage(newClobData);
					
				}
				
				boolean needToSaveForm = false;
				// Description
				if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
					form.setDescription(formDescription);
					needToSaveForm = true;
				}
				
				// uuid
				if (!OpenmrsUtil.nullSafeEquals(form.getUuid(), formUuid)) {
					form.setUuid(formUuid);
					needToSaveForm = true;
				}
				
				// name
				if (!OpenmrsUtil.nullSafeEquals(form.getName(), formName)) {
					form.setName(formName);
					needToSaveForm = true;
				}
				// Version
				if (!OpenmrsUtil.nullSafeEquals(form.getVersion(), formVersion)) {
					form.setVersion(formVersion);
					needToSaveForm = true;
				}
				// Add in schema
				// Published
				if (!OpenmrsUtil.nullSafeEquals(form.getPublished(), formPublished)) {
					form.setPublished(formPublished);
					needToSaveForm = true;
				}
				// Add to schema
				// Retired
				if (!OpenmrsUtil.nullSafeEquals(form.getRetired(), formRetired)) {
					form.setRetired(formRetired);
					if (formRetired && StringUtils.isBlank(form.getRetireReason())) {
						form.setRetireReason("Retired by Initializer");
					}
					needToSaveForm = true;
				}
				// Add encounter to schema
				if (encounterType != null && !OpenmrsUtil.nullSafeEquals(form.getEncounterType(), encounterType)) {
					form.setEncounterType(encounterType);
					needToSaveForm = true;
				}
				
				if (needToSaveForm) {
					formService.saveForm(form);
				}
			}
		} else if (formService.getForm(formName) != null) { // ISSUE-150 If form with name present then retire it and
			                                                // create a new one
			Form form = formService.getForm(formName);
			formService.retireForm(form, "Replaced with new version by Iniz");
			createNewForm(formUuid, formName, formDescription, formPublished, formRetired, encounterType, formVersion,
			    jsonString);
		} else {// ISSUE-150 Create new form
			createNewForm(formUuid, formName, formDescription, formPublished, formRetired, encounterType, formVersion,
			    jsonString);
		}
	}
	
	private void createNewForm(String uuid, String formName, String formDescription, Boolean formPublished,
	        Boolean formRetired, EncounterType encounterType, String formVersion, String jsonString) {
		String clobUuid = UUID.randomUUID().toString();
		Form newForm = new Form();
		newForm.setName(formName);
		newForm.setVersion(formVersion);
		newForm.setUuid(uuid);
		newForm.setDescription(formDescription);
		newForm.setRetired(formRetired);
		newForm.setPublished(formPublished);
		newForm.setEncounterType(encounterType);
		
		newForm = formService.saveForm(newForm);
		FormResource formResource;
		formResource = new FormResource();
		formResource.setName("JSON schema");
		formResource.setForm(newForm);
		formResource.setValueReferenceInternal(clobUuid);
		formResource.setDatatypeClassname("AmpathJsonSchema");
		formService.saveFormResource(formResource);
		
		ClobDatatypeStorage clobData = new ClobDatatypeStorage();
		clobData.setUuid(clobUuid);
		clobData.setValue(jsonString);
		datatypeService.saveClobDatatypeStorage(clobData);
	}
	
	private static String replaceExtension(String fileName, String newExtension) {
		// Validate inputs
		if (StringUtils.isEmpty(fileName) || StringUtils.isEmpty(newExtension)) {
			throw new IllegalArgumentException("File name and extension must not be null or empty");
		}
		
		// Find the last dot in the file name
		int lastDotIndex = fileName.lastIndexOf('.');
		
		// Handle the case where there's no dot in the file name
		if (lastDotIndex == -1) {
			return fileName + "." + newExtension;
		}
		
		// Replace the extension
		return fileName.substring(0, lastDotIndex) + "." + newExtension;
	}
}

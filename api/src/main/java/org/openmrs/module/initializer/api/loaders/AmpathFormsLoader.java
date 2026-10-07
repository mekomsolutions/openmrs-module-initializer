package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
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

import java.util.Optional;
import java.util.UUID;

import static org.openmrs.module.initializer.InitializerConstants.PROPS_ROW_CHECKSUMS_ENABLED;
import static org.openmrs.module.initializer.api.utils.Utils.getPropertyValue;

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
		
		boolean formPublished = resolveBooleanValue(jsonFile.get("published"), cfg.getDefaultFormPublishedState());
		boolean formRetired = resolveBooleanValue(jsonFile.get("retired"), false);
		
		EncounterType encounterType = getEncounterType(jsonFile, formName);
		
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
		if (formService.getFormByUuid(uuid) != null) {
			Form form = formService.getFormByUuid(uuid);
			
			if (OpenmrsUtil.nullSafeEquals(form.getUuid(), uuid)) {
				ClobDatatypeStorage clobData = datatypeService
				        .getClobDatatypeStorageByUuid(formService.getFormResource(form, "JSON schema").getValueReference());
				clobData.setValue(jsonString);
				datatypeService.saveClobDatatypeStorage(clobData);
				
				boolean needToSaveForm = false;
				// Description
				if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
					form.setDescription(formDescription);
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
			createNewForm(uuid, formName, formDescription, formPublished, formRetired, encounterType, formVersion,
			    jsonString);
		} else {// ISSUE-150 Create new form
			createNewForm(uuid, formName, formDescription, formPublished, formRetired, encounterType, formVersion,
			    jsonString);
		}
	}
	
	private boolean resolveBooleanValue(Object booleanValue, boolean defaultValue) {
		if (booleanValue instanceof Boolean) {
			return (Boolean) booleanValue;
		}
		return BooleanUtils.toBoolean(Optional.ofNullable((String) booleanValue).orElse(Boolean.toString(defaultValue)));
	}
	
	private EncounterType getEncounterType(Map<String, Object> jsonFile, String formName) {
		EncounterType encounterType = null;

		String formEncounterType = (String) jsonFile.get("encounter");
		if (!StringUtils.isBlank(formEncounterType)) {
			encounterType = encounterService.getEncounterType(formEncounterType);
		}
		
		String formEncounterTypeId = (String) jsonFile.get("encounterType");
        if (!StringUtils.isBlank(formEncounterTypeId)) {
			if (encounterType == null) {
				encounterType = encounterService.getEncounterTypeByUuid(formEncounterTypeId);
			} else if (!formEncounterTypeId.equals(formEncounterType) // Some of the existing form data already has both fields filled in with the name of the encounter type so we need to support that
			        && !encounterType.getUuid().equals(formEncounterTypeId)) {
				throw new IllegalArgumentException("Both the 'encounter' (" + formEncounterType + ") and 'encounterType' ("
				        + formEncounterTypeId + ") fields are filled in for the form '" + formName
				        + "', but they do not represent the same encounter type.");
			}
		}
		
		if (encounterType != null) {
			return encounterType;
		}
		
		String formProcessor = (String) jsonFile.get("processor");
		boolean isEncounterForm = formProcessor == null || StringUtils.isBlank(formProcessor)
		        || formProcessor.equalsIgnoreCase("EncounterFormProcessor");
		
		if (isEncounterForm) {
			throw new IllegalArgumentException(
			        encounterService.getEncounterType("Emergency").getUuid() + " - No encounter was found for the form '"
			                + formName + "'." + " You must have an 'encounter' entry whose value (" + formEncounterType
			                + ") is the name of the encounter type (e.g. 'encounter': 'Emergency')"
			                + " or an 'encounterType' entry whose value (" + formEncounterTypeId
			                + ") is the id of the encounter type (e.g. 'encounterType': '<UUID>').");
		}
		
		return null;
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

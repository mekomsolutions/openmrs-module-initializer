package org.openmrs.module.initializer.api.loaders;

import java.io.File;
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
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AmpathFormsLoader extends BaseFileLoader {
	
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
		String jsonString = FileUtils.readFileToString(file, "UTF-8");
		Map jsonFile = new ObjectMapper().readValue(jsonString, Map.class);
		
		String formName = (String) jsonFile.get("name");
		if (formName == null) {
			throw new IllegalArgumentException("Form Name is required");
		}
		String formDescription = (String) jsonFile.get("description");
		boolean formPublished = (Boolean) jsonFile.get("published");
		boolean formRetired = (Boolean) jsonFile.get("retired");
		String formEncounterType = (String) jsonFile.get("encounter");
		EncounterType encounterType = null;
		if (formEncounterType != null) {
			encounterType = encounterService.getEncounterType(formEncounterType);
			if (encounterType == null) {
				throw new IllegalArgumentException("Form Encounter type not found");
			}
		}
		String formVersion = (String) jsonFile.get("version");
		if (formVersion == null) {
			throw new IllegalArgumentException("Form Version is required");
		}
		// Add Form
		Form form = formService.getForm(formName);
		
		if (form != null) {
			
			if (OpenmrsUtil.nullSafeEquals(form.getVersion(), formVersion)) {
				ClobDatatypeStorage clobData;
				
				clobData = datatypeService
				        .getClobDatatypeStorageByUuid(formService.getFormResource(form, "JSON schema").getValueReference());
				clobData.setValue(jsonString);
				clobData = datatypeService.saveClobDatatypeStorage(clobData);
				
				boolean needToSaveForm = false;
				// Description
				if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
					form.setDescription(formDescription);
					needToSaveForm = true;
				}
				
				// Add in schema
				// Published
				if (!OpenmrsUtil.nullSafeEquals(form.getPublished(), formPublished)) {
					form.setPublished((Boolean) formPublished);
					needToSaveForm = true;
					
				}
				// Add to schema
				// Retired
				if (!OpenmrsUtil.nullSafeEquals(form.getRetired(), formRetired)) {
					form.setRetired((Boolean) formRetired);
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
					form = formService.saveForm(form);
					
				}
			} else {
				if (formVersion.compareTo(form.getVersion()) < 0) {
					// Fix comparator or consider having ints rather than Strings for version
					// control
					throw new IllegalArgumentException(
					        "Form Version can not be less than the current version in the system");
				}
				formService.retireForm(form, "Retired by Iniz for new version");
				
				createNewForm(formName, formDescription, formPublished, formRetired, encounterType, formVersion, jsonString);
				
			}
			
		} else {
			createNewForm(formName, formDescription, formPublished, formRetired, encounterType, formVersion, jsonString);
			
		}
		
	}
	
	private void createNewForm(String formName, String formDescription, Boolean formPublished, Boolean formRetired,
	        EncounterType encounterType, String formVersion, String jsonString) {
		
		Form newForm = new Form();
		newForm.setName(formName);
		newForm.setVersion(formVersion);
		UUID randomUUID = UUID.randomUUID();
		String uuidAsString = randomUUID.toString();
		newForm.setUuid(uuidAsString);
		newForm.setDescription(formDescription);
		newForm.setRetired(formRetired);
		newForm.setPublished(formPublished);
		newForm.setEncounterType(encounterType);
		
		newForm = formService.saveForm(newForm);
		FormResource formResource;
		formResource = new FormResource();
		formResource.setName("JSON schema");
		formResource.setForm(newForm);
		formResource.setUuid(uuidAsString);
		formResource.setValueReferenceInternal(uuidAsString);
		formResource.setDatatypeClassname("AmpathJsonSchema");
		formResource = formService.saveFormResource(formResource);
		
		ClobDatatypeStorage clobData = new ClobDatatypeStorage();
		clobData.setUuid(uuidAsString);
		clobData.setValue(jsonString);
		clobData = datatypeService.saveClobDatatypeStorage(clobData);
	}
}

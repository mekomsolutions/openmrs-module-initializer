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
		String formUuid = (String) jsonFile.get("uuid");
		System.out.println(formUuid);
		if (formUuid == null) {
			throw new IllegalArgumentException("Form UUID is required");
		}
		Form form = formService.getFormByUuid(formUuid);
		boolean needToSaveForm = false;
		if (form == null) {
			form = new Form();
			form.setUuid(formUuid);
			needToSaveForm = true;
		}
		
		//Name
		String formName = (String) jsonFile.get("name");
		if (!OpenmrsUtil.nullSafeEquals(form.getName(), formName)) {
			form.setName(formName);
			needToSaveForm = true;
		}
		
		//Description
		String formDescription = (String) jsonFile.get("description");
		if (!OpenmrsUtil.nullSafeEquals(form.getDescription(), formDescription)) {
			form.setDescription(formDescription);
			needToSaveForm = true;
		}
		
		//Add in schema
		boolean formPublished = (Boolean) jsonFile.get("published");
		if (!OpenmrsUtil.nullSafeEquals(form.getPublished(), formPublished)) {
			form.setPublished((Boolean) formPublished);
			needToSaveForm = true;
			
		}
		
		boolean formRetired = (Boolean) jsonFile.get("retired");
		if (!OpenmrsUtil.nullSafeEquals(form.getRetired(), formRetired)) {
			form.setRetired((Boolean) formRetired);
			if (formRetired && StringUtils.isBlank(form.getRetireReason())) {
				form.setRetireReason("Retired by Initializer");
			}
			needToSaveForm = true;
		}
		
		//Version
		String formVersion = (String) jsonFile.get("version");
		if (!OpenmrsUtil.nullSafeEquals(form.getVersion(), formVersion)) {
			form.setVersion(formVersion);
			//form.setVersion("1");
			
			needToSaveForm = true;
		}
		
		//Add encounter to schema
		String formEncounterType = (String) jsonFile.get("encounter");
		EncounterType encounterType = null;
		if (formEncounterType != null) {
			encounterType = encounterService.getEncounterType(formEncounterType);
			
		}
		if (encounterType != null && !OpenmrsUtil.nullSafeEquals(form.getEncounterType(), encounterType)) {
			form.setEncounterType(encounterType);
			needToSaveForm = true;
		}
		
		FormResource formResource;
		ClobDatatypeStorage clobData;
		if (needToSaveForm) {
			clobData = new ClobDatatypeStorage();
			clobData.setUuid(formUuid);
			clobData.setValue(jsonString);
			clobData = datatypeService.saveClobDatatypeStorage(clobData);
			
			//Set up Form resource
			formResource = new FormResource();
			formResource.setName("JSON schema");
			formResource.setForm(form);
			formResource.setUuid(formUuid);
			formResource.setValueReferenceInternal(formUuid);
			formResource.setDatatypeClassname("AmpathJsonSchema");
			
			form = formService.saveForm(form);
			formResource = formService.saveFormResource(formResource);
			
		}
		
	}
}

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
import org.openmrs.module.initializer.api.utils.Utils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AmpathFormsLoader extends BaseFileLoader {

	public static final String AMPATH_FORMS_UUID = "794c4598-ab82-47ca-8d18-483a8abe6f4f";

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
		if (StringUtils.isBlank(formName)) {
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
		String uuid = Utils.generateUuidFromObjects(AMPATH_FORMS_UUID, formName, formVersion);
		// Process Form
		// ISSUE-150 If form with uuid present then update it
		if (formService.getFormByUuid(uuid) != null) {
			Form form = formService.getFormByUuid(uuid);

			if (OpenmrsUtil.nullSafeEquals(form.getUuid(), uuid)) {
				ClobDatatypeStorage clobData = datatypeService
						.getClobDatatypeStorageByUuid(
								formService.getFormResource(form, "JSON schema").getValueReference());
				clobData.setValue(jsonString);
				clobData = datatypeService.saveClobDatatypeStorage(clobData);

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
		formResource = formService.saveFormResource(formResource);

		ClobDatatypeStorage clobData = new ClobDatatypeStorage();
		clobData.setUuid(clobUuid);
		clobData.setValue(jsonString);
		clobData = datatypeService.saveClobDatatypeStorage(clobData);
	}
}

package org.openmrs.module.initializer.api.loaders;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.Form;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.FormService;
import org.openmrs.module.initializer.Domain;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "bahmni.ie.apps:*" })
public class BahmniFormsLoader extends BaseInputStreamLoader {
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private BahmniFormService bahmniFormService;
	
	@Autowired
	private BahmniFormTranslationService bahmniFormTranslationService;
	
	@Override
	protected Domain getDomain() {
		return Domain.BAHMNI_FORMS;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	protected void load(InputStream is) throws Exception {
		Map jsonFile = new ObjectMapper().readValue(is, Map.class);
		Map formJson = (Map) jsonFile.get("formJson");
		List<Object> translations = (List<Object>) jsonFile.get("translations");
		String formName = (String) formJson.get("name");
		Boolean published = (Boolean) formJson.get("published");
		String uuid = (String) formJson.get("uuid");
		List<Map> resources = (List<Map>) formJson.get("resources");
		String resourceValue = (String) resources.get(0).get("value");
		
		// check if form exist
		Form existingForm = formService.getFormByUuid(uuid);
		Form form;
		if (existingForm != null)
			form = existingForm;
		else {
			form = new Form();
			form.setVersion("1");
		}
		
		// Save OpenMRS form
		form.setPublished(false);
		form.setName(formName);
		form.setUuid(uuid);
		form = formService.saveForm(form);
		
		// Save Bahmni form resource
		BahmniForm bahmniForm = new BahmniForm();
		bahmniForm.setName(form.getName());
		bahmniForm.setVersion(form.getVersion());
		bahmniForm.setUuid(form.getUuid());
		bahmniForm.setPublished(form.getPublished());
		
		BahmniFormResource bahmniFormResource = new BahmniFormResource();
		bahmniFormResource.setForm(bahmniForm);
		bahmniFormResource.setUuid(form.getUuid());
		bahmniFormResource.setValue(resourceValue);
		bahmniFormService.saveFormResource(bahmniFormResource);
		
		// Save translation
		List<FormTranslation> formTranslations = new ObjectMapper().convertValue(translations,
		    new TypeReference<List<FormTranslation>>() {});
		
		Form finalForm = form;
		formTranslations = formTranslations.stream().map(formTranslation -> {
			formTranslation.setFormUuid(finalForm.getUuid());
			return formTranslation;
		}).collect(Collectors.toList());
		
		bahmniFormTranslationService.saveFormTranslation(formTranslations);
		
		// publish the form
		if (published) {
			bahmniFormService.publish(bahmniForm.getUuid());
		}
	}
}

package org.openmrs.module.initializer.api.form;

import org.apache.commons.logging.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Form;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.FormService;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerLogFactory;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.loaders.BaseLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@OpenmrsProfile(modules = { "bahmni.ie.apps:*" })
@Component
public class BahmniFormsLoader extends BaseLoader {
	
	private final Log log = InitializerLogFactory.getLog(getClass());
	
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
	public void load() {
		ConfigDirUtil dirUtil = getDirUtil();
		for (File file : dirUtil.getFiles("json")) { // processing all the JSON files inside the domain
			
			String fileName = dirUtil.getFileName(file.getPath());
			String checksum = dirUtil.getChecksumIfChanged(fileName);
			if (checksum.isEmpty()) {
				continue;
			}
			InputStream is = null;
			try {
				
				//Extract Json data from file
				is = new FileInputStream(file);
				Map jsonFile = new ObjectMapper().readValue(is, Map.class);
				Map formJson = (Map) jsonFile.get("formJson");
				String formName = (String) formJson.get("name");
				Boolean published = (Boolean) formJson.get("published");
				List<Map> resources = (List<Map>) formJson.get("resources");
				String resourceValue = (String) resources.get(0).get("value");
				
				// check if form exist
				Form existingForm = formService.getForm(formName);
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
				
				// Save Translation
				List<FormTranslation> formTranslations = (List<FormTranslation>) jsonFile.get("translations");
				bahmniFormTranslationService.saveFormTranslation(formTranslations);
				
				// publish the form
				if (published)
					bahmniFormService.publish(bahmniForm.getUuid());
				dirUtil.writeChecksum(fileName, checksum); // the updated config. file is marked as processed
				log.info("The form file has been processed: " + fileName);
			}
			catch (Exception e) {
				log.error("Could not load the form from: " + file.getPath(), e);
			}
		}
		
	}
}

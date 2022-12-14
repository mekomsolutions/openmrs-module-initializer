package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class AmpathFormsTranslationsLoader extends BaseFileLoader {
	
	@Autowired
	private FormService formService;
	
	@Override
	protected Domain getDomain() {
		return Domain.AMPATH_FORMS_TRANSLATIONS;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	protected void load(File file) throws Exception {
		String jsonTranslationsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
		Map<String, Object> jsonTranslationsDefinition = new ObjectMapper().readValue(jsonTranslationsString, Map.class);
		
		String formResourceUuid = (String) jsonTranslationsDefinition.get("uuid");
		if (StringUtils.isBlank(formResourceUuid)) {
			throw new Exception("Uuid is required for AMPATH forms translations loader");
		}
		
		String jsonTranlsationsFileName = FilenameUtils.removeExtension(file.getName());
		
		String language = (String) jsonTranslationsDefinition.get("language");
		if (StringUtils.isBlank(language)
		        && !language.equalsIgnoreCase(jsonTranlsationsFileName.split("_translations_")[1])) {
			throw new Exception(
			        "'language' property is required for AMPATH forms translations loader and should align with locale appended to the file name.");
		}
		
		Form form = null;
		FormResource formResource = Context.getFormService().getFormResourceByUuid(formResourceUuid);
		if (formResource == null) {
			formResource = new FormResource();
			formResource.setUuid(formResourceUuid);
		} else {
			form = formResource.getForm();
		}
		
		if (form == null) {
			String formName = (String) jsonTranslationsDefinition.get("form");
			form = formService.getForm(formName);
			if (form == null) {
				throw new RuntimeException("No AMPATH form exists for AMPATH form tranlsations file: " + file.getName()
				        + ". An existing form name should be specified on the 'form' property");
			}
		}
		
		formResource.setForm(form);
		formResource.setName(form.getName() + "_translations_" + language);
		formResource.setDatatypeClassname("org.openmrs.customdatatype.datatype.LongFreeTextDatatype");
		formResource.setValue(jsonTranslationsString);
		formService.saveFormResource(formResource);
	}
}

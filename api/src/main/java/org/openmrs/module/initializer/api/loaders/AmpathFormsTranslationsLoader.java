package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class AmpathFormsTranslationsLoader extends BaseFileLoader {
	
	@Autowired
	private FormService formService;
	
	@Autowired
	@Qualifier("initializer.InitializerMessageSource")
	private InitializerMessageSource msgSource;
	
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
			throw new IllegalArgumentException("Uuid is required for AMPATH forms translations loader.");
		}
		
		String language = (String) jsonTranslationsDefinition.get("language");
		if (StringUtils.isBlank(language)) {
			throw new IllegalArgumentException("'language' property is required for AMPATH forms translations loader.");
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
			if (formName == null) {
				throw new IllegalArgumentException("'form' property is required for AMPATH forms translations loader.");
			}
			form = formService.getForm(formName);
			if (form == null) {
				throw new IllegalArgumentException(
				        "Could not find a form named '" + formName + "'. Please ensure an existing form is configured.");
			}
		}
		
		formResource.setForm(form);
		formResource.setName(form.getName() + "_translations_" + language);
		formResource.setDatatypeClassname("org.openmrs.customdatatype.datatype.LongFreeTextDatatype");
		formResource.setValue(jsonTranslationsString);
		formService.saveFormResource(formResource);
		
		String formNameTranslation = (String) jsonTranslationsDefinition.get("form_name_translation");
		if (!StringUtils.isBlank(formNameTranslation)) {
			msgSource.addPresentation(new PresentationMessage("ui.i18n.Form.name." + form.getUuid(),
			        LocaleUtils.toLocale(language), formNameTranslation, null));
			msgSource.addPresentation(new PresentationMessage("org.openmrs.Form." + form.getUuid(),
			        LocaleUtils.toLocale(language), formNameTranslation, null));
		}
	}
	
	@Override
	public ConfigDirUtil getDirUtil() {
		return new ConfigDirUtil(iniz.getConfigDirPath(), iniz.getChecksumsDirPath(), getDomainName(), true);
	}
}

package org.openmrs.module.initializer.api.loaders;

import java.io.File;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.initializer.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

import static org.openmrs.module.initializer.Domain.AMPATH_TRANSLATIONS;

@Component
public class AmpathTranslationsLoader extends BaseFileLoader {
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Override
	protected Domain getDomain() {
		return AMPATH_TRANSLATIONS;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	protected void load(File file) throws Exception {
		String jsonTranslationsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString());
		Map<String, Object> jsonTranslationsFile = new ObjectMapper().readValue(jsonTranslationsString, Map.class);
		
		String clobUuid = (String) jsonTranslationsFile.get("uuid");
		if (StringUtils.isBlank(clobUuid)) {
			throw new Exception("Uuid is required for AMPATH translations loader");
		}
		
		String language = (String) jsonTranslationsFile.get("language");
		if (StringUtils.isBlank(language)) {
			throw new Exception("language is required for AMPATH translations loader");
		}
		
		ClobDatatypeStorage clobTranslationsData = datatypeService.getClobDatatypeStorageByUuid(clobUuid);
		if (clobTranslationsData == null) {
			clobTranslationsData = new ClobDatatypeStorage();
		}
		
		clobTranslationsData.setUuid(clobUuid);
		clobTranslationsData.setValue(jsonTranslationsString);
		datatypeService.saveClobDatatypeStorage(clobTranslationsData);
	}
}

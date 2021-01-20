package org.openmrs.module.initializer.api.loaders;

import java.io.InputStream;

import org.openmrs.module.initializer.Domain;
import org.springframework.stereotype.Component;

@Component
public class JsonKeyValuesLoader extends BaseInputStreamLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.JSON_KEY_VALUES;
	}
	
	@Override
	protected String getFileExtension() {
		return "json";
	}
	
	@Override
	protected void load(InputStream is) throws Exception {
		iniz.addKeyValues(is);
	}
}

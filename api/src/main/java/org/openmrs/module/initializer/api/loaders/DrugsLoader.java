package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.drugs.DrugsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DrugsLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_DRUGS;
	}
	
	@Override
	public Integer getOrder() {
		return 12;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new DrugsCsvParser(is, service);
	}
	
}

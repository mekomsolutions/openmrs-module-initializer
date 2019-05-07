package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.drugs.DrugsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DrugsLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.DRUGS;
	}
	
	@Autowired
	@Qualifier("conceptService")
	private ConceptService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new DrugsCsvParser(is, service);
	}
	
}

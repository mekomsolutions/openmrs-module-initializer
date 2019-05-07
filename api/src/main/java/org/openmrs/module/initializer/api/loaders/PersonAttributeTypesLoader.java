package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.pat.PersonAttributeTypesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypesLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.PERSON_ATTRIBUTE_TYPES;
	}
	
	@Autowired
	@Qualifier("personService")
	private PersonService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new PersonAttributeTypesCsvParser(is, service);
	}
	
}

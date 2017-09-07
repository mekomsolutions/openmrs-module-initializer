package org.openmrs.module.initializer.api.pat;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.api.CsvParser;

public class PersonAttributeTypesCsvParser extends CsvParser<PersonAttributeType, PersonService, PersonAttributeTypeLineProcessor> {
	
	public PersonAttributeTypesCsvParser(InputStream is, PersonService ps) throws IOException {
		super(is, ps);
	}
	
	@Override
	protected PersonAttributeType save(PersonAttributeType instance) {
		return service.savePersonAttributeType(instance);
	}
	
	@Override
	protected boolean voidOrRetire(PersonAttributeType instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new PersonAttributeTypeLineProcessor(headerLine, service));
	}
}

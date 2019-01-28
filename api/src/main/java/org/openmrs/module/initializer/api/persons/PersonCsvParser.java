package org.openmrs.module.initializer.api.persons;

import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.api.CsvParser;

import java.io.IOException;
import java.io.InputStream;

public class PersonCsvParser extends CsvParser<Person, PersonService, PersonLineProcessor> {

	public PersonCsvParser(InputStream is, PersonService ps) throws IOException {
		super(is, ps);
	}
	
	@Override
	protected Person save(Person instance) {
		return service.savePerson(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Person instance) {
		return instance.getVoided();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new PersonLineProcessor(headerLine, service));
	}
}

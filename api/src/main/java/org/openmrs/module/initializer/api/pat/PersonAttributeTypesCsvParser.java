package org.openmrs.module.initializer.api.pat;

import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypesCsvParser extends CsvParser<PersonAttributeType, PersonService, PersonAttributeTypeLineProcessor> {
	
	@Autowired
	public PersonAttributeTypesCsvParser(@Qualifier("personService") PersonService service) {
		this.service = service;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PERSON_ATTRIBUTE_TYPES;
	}
	
	@Override
	protected PersonAttributeType save(PersonAttributeType instance) {
		return service.savePersonAttributeType(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(PersonAttributeType instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new PersonAttributeTypeLineProcessor(headerLine, service));
	}
}

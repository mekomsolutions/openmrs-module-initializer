package org.openmrs.module.initializer.api.pat;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypesCsvParser extends CsvParser<PersonAttributeType, BaseLineProcessor<PersonAttributeType>> {
	
	private PersonService personService;
	
	@Autowired
	public PersonAttributeTypesCsvParser(@Qualifier("personService") PersonService personService,
	    PersonAttributeTypeLineProcessor processor) {
		super(processor);
		this.personService = personService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PERSON_ATTRIBUTE_TYPES;
	}
	
	@Override
	public PersonAttributeType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String name = line.getName();
		
		PersonAttributeType pat = new PersonAttributeType();
		pat = personService.getPersonAttributeTypeByUuid(uuid);
		
		if (pat == null && StringUtils.isEmpty(uuid) && !StringUtils.isEmpty(name)) {
			pat = personService.getPersonAttributeTypeByName(name);
		}
		
		if (pat == null) {
			pat = new PersonAttributeType();
			if (StringUtils.isEmpty(uuid)) {
				pat.setUuid(uuid);
			}
		}
		
		return pat;
	}
	
	@Override
	public PersonAttributeType save(PersonAttributeType instance) {
		return personService.savePersonAttributeType(instance);
	}
}

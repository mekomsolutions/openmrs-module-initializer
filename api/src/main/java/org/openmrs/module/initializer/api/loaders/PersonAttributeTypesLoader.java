package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.pat.PersonAttributeTypesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonAttributeTypesLoader extends BaseCsvLoader<PersonAttributeTypesCsvParser> {
	
	@Autowired
	public void setParser(PersonAttributeTypesCsvParser parser) {
		this.parser = parser;
	}
	
}

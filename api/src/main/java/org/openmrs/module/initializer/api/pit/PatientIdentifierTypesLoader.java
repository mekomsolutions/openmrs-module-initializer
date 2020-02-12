package org.openmrs.module.initializer.api.pit;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierTypesLoader extends BaseCsvLoader<PatientIdentifierType, PatientIdentifierTypesCsvParser> {
	
	@Autowired
	public void setParser(PatientIdentifierTypesCsvParser parser) {
		this.parser = parser;
	}
}

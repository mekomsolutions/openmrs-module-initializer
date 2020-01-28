package org.openmrs.module.initializer.api.pit;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierTypeLoader extends BaseCsvLoader<PatientIdentifierType, PatientIdentifierTypeCsvParser> {
	
	@Autowired
	public void setParser(PatientIdentifierTypeCsvParser parser) {
		this.parser = parser;
	}
}

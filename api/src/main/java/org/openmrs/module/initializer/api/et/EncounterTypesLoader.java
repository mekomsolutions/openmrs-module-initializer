package org.openmrs.module.initializer.api.et;

import org.openmrs.EncounterType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterTypesLoader extends BaseCsvLoader<EncounterType, EncounterTypesCsvParser> {
	
	@Autowired
	public void setParser(EncounterTypesCsvParser parser) {
		this.parser = parser;
	}
}

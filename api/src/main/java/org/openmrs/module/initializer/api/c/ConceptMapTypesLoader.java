package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptMapType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptMapTypesLoader extends BaseCsvLoader<ConceptMapType, ConceptMapTypesCsvParser> {
	
	@Autowired
	public void setParser(ConceptMapTypesCsvParser parser) {
		this.parser = parser;
	}
}

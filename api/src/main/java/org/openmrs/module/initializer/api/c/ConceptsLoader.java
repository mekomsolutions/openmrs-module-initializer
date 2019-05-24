package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptsLoader extends BaseCsvLoader<Concept, ConceptsCsvParser> {
	
	@Autowired
	public void setParser(ConceptsCsvParser parser) {
		this.parser = parser;
	}
}

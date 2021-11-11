package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptSetsLoader extends BaseCsvLoader<Concept, ConceptSetsCsvParser> {
	
	@Autowired
	public void setParser(ConceptSetsCsvParser parser) {
		this.parser = parser;
	}
}

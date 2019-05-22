package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.c.ConceptsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptsLoader extends BaseCsvLoader<ConceptsCsvParser> {
	
	@Autowired
	public void setParser(ConceptsCsvParser parser) {
		this.parser = parser;
	}
}

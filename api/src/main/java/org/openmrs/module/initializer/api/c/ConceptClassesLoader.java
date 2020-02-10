package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptClass;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassesLoader extends BaseCsvLoader<ConceptClass, ConceptClassesCsvParser> {
	
	@Autowired
	public void setParser(ConceptClassesCsvParser parser) {
		this.parser = parser;
	}
}

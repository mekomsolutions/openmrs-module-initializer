package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptClass;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptClassLoader extends BaseCsvLoader<ConceptClass, ConceptClassCsvParser> {
	
	@Autowired
	public void setParser(ConceptClassCsvParser parser) {
		this.parser = parser;
	}
}

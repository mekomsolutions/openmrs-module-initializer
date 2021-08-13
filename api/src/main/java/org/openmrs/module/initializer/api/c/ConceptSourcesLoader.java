package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptSource;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConceptSourcesLoader extends BaseCsvLoader<ConceptSource, ConceptSourcesCsvParser> {
	
	@Autowired
	public void setParser(ConceptSourcesCsvParser parser) {
		this.parser = parser;
	}
}

package org.openmrs.module.initializer.api.conceptreferencerange;

import org.openmrs.ConceptReferenceRange;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ConceptReferenceRangeLoader extends BaseCsvLoader<ConceptReferenceRange, ConceptReferenceRangeCsvParser> {
	
	@Autowired
	public void setParser(ConceptReferenceRangeCsvParser parser) {
		this.parser = parser;
	}
	
	@Override
	protected void preload(File file) {
	}
}

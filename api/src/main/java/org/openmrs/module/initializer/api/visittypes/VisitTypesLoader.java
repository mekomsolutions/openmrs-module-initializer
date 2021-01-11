package org.openmrs.module.initializer.api.visittypes;

import org.openmrs.VisitType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitTypesLoader extends BaseCsvLoader<VisitType, VisitTypesCsvParser> {
	
	@Autowired
	public void setParser(VisitTypesCsvParser parser) {
		this.parser = parser;
	}
}

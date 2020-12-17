package org.openmrs.module.initializer.api.visittypes;

import org.openmrs.VisitType;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitTypeLoader extends BaseCsvLoader<VisitType, VisitTypeCsvParser> {

	@Autowired
	public void setParser(VisitTypeCsvParser parser) {
		this.parser = parser;
	}
}

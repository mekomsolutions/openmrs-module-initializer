package org.openmrs.module.initializer.api.loc;

import org.openmrs.LocationTag;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTagsLoader extends BaseCsvLoader<LocationTag, LocationTagsCsvParser> {
	
	@Autowired
	public void setParser(LocationTagsCsvParser parser) {
		this.parser = parser;
	}
}

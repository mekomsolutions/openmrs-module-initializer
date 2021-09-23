package org.openmrs.module.initializer.api.loc;

import org.openmrs.Location;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTagMapsLoader extends BaseCsvLoader<Location, LocationTagMapsCsvParser> {
	
	@Autowired
	public void setParser(LocationTagMapsCsvParser parser) {
		this.parser = parser;
	}
}

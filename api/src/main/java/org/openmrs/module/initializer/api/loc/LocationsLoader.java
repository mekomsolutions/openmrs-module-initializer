package org.openmrs.module.initializer.api.loc;

import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationsLoader extends BaseCsvLoader<LocationsCsvParser> {
	
	@Autowired
	public void setParser(LocationsCsvParser parser) {
		this.parser = parser;
	}
}

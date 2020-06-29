package org.openmrs.module.initializer.api.loc;

import org.openmrs.LocationTag;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTagMapsLoader extends BaseCsvLoader<LocationTagMap, LocationTagMapsCsvParser> {
	
	// Since LocationTagMap is not actually a proper OpenMRS Object, we use
	// our own LocationTagMap class, which extends BaseOpenmrsObject and happens
	// to behave correctly enough for our limited needs.
	
	@Autowired
	public void setParser(LocationTagMapsCsvParser parser) {
		this.parser = parser;
	}
}

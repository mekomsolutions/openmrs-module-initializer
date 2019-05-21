package org.openmrs.module.initializer.api.utils;

import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTagListParser extends ListParser<LocationTag> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagListParser(LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	protected LocationTag lastMinuteSave(String id) {
		LocationTag tag = locationService.getLocationTagByName(id);
		if (tag == null) {
			log.info("The location tag identified by the name '" + id + "' was not found in database. Creating it...");
			tag = locationService.saveLocationTag(new LocationTag(id, ""));
		}
		return tag;
	}
	
	@Override
	protected LocationTag fetch(String id) {
		return Utils.fetchLocationTag(id, locationService);
	}
	
}

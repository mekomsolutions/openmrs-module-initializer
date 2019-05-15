package org.openmrs.module.initializer.api.utils;

import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;

public class LocationTagListParser extends ListParser<LocationTag> {
	
	private LocationService ls;
	
	public LocationTagListParser(LocationService ls) {
		this.ls = ls;
	}
	
	@Override
	protected LocationTag lastMinuteSave(String id) {
		LocationTag tag = ls.getLocationTagByName(id);
		if (tag == null) {
			log.info("The location tag identified by the name '" + id + "' was not found in database. Creating it...");
			tag = ls.saveLocationTag(new LocationTag(id, ""));
		}
		return tag;
	}
	
	@Override
	protected LocationTag fetch(String id) {
		return Utils.fetchLocationTag(id, ls);
	}
	
}

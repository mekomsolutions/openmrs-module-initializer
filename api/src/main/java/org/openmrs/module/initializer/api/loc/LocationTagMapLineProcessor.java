package org.openmrs.module.initializer.api.loc;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.locationTagMapLineProcessor")
public class LocationTagMapLineProcessor extends BaseLineProcessor<LocationTagMap> {
	
	protected static String HEADER_LOCATION_NAME = "location name";
	
	protected static String HEADER_TAG_NAME = "location tag name";
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagMapLineProcessor(@Qualifier("locationService") LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public LocationTagMap fill(LocationTagMap map, CsvLine line) throws IllegalArgumentException {
		
		Location location = locationService.getLocation(line.get(HEADER_LOCATION_NAME));
		LocationTag tag = locationService.getLocationTagByName(line.get(HEADER_TAG_NAME));
		boolean remove = getVoidOrRetire(line);
		
		map.setLocation(location);
		map.setLocationTag(tag);
		map.setRemove(remove);
		
		return map;
	}
}

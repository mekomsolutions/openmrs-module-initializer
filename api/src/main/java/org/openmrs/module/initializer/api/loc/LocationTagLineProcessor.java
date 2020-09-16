package org.openmrs.module.initializer.api.loc;

import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.locationTagLineProcessor")
public class LocationTagLineProcessor extends BaseLineProcessor<LocationTag> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagLineProcessor(@Qualifier("locationService") LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public LocationTag fill(LocationTag tag, CsvLine line) throws IllegalArgumentException {
		
		tag.setName(line.get(HEADER_NAME));
		tag.setDescription(line.get(HEADER_DESC));
		
		return tag;
	}
}

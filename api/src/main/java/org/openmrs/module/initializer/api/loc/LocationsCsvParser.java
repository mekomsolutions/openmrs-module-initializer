package org.openmrs.module.initializer.api.loc;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationsCsvParser extends CsvParser<Location, BaseLineProcessor<Location>> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationsCsvParser(@Qualifier("locationService") LocationService locationService,
	    LocationLineProcessor processor) {
		super(processor);
		this.locationService = locationService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.LOCATIONS;
	}
	
	@Override
	public Location bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String name = line.getName();
		
		Location loc = locationService.getLocationByUuid(uuid);
		
		if (loc == null && StringUtils.isEmpty(uuid) && !StringUtils.isEmpty(name)) {
			loc = locationService.getLocation(name);
		}
		
		if (loc == null) {
			loc = new Location();
			if (!StringUtils.isEmpty(uuid)) {
				loc.setUuid(uuid);
			}
		}
		
		return loc;
	}
	
	@Override
	public Location save(Location instance) {
		return locationService.saveLocation(instance);
	}
}

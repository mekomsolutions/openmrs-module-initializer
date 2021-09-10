package org.openmrs.module.initializer.api.loc;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.api.loc.LocationTagMapsLineProcessor.HEADER_LOCATION;

@Component
public class LocationTagMapsCsvParser extends CsvParser<Location, BaseLineProcessor<Location>> {
	
	LocationService locationService;
	
	@Autowired
	public LocationTagMapsCsvParser(@Qualifier("locationService") LocationService locationService,
	    @Qualifier("initializer.locationTagMapsLineProcessor") LocationTagMapsLineProcessor baseProcessor) {
		super(baseProcessor);
		this.locationService = locationService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.LOCATION_TAG_MAPS;
	}
	
	@Override
	public Location bootstrap(CsvLine line) throws IllegalArgumentException {
		String locationLookup = line.get(HEADER_LOCATION);
		if (StringUtils.isBlank(locationLookup)) {
			throw new IllegalArgumentException("No location column found in location tags map csv");
		}
		Location loc = locationService.getLocationByUuid(locationLookup);
		if (loc == null) {
			loc = locationService.getLocation(locationLookup);
		}
		
		if (loc == null) {
			throw new IllegalArgumentException("No matching location found for value: " + locationLookup);
		}
		return loc;
	}
	
	@Override
	public Location save(Location instance) {
		return locationService.saveLocation(instance);
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		lineProcessors.add(getSingleLineProcessor());
	}
}

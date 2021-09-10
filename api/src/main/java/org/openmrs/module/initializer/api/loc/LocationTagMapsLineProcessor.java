package org.openmrs.module.initializer.api.loc;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component("initializer.locationTagMapsLineProcessor")
public class LocationTagMapsLineProcessor extends BaseLineProcessor<Location> {
	
	protected static String HEADER_LOCATION = "location";
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagMapsLineProcessor(@Qualifier("locationService") LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public Location fill(Location loc, CsvLine line) throws IllegalArgumentException {
		for (String header : line.getHeaderLine()) {
			if (!header.trim().equalsIgnoreCase(HEADER_LOCATION)) {
				LocationTag tag = locationService.getLocationTagByUuid(header);
				if (tag == null) {
					tag = locationService.getLocationTagByName(header);
				}
				if (tag == null) {
					throw new IllegalArgumentException("No matching location tag found: " + header);
				}
				boolean shouldHaveTag = isTrue(line.getBool(header));
				if (locationHasTag(loc, tag)) {
					if (shouldHaveTag) {
						log.debug("Tag " + tag + " already present on location " + loc);
					} else {
						log.info("Removing tag " + tag + " from location " + loc);
						loc.removeTag(tag);
					}
				} else {
					if (shouldHaveTag) {
						log.info("Adding tag " + tag + " to location " + loc);
						loc.addTag(tag);
					} else {
						log.debug("Tag " + tag + " already absent on location " + loc);
					}
				}
			}
		}
		return loc;
	}
	
	private boolean locationHasTag(Location location, LocationTag locationTag) {
		return location.getTags() != null && location.getTags().contains(locationTag);
	}
}

package org.openmrs.module.initializer.api.loc;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationTagsCsvParser extends CsvParser<LocationTag, BaseLineProcessor<LocationTag>> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagsCsvParser(@Qualifier("locationService") LocationService locationService,
	    @Qualifier("initializer.locationTagLineProcessor") LocationTagLineProcessor baseProcessor) {
		super(baseProcessor);
		this.locationService = locationService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.LOCATION_TAGS;
	}
	
	@Override
	public LocationTag bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		String name = line.getName();
		
		LocationTag tag = locationService.getLocationTagByUuid(uuid);
		
		if (tag == null && StringUtils.isEmpty(uuid) && !StringUtils.isEmpty(name)) {
			tag = locationService.getLocationTagByName(name);
		}
		
		if (tag == null) {
			tag = new LocationTag();
			if (!StringUtils.isEmpty(uuid)) {
				tag.setUuid(uuid);
			}
		}
		
		return tag;
	}
	
	@Override
	public LocationTag save(LocationTag instance) {
		return locationService.saveLocationTag(instance);
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		lineProcessors.add(getSingleLineProcessor());
	}
}

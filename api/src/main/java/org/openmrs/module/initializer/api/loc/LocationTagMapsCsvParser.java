package org.openmrs.module.initializer.api.loc;

import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationTagMapsCsvParser extends CsvParser<LocationTagMap, BaseLineProcessor<LocationTagMap>> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationTagMapsCsvParser(@Qualifier("locationService") LocationService locationService,
	    @Qualifier("initializer.locationTagMapLineProcessor") LocationTagMapLineProcessor baseProcessor) {
		super(baseProcessor);
		this.locationService = locationService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.LOCATION_TAG_MAPS;
	}
	
	@Override
	public LocationTagMap bootstrap(CsvLine line) throws IllegalArgumentException {
		return new LocationTagMap();
	}
	
	@Override
	public LocationTagMap save(LocationTagMap instance) {
		return instance.save();
	}
	
	@Override
	protected void setLineProcessors(String version) {
		lineProcessors.clear();
		lineProcessors.add(getSingleLineProcessor());
	}
}

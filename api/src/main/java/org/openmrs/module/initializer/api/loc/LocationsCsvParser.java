package org.openmrs.module.initializer.api.loc;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.LocationTagListParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationsCsvParser extends CsvParser<Location, LocationService, LocationLineProcessor> {
	
	@Autowired
	public LocationsCsvParser(@Qualifier("locationService") LocationService service) {
		this.service = service;
	}
	
	@Override
	protected Location save(Location instance) {
		return service.saveLocation(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Location instance) {
		return instance.isRetired();
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new LocationLineProcessor(headerLine, service, new LocationTagListParser(service)));
	}
}

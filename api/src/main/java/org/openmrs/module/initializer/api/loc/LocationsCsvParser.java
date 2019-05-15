package org.openmrs.module.initializer.api.loc;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.utils.LocationTagListParser;

public class LocationsCsvParser extends CsvParser<Location, LocationService, LocationLineProcessor> {
	
	public LocationsCsvParser(InputStream is, LocationService ps) throws IOException {
		super(is, ps);
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

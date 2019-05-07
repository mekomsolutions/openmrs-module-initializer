package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.loc.LocationsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationsLoader extends BaseCsvLoader {
	
	@Override
	protected Domain getDomain() {
		return Domain.LOCATIONS;
	}
	
	@Autowired
	@Qualifier("locationService")
	private LocationService service;
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new LocationsCsvParser(is, service);
	}
}

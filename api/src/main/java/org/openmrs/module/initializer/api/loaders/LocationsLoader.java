package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.loc.LocationsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationsLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("locationService")
	private LocationService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_LOC;
	}
	
	@Override
	public Integer getOrder() {
		return 5;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new LocationsCsvParser(is, service);
	}
	
}

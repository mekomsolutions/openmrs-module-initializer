package org.openmrs.module.initializer.api.loc;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.LocationTagListParser;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LocationLineProcessor extends BaseLineProcessor<Location> {
	
	protected static String HEADER_PARENT = "parent";
	
	protected static String HEADER_TAGS = "tags";
	
	protected static String HEADER_CITY_VILLAGE = "city/village";
	
	protected static String HEADER_COUNTY_DISTRICT = "county/district";
	
	protected static String HEADER_STATE_PROVINCE = "state/province";
	
	protected static String HEADER_POSTAL_CODE = "postal code";
	
	protected static String HEADER_COUNTRY = "country";
	
	protected static String HEADER_ADDRESS_1 = "address 1";
	
	protected static String HEADER_ADDRESS_2 = "address 2";
	
	protected static String HEADER_ADDRESS_3 = "address 3";
	
	protected static String HEADER_ADDRESS_4 = "address 4";
	
	protected static String HEADER_ADDRESS_5 = "address 5";
	
	protected static String HEADER_ADDRESS_6 = "address 6";
	
	private LocationService locationService;
	
	private LocationTagListParser listParser;
	
	@Autowired
	public LocationLineProcessor(@Qualifier("locationService") LocationService locationService,
	    LocationTagListParser listParser) {
		this.locationService = locationService;
		this.listParser = listParser;
	}
	
	@Override
	protected Location bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = getUuid(line.asLine());
		
		Location loc = locationService.getLocationByUuid(uuid);
		if (loc == null) {
			loc = new Location();
			if (!StringUtils.isEmpty(uuid)) {
				loc.setUuid(uuid);
			}
		}
		
		return loc;
	}
	
	@Override
	protected Location fill(Location loc, CsvLine line) throws IllegalArgumentException {
		
		loc.setName(line.get(HEADER_NAME));
		loc.setDescription(line.get(HEADER_DESC));
		
		loc.setParentLocation(null);
		String parentId = line.getString(HEADER_PARENT, "");
		if (!StringUtils.isEmpty(parentId)) {
			loc.setParentLocation(Utils.fetchLocation(parentId, locationService));
		}
		
		loc.setTags(null);
		String tags = line.getString(HEADER_TAGS, "");
		if (!StringUtils.isEmpty(tags)) {
			loc.setTags(new HashSet<LocationTag>(listParser.parseList(tags)));
		}
		
		loc.setCityVillage(line.get(HEADER_CITY_VILLAGE));
		loc.setCountyDistrict(line.get(HEADER_COUNTY_DISTRICT));
		loc.setStateProvince(line.get(HEADER_STATE_PROVINCE));
		loc.setPostalCode(line.get(HEADER_POSTAL_CODE));
		loc.setCountry(line.get(HEADER_COUNTRY));
		loc.setAddress1(line.get(HEADER_ADDRESS_1));
		loc.setAddress2(line.get(HEADER_ADDRESS_2));
		loc.setAddress3(line.get(HEADER_ADDRESS_3));
		loc.setAddress4(line.get(HEADER_ADDRESS_4));
		loc.setAddress5(line.get(HEADER_ADDRESS_5));
		loc.setAddress6(line.get(HEADER_ADDRESS_6));
		
		return loc;
	}
}

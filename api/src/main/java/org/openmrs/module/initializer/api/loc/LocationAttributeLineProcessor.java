package org.openmrs.module.initializer.api.loc;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseAttributeLineProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.locationAttributeLineProcessor")
public class LocationAttributeLineProcessor extends BaseAttributeLineProcessor<Location, LocationAttributeType, LocationAttribute> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationAttributeLineProcessor(@Qualifier("locationService") LocationService locationService) {
		this.locationService = locationService;
	}
	
	/**
	 * See {@link BaseAttributeLineProcessor#getAttributeType(String)}
	 */
	@Override
	public LocationAttributeType getAttributeType(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("A blank attribute type identifier was provided.");
		}
		LocationAttributeType ret = locationService.getLocationAttributeTypeByUuid(identifier);
		if (ret == null) {
			ret = locationService.getLocationAttributeTypeByName(identifier);
		}
		return ret;
	}
	
	/**
	 * See
	 * {@link BaseAttributeLineProcessor#newAttribute(org.openmrs.attribute.BaseAttributeType, Object)}
	 */
	@Override
	public LocationAttribute newAttribute() {
		return new LocationAttribute();
	}
}

package org.openmrs.module.initializer.api.loc;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.module.initializer.api.BaseAttributeLineProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.locationAttributeLineProcessor")
public class LocationAttributeLineProcessor extends BaseAttributeLineProcessor<Location, LocationAttribute> {
	
	private LocationService locationService;
	
	@Autowired
	public LocationAttributeLineProcessor(@Qualifier("locationService") LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	protected LocationAttribute newAttributeInstance(String header, String attributeValue) {
		LocationAttribute attribute = new LocationAttribute();
		LocationAttributeType attributeType = getAttributeType(header);
		if (attributeType == null && StringUtils.isNotBlank(attributeValue)) {
			throw new APIException("Could not find AttributeType identified by: " + getAttributeTypeRef(header));
		}
		attribute.setAttributeType(attributeType);
		attribute.setValue(attributeValue);
		return attribute;
	}
	
	/**
	 * Gets {@link LocationAttributeType} from the DB basing on the <code>attributeTypeReference</code>
	 * that's generated from the <code>header</code>.
	 * 
	 * @param header string that contains the attribute type reference
	 * @return the <code>attributeType</code>
	 */
	private LocationAttributeType getAttributeType(String header) {
		String attributeTypeRef = getAttributeTypeRef(header);
		if (StringUtils.isBlank(attributeTypeRef)) {
			throw new IllegalArgumentException("AttributeType Reference cannot be blank");
		}
		LocationAttributeType ret = locationService.getLocationAttributeTypeByUuid(attributeTypeRef);
		if (ret == null) {
			ret = locationService.getLocationAttributeTypeByName(attributeTypeRef);
		}
		return ret;
	}
}

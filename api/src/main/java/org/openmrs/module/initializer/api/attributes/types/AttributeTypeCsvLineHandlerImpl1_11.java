package org.openmrs.module.initializer.api.attributes.types;

import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum.LOCATION;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum.PROVIDER;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum.VISIT;

import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(openmrsPlatformVersion = "[1.11.9 - 2.1.*]")
public class AttributeTypeCsvLineHandlerImpl1_11 implements AttributeTypeCsvLineHandler {
	
	@Override
	final public AttributeTypeEnum getAttributeType(CsvLine line) {
		String attributeDomain = line.getString(BaseAttributeTypeLineProcessor.HEADER_DOMAIN);
		AttributeTypeEnum type = getType(attributeDomain);
		if (type == null) {
			throw new IllegalArgumentException(
			        "No attribute type domain could be guessed from the CSV line: '" + line.toString() + "'.");
		}
		return type;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	final public BaseAttributeType newAttributeType(CsvLine line) {
		AttributeTypeEnum type = getAttributeType(line);
		BaseAttributeType attType = newType(type);
		if (attType == null) {
			throw new IllegalArgumentException(
			        "No attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
		}
		return attType;
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected AttributeTypeEnum getType(String attributeDomain) {
		
		AttributeTypeEnum type = null;
		
		if (LOCATION.toString().equalsIgnoreCase(attributeDomain)) {
			type = LOCATION;
		}
		if (VISIT.toString().equalsIgnoreCase(attributeDomain)) {
			type = VISIT;
		}
		if (PROVIDER.toString().equalsIgnoreCase(attributeDomain)) {
			type = PROVIDER;
		}
		return type;
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	@SuppressWarnings("rawtypes")
	protected BaseAttributeType newType(AttributeTypeEnum type) {
		
		BaseAttributeType attType = null;
		
		switch (type) {
			case LOCATION:
				return new LocationAttributeType();
			case VISIT:
				return new VisitAttributeType();
			case PROVIDER:
				return new ProviderAttributeType();
			default:
				return attType;
		}
	}
	
}

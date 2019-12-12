package org.openmrs.module.initializer.api.attributes.types;

import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.LOCATION;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.PROVIDER;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.VISIT;

import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(openmrsPlatformVersion = "[2.1.1 - 2.1.*]")
public class AttributeTypeCsvLineHandlerImpl implements AttributeTypeCsvLineHandler {
	
	@Override
	final public AttributeTypeEntity getAttributeType(CsvLine line) {
		String attributeDomain = line.get(HEADER_ENTITY_NAME, true);
		AttributeTypeEntity type = getType(attributeDomain);
		if (type == null) {
			throw new IllegalArgumentException(
			        "No attribute type domain could be guessed from the CSV line: '" + line.toString() + "'.");
		}
		return type;
	}
	
	@Override
	final public BaseAttributeType<?> newAttributeType(CsvLine line) {
		AttributeTypeEntity type = getAttributeType(line);
		BaseAttributeType<?> attType = newType(type);
		if (attType == null) {
			throw new IllegalArgumentException(
			        "No attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
		}
		return attType;
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected AttributeTypeEntity getType(String attributeDomain) {
		
		AttributeTypeEntity type = null;
		
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
	protected BaseAttributeType<?> newType(AttributeTypeEntity type) {
		
		BaseAttributeType<?> attType = null;
		
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

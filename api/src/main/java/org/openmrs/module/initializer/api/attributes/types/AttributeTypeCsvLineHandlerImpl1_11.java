package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(openmrsPlatformVersion = "[1.11.9 - 2.1.*]")
public class AttributeTypeCsvLineHandlerImpl1_11 implements AttributeTypeCsvLineHandler {
	
	@Override
	public AttributeType getAttributeType(CsvLine line) {
		String attributeDomain = line.getString(BaseAttributeTypeLineProcessor.HEADER_DOMAIN);
		if (AttributeType.LOCATION.toString().equalsIgnoreCase(attributeDomain)) {
			return AttributeType.LOCATION;
		}
		if (AttributeType.VISIT.toString().equalsIgnoreCase(attributeDomain)) {
			return AttributeType.VISIT;
		}
		if (AttributeType.PROVIDER.toString().equalsIgnoreCase(attributeDomain)) {
			return AttributeType.PROVIDER;
		}
		throw new IllegalArgumentException(
		        "No attribute type domain could be guessed from the CSV line: '" + line.toString() + "'.");
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public BaseAttributeType newAttributeType(CsvLine line) {
		AttributeType type = getAttributeType(line);
		switch (type) {
			case LOCATION:
				return new LocationAttributeType();
			case VISIT:
				return new VisitAttributeType();
			case PROVIDER:
				return new ProviderAttributeType();
			default:
				throw new IllegalArgumentException(
				        "No attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
		}
	}
	
}

package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(openmrsPlatformVersion = "[1.11.9 - 2.1.*]")
public class AttributeTypeLineProcessorCompatibiltyHandler1_11_9 implements AttributeTypeLineProcessorCompatibilty {
	
	@Override
	public AttributeType getAttributeType(CsvLine line) {
		String attributeDomain = line.getString(BaseAttributeTypeLineProcessor.HEADER_DOMAIN);
		if (attributeDomain.equalsIgnoreCase(AttributeType.LOCATION.toString())) {
			return AttributeType.LOCATION;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeType.VISIT.toString())) {
			return AttributeType.VISIT;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeType.PROVIDER.toString())) {
			return AttributeType.PROVIDER;
		}
		throw new IllegalArgumentException(
		        "No Attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
	}
	
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
				        "No Attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
		}
	}
	
}

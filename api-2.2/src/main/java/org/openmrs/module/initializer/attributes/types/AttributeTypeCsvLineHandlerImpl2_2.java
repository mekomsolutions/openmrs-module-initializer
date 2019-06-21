package org.openmrs.module.initializer.attributes.types;

import org.openmrs.ConceptAttributeType;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeCsvLineHandler;
import org.openmrs.module.initializer.api.attributes.types.BaseAttributeTypeLineProcessor;

@OpenmrsProfile(openmrsPlatformVersion = "[2.2.0 - 2.3.*]")
public class AttributeTypeCsvLineHandlerImpl2_2 implements AttributeTypeCsvLineHandler {
	
	@Override
	public AttributeTypeEnum getAttributeType(CsvLine line) {
		String attributeDomain = line.getString(BaseAttributeTypeLineProcessor.HEADER_DOMAIN);
		if (attributeDomain.equalsIgnoreCase(AttributeTypeEnum.LOCATION.toString())) {
			return AttributeTypeEnum.LOCATION;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeTypeEnum.VISIT.toString())) {
			return AttributeTypeEnum.VISIT;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeTypeEnum.PROVIDER.toString())) {
			return AttributeTypeEnum.PROVIDER;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeTypeEnum.PROGRAM.toString())) {
			return AttributeTypeEnum.PROGRAM;
		}
		if (attributeDomain.equalsIgnoreCase(AttributeTypeEnum.CONCEPT.toString())) {
			return AttributeTypeEnum.CONCEPT;
		}
		throw new IllegalArgumentException(
		        "No Attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public BaseAttributeType newAttributeType(CsvLine line) {
		AttributeTypeEnum type = getAttributeType(line);
		switch (type) {
			case LOCATION:
				return new LocationAttributeType();
			case VISIT:
				return new VisitAttributeType();
			case PROVIDER:
				return new ProviderAttributeType();
			case PROGRAM:
				return new ProgramAttributeType();
			case CONCEPT:
				return new ConceptAttributeType();
			default:
				throw new IllegalArgumentException(
				        "No Attribute type could be guessed from the CSV line: '" + line.toString() + "'.");
		}
		
	}
	
}

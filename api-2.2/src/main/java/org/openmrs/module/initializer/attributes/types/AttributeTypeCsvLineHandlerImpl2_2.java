package org.openmrs.module.initializer.attributes.types;

import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum.CONCEPT;
import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum.PROGRAM;

import org.openmrs.ConceptAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeCsvLineHandlerImpl1_11;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum;

@OpenmrsProfile(openmrsPlatformVersion = "[2.2.0 - 2.3.*]")
public class AttributeTypeCsvLineHandlerImpl2_2 extends AttributeTypeCsvLineHandlerImpl1_11 {
	
	@Override
	protected AttributeTypeEnum getType(String attributeDomain) {
		
		AttributeTypeEnum type = super.getType(attributeDomain);
		
		if (PROGRAM.toString().equalsIgnoreCase(attributeDomain)) {
			type = PROGRAM;
		}
		if (CONCEPT.toString().equalsIgnoreCase(attributeDomain)) {
			type = CONCEPT;
		}
		return type;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected BaseAttributeType newType(AttributeTypeEnum type) {
		
		BaseAttributeType attType = super.newType(type);
		
		switch (type) {
			case PROGRAM:
				return new ProgramAttributeType();
			case CONCEPT:
				return new ConceptAttributeType();
			default:
				return attType;
		}
	}
}

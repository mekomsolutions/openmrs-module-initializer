package org.openmrs.module.initializer.api.attributes.types;

import static org.openmrs.module.initializer.api.attributes.types.AttributeTypeEntity.PROGRAM;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;

@OpenmrsProfile(openmrsPlatformVersion = "[2.1.1 - 2.1.*]", modules = { "bahmnicore:*.*" })
public class BahmniAttributeTypeCsvLineHandlerImpl extends AttributeTypeCsvLineHandlerImpl {
	
	@Override
	protected AttributeTypeEntity getType(String attributeDomain) {
		
		AttributeTypeEntity type = super.getType(attributeDomain);
		
		if (PROGRAM.toString().equalsIgnoreCase(attributeDomain)) {
			type = PROGRAM;
		}
		return type;
	}
	
	@Override
	protected BaseAttributeType<?> newType(AttributeTypeEntity type) {
		
		BaseAttributeType<?> attType = super.newType(type);
		
		switch (type) {
			case PROGRAM:
				return new ProgramAttributeType();
			default:
				return attType;
		}
	}
	
}

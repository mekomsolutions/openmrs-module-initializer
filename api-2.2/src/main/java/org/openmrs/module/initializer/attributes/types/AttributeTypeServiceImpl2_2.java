package org.openmrs.module.initializer.attributes.types;

import java.util.List;
import java.util.Set;

import org.openmrs.ConceptAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeServiceImpl1_11;

@SuppressWarnings("rawtypes")
@OpenmrsProfile(openmrsPlatformVersion = "[2.2.0 - 2.3.*]")
public class AttributeTypeServiceImpl2_2 extends AttributeTypeServiceImpl1_11 {
	
	@Override
	public Set<AttributeTypeEnum> getSupportedTypes() {
		Set<AttributeTypeEnum> types = super.getSupportedTypes();
		types.add(AttributeTypeEnum.CONCEPT);
		types.add(AttributeTypeEnum.PROGRAM);
		return types;
	}
	
	@Override
	protected BaseAttributeType save(BaseAttributeType instance) {
		
		instance = super.save(instance);
		
		if (instance instanceof ProgramAttributeType) {
			ProgramAttributeType programAttributeType = (ProgramAttributeType) instance;
			instance = Context.getProgramWorkflowService().saveProgramAttributeType(programAttributeType);
		}
		if (instance instanceof ConceptAttributeType) {
			ConceptAttributeType conceptAttributeType = (ConceptAttributeType) instance;
			instance = Context.getConceptService().saveConceptAttributeType(conceptAttributeType);
		}
		
		return instance;
	}
	
	@Override
	protected BaseAttributeType getByUuid(String uuid, AttributeTypeEnum attributeType) {
		
		switch (attributeType) {
			case CONCEPT:
				return Context.getConceptService().getConceptAttributeTypeByUuid(uuid);
			
			case PROGRAM:
				return Context.getProgramWorkflowService().getProgramAttributeTypeByUuid(uuid);
			
			default:
				return null;
		}
	}
	
	@Override
	protected BaseAttributeType getByName(String name, AttributeTypeEnum typeEnum) {
		switch (typeEnum) {
			case CONCEPT:
				return Context.getConceptService().getConceptAttributeTypeByName(name);
			
			case PROGRAM:
				List<ProgramAttributeType> existingProgAttType = Context.getProgramWorkflowService()
				        .getAllProgramAttributeTypes();
				if (existingProgAttType != null) {
					for (ProgramAttributeType candidate : existingProgAttType) {
						if (candidate.getName().equals(name)) {
							return candidate;
						}
					}
				}
				return null;
			
			default:
				log.info(getClass().getSimpleName() + " does not support the attribute type '" + typeEnum.toString()
				        + "'. The supported attribute types for this implementation are '" + getSupportedTypes() + "'.");
				
				return null;
		}
	}
	
}

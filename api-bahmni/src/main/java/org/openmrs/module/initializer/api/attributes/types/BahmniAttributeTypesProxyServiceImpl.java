package org.openmrs.module.initializer.api.attributes.types;

import java.util.List;
import java.util.Set;

import org.bahmni.module.bahmnicore.model.bahmniPatientProgram.ProgramAttributeType;
import org.bahmni.module.bahmnicore.service.BahmniProgramWorkflowService;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.attribute.BaseAttributeType;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(openmrsPlatformVersion = "[2.1.1 - 2.1.*]", modules = { "bahmnicore:*.*" })
public class BahmniAttributeTypesProxyServiceImpl extends AttributeTypesProxyServiceImpl {
	
	@Autowired
	BahmniProgramWorkflowService bahmniProgramWorkflowService;
	
	@Override
	public Set<AttributeTypeEntity> getSupportedTypes() {
		Set<AttributeTypeEntity> types = super.getSupportedTypes();
		types.add(AttributeTypeEntity.PROGRAM);
		return types;
	}
	
	@Override
	protected BaseAttributeType<?> save(BaseAttributeType<?> instance) {
		
		instance = super.save(instance);
		
		if (instance instanceof ProgramAttributeType) {
			instance = bahmniProgramWorkflowService.saveProgramAttributeType((ProgramAttributeType) instance);
		}
		
		return instance;
	}
	
	@Override
	protected BaseAttributeType<?> getByUuid(String uuid, AttributeTypeEntity typeEnum) {
		
		BaseAttributeType<?> attType = super.getByUuid(uuid, typeEnum);
		
		switch (typeEnum) {
			case PROGRAM:
				attType = bahmniProgramWorkflowService.getProgramAttributeTypeByUuid(uuid);
				break;
			
			default:
				;
		}
		
		return attType;
	}
	
	@Override
	protected BaseAttributeType<?> getByName(String name, AttributeTypeEntity typeEnum) {
		
		BaseAttributeType<?> attType = super.getByName(name, typeEnum);
		
		switch (typeEnum) {
			case PROGRAM:
				List<ProgramAttributeType> programAttTypes = bahmniProgramWorkflowService.getAllProgramAttributeTypes();
				if (programAttTypes != null) {
					for (ProgramAttributeType candidate : programAttTypes) {
						if (name.equals(candidate.getName())) {
							attType = candidate;
							break;
						}
					}
				}
				break;
			
			default:
				;
		}
		
		return attType;
	}
	
}

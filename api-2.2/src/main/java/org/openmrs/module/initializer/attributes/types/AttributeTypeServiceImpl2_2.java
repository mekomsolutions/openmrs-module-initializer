package org.openmrs.module.initializer.attributes.types;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptAttributeType;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeService;

@SuppressWarnings("rawtypes")
@OpenmrsProfile(openmrsPlatformVersion = "[2.2.0 - 2.3.*]")
public class AttributeTypeServiceImpl2_2 implements AttributeTypeService {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void onShutdown() {
	}
	
	@Override
	public void onStartup() {
	}
	
	@Override
	public BaseAttributeType saveAttributeType(BaseAttributeType instance) {
		if (instance == null) {
			return null;
		}
		if (instance instanceof LocationAttributeType) {
			LocationAttributeType locationAttributeType = (LocationAttributeType) instance;
			return Context.getLocationService().saveLocationAttributeType(locationAttributeType);
		}
		if (instance instanceof VisitAttributeType) {
			VisitAttributeType visitAttributeType = (VisitAttributeType) instance;
			return Context.getVisitService().saveVisitAttributeType(visitAttributeType);
		}
		if (instance instanceof ProviderAttributeType) {
			ProviderAttributeType providerAttributeType = (ProviderAttributeType) instance;
			return Context.getProviderService().saveProviderAttributeType(providerAttributeType);
		}
		if (instance instanceof ProgramAttributeType) {
			ProgramAttributeType programAttributeType = (ProgramAttributeType) instance;
			return Context.getProgramWorkflowService().saveProgramAttributeType(programAttributeType);
		}
		if (instance instanceof ConceptAttributeType) {
			ConceptAttributeType conceptAttributeType = (ConceptAttributeType) instance;
			return Context.getConceptService().saveConceptAttributeType(conceptAttributeType);
		}
		log.error("This attribute type " + instance.getClass().getSimpleName()
		        + " is not supported, here are the currently supported attribute types " + getSupportedTypes());
		return null;
	}
	
	@Override
	public BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeType attributeType) {
		if (StringUtils.isEmpty(uuid) || attributeType == null) {
			return null;
		}
		switch (attributeType) {
			case LOCATION:
				return Context.getLocationService().getLocationAttributeTypeByUuid(uuid);
			
			case VISIT:
				return Context.getVisitService().getVisitAttributeTypeByUuid(uuid);
			
			case PROVIDER:
				return Context.getProviderService().getProviderAttributeTypeByUuid(uuid);
			
			case CONCEPT:
				return Context.getConceptService().getConceptAttributeTypeByUuid(uuid);
			
			case PROGRAM:
				return Context.getProgramWorkflowService().getProgramAttributeTypeByUuid(uuid);
			
			default:
				log.error("This attribute type " + attributeType
				        + " is not supported, here are the currently supported attribute types " + getSupportedTypes());
				return null;
		}
	}
	
	@Override
	public List<String> getSupportedTypes() {
		return Arrays.asList(LocationAttributeType.class.getSimpleName(), VisitAttributeType.class.getSimpleName(),
		    ProviderAttributeType.class.getSimpleName(), ConceptAttributeType.class.getSimpleName(),
		    ProgramAttributeType.class.getSimpleName());
	}
	
	@Override
	public BaseAttributeType getAttributeTypeByName(String name, AttributeType attributeType) {
		if (StringUtils.isEmpty(name) || attributeType == null) {
			return null;
		}
		switch (attributeType) {
			case LOCATION:
				return Context.getLocationService().getLocationAttributeTypeByName(name);
			
			case VISIT:
				List<VisitAttributeType> existingVisitAttTypes = Context.getVisitService().getAllVisitAttributeTypes();
				if (existingVisitAttTypes != null) {
					for (VisitAttributeType candidate : existingVisitAttTypes) {
						if (candidate.getName().equals(name)) {
							return candidate;
						}
					}
				}
				return null;
			
			case PROVIDER:
				List<ProviderAttributeType> existingProvAttTypes = Context.getProviderService()
				        .getAllProviderAttributeTypes();
				if (existingProvAttTypes != null) {
					for (ProviderAttributeType candidate : existingProvAttTypes) {
						if (candidate.getName().equals(name)) {
							return candidate;
						}
					}
				}
				return null;
			
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
				log.error("This attribute type " + attributeType
				        + " is not supported, here are the currently supported attribute types " + getSupportedTypes());
				
				return null;
		}
	}
	
}

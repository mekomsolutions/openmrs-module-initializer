package org.openmrs.module.initializer.api.attributes.types;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.openmrs.LocationAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.InitializerLogFactory;

@SuppressWarnings("rawtypes")
@OpenmrsProfile(openmrsPlatformVersion = "[1.11.9 - 2.1.*]")
public class AttributeTypeServiceImpl1_11 implements AttributeTypeService {
	
	protected final Log log = InitializerLogFactory.getLog(AttributeTypeService.class);
	
	@Override
	public void onShutdown() {
	}
	
	@Override
	public void onStartup() {
	}
	
	@Override
	public Set<AttributeTypeEnum> getSupportedTypes() {
		Set<AttributeTypeEnum> types = new HashSet<AttributeTypeEnum>();
		types.add(AttributeTypeEnum.LOCATION);
		types.add(AttributeTypeEnum.VISIT);
		types.add(AttributeTypeEnum.PROVIDER);
		return types;
	}
	
	@Override
	public final BaseAttributeType saveAttributeType(BaseAttributeType instance) {
		
		if (instance == null || instance.getId() != null) {
			return instance;
		}
		
		instance = save(instance);
		
		if (instance.getId() == null) {
			log.info(
			    getClass().getSimpleName() + " does not support the attribute type '" + instance.getClass().getSimpleName()
			            + "'.\nThe supported attribute types for this implementation are '" + getSupportedTypes() + "'.");
		}
		
		return instance;
	}
	
	@Override
	public final BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeTypeEnum typeEnum) {
		if (StringUtils.isEmpty(uuid) || typeEnum == null) {
			return null;
		}
		
		if (!this.getSupportedTypes().contains(typeEnum)) {
			log.info(getClass().getSimpleName() + " does not support the attribute type '" + typeEnum.toString()
			        + "'. The supported attribute types for this implementation are '" + getSupportedTypes() + "'.");
			
			return null;
		}
		
		return getByUuid(uuid, typeEnum);
	}
	
	@Override
	public final BaseAttributeType getAttributeTypeByName(String name, AttributeTypeEnum typeEnum) {
		if (StringUtils.isEmpty(name) || typeEnum == null) {
			return null;
		}
		
		if (!this.getSupportedTypes().contains(typeEnum)) {
			log.info(getClass().getSimpleName() + " does not support the attribute type '" + typeEnum.toString()
			        + "'. The supported attribute types for this implementation are '" + getSupportedTypes() + "'.");
			
			return null;
		}
		
		return getByName(name, typeEnum);
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected BaseAttributeType save(BaseAttributeType instance) {
		
		if (instance instanceof LocationAttributeType) {
			LocationAttributeType locationAttributeType = (LocationAttributeType) instance;
			instance = Context.getLocationService().saveLocationAttributeType(locationAttributeType);
		}
		if (instance instanceof VisitAttributeType) {
			VisitAttributeType visitAttributeType = (VisitAttributeType) instance;
			instance = Context.getVisitService().saveVisitAttributeType(visitAttributeType);
		}
		if (instance instanceof ProviderAttributeType) {
			ProviderAttributeType providerAttributeType = (ProviderAttributeType) instance;
			instance = Context.getProviderService().saveProviderAttributeType(providerAttributeType);
		}
		
		return instance;
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected BaseAttributeType getByUuid(String uuid, AttributeTypeEnum attributeType) {
		
		switch (attributeType) {
			case LOCATION:
				return Context.getLocationService().getLocationAttributeTypeByUuid(uuid);
			
			case VISIT:
				return Context.getVisitService().getVisitAttributeTypeByUuid(uuid);
			
			case PROVIDER:
				return Context.getProviderService().getProviderAttributeTypeByUuid(uuid);
			
			default:
				return null;
		}
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected BaseAttributeType getByName(String name, AttributeTypeEnum attributeType) {
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
			
			default:
				return null;
		}
	}
}

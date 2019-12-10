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
public class AttributeTypesProxyServiceImpl implements AttributeTypesProxyService {
	
	protected final Log log = InitializerLogFactory.getLog(AttributeTypesProxyService.class);
	
	@Override
	public void onShutdown() {
	}
	
	@Override
	public void onStartup() {
	}
	
	@Override
	public Set<AttributeTypeEntity> getSupportedTypes() {
		Set<AttributeTypeEntity> types = new HashSet<AttributeTypeEntity>();
		types.add(AttributeTypeEntity.LOCATION);
		types.add(AttributeTypeEntity.VISIT);
		types.add(AttributeTypeEntity.PROVIDER);
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
	public final BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeTypeEntity typeEnum) {
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
	public final BaseAttributeType getAttributeTypeByName(String name, AttributeTypeEntity typeEnum) {
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
	protected BaseAttributeType getByUuid(String uuid, AttributeTypeEntity typeEnum) {
		
		BaseAttributeType attType = null;
		
		switch (typeEnum) {
			case LOCATION:
				attType = Context.getLocationService().getLocationAttributeTypeByUuid(uuid);
				break;
			
			case VISIT:
				attType = Context.getVisitService().getVisitAttributeTypeByUuid(uuid);
				break;
			
			case PROVIDER:
				attType = Context.getProviderService().getProviderAttributeTypeByUuid(uuid);
				break;
			
			default:
				;
		}
		
		return attType;
	}
	
	/**
	 * To be overridden and extended by subclasses.
	 */
	protected BaseAttributeType getByName(String name, AttributeTypeEntity typeEnum) {
		
		BaseAttributeType attType = null;
		
		switch (typeEnum) {
			case LOCATION:
				attType = Context.getLocationService().getLocationAttributeTypeByName(name);
				break;
			
			case VISIT:
				List<VisitAttributeType> visitAttTypes = Context.getVisitService().getAllVisitAttributeTypes();
				if (visitAttTypes != null) {
					for (VisitAttributeType candidate : visitAttTypes) {
						if (name.equals(candidate.getName())) {
							attType = candidate;
							break; // for loop
						}
					}
				}
				break;
			
			case PROVIDER:
				List<ProviderAttributeType> providerAttTypes = Context.getProviderService().getAllProviderAttributeTypes();
				if (providerAttTypes != null) {
					for (ProviderAttributeType candidate : providerAttTypes) {
						if (name.equals(candidate.getName())) {
							attType = candidate;
							break; // for loop
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

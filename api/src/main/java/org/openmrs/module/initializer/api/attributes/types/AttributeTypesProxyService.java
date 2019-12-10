package org.openmrs.module.initializer.api.attributes.types;

import java.util.Set;

import org.openmrs.api.OpenmrsService;
import org.openmrs.attribute.BaseAttributeType;

/**
 * Proxy OpenMRS service to interface CRUD operations with attribute types.
 */
@SuppressWarnings("rawtypes")
public interface AttributeTypesProxyService extends OpenmrsService {
	
	/**
	 * Saves an <code>AttributeType</code>
	 * 
	 * @param attributeType
	 * @return attributeType
	 */
	public BaseAttributeType saveAttributeType(BaseAttributeType attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by uuid.
	 * 
	 * @param uuid of AttributeType
	 * @param attributeType the required {@link AttributeTypeEntity}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeTypeEntity attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by name.
	 * 
	 * @param name of AttributeType
	 * @param attributeType the required {@link AttributeTypeEntity}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByName(String name, AttributeTypeEntity attributeType);
	
	/**
	 * @return The set of supported {@link AttributeTypeEntity}.
	 */
	public Set<AttributeTypeEntity> getSupportedTypes();
	
}

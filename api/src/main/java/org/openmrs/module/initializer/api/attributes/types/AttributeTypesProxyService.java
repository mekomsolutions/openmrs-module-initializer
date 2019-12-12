package org.openmrs.module.initializer.api.attributes.types;

import java.util.Set;

import org.openmrs.api.OpenmrsService;
import org.openmrs.attribute.BaseAttributeType;

/**
 * Proxy OpenMRS service to interface CRUD operations with attribute types.
 */
public interface AttributeTypesProxyService extends OpenmrsService {
	
	/**
	 * Saves an <code>AttributeType</code>
	 * 
	 * @param attributeType
	 * @return attributeType
	 */
	BaseAttributeType<?> saveAttributeType(BaseAttributeType<?> attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by uuid.
	 * 
	 * @param uuid of AttributeType
	 * @param attributeType the required {@link AttributeTypeEntity}
	 * @return attributeType
	 */
	BaseAttributeType<?> getAttributeTypeByUuid(String uuid, AttributeTypeEntity attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by name.
	 * 
	 * @param name of AttributeType
	 * @param attributeType the required {@link AttributeTypeEntity}
	 * @return attributeType
	 */
	BaseAttributeType<?> getAttributeTypeByName(String name, AttributeTypeEntity attributeType);
	
	/**
	 * @return The set of supported {@link AttributeTypeEntity}.
	 */
	Set<AttributeTypeEntity> getSupportedTypes();
}

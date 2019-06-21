package org.openmrs.module.initializer.api.attributes.types;

import java.util.Set;

import org.openmrs.api.OpenmrsService;
import org.openmrs.attribute.BaseAttributeType;

/**
 * Proxy OpenMRS service to interface CRUD operations with attribute types.
 */
@SuppressWarnings("rawtypes")
public interface AttributeTypeService extends OpenmrsService {
	
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
	 * @param attributeType the required {@link AttributeTypeEnum}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeTypeEnum attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by name.
	 * 
	 * @param name of AttributeType
	 * @param attributeType the required {@link AttributeTypeEnum}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByName(String name, AttributeTypeEnum attributeType);
	
	/**
	 * @return The set of supported {@link AttributeTypeEnum}.
	 */
	public Set<AttributeTypeEnum> getSupportedTypes();
	
}

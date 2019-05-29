package org.openmrs.module.initializer.api.attributes.types;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.attribute.BaseAttributeType;

public interface AttributeTypeServiceCompatibility extends OpenmrsService {
	
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
	 * @param attributeType the required {@link AttributeType}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByUuid(String uuid, AttributeType attributeType);
	
	/**
	 * Gets an <code>AttributeType</code> by name.
	 * 
	 * @param name of AttributeType
	 * @param attributeType the required {@link AttributeType}
	 * @return attributeType
	 */
	public BaseAttributeType getAttributeTypeByName(String name, AttributeType attributeType);
	
	/**
	 * @return list of supported {@link AttributeType} names.
	 */
	public List<String> getSupportedTypes();
	
}

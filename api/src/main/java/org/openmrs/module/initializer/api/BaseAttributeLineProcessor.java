package org.openmrs.module.initializer.api;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.Customizable;
import org.openmrs.module.initializer.InitializerConstants;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class to any <code>AttributeLineProcessor</code> that processes all attributes of a domain
 * entity.
 */
public abstract class BaseAttributeLineProcessor<T extends BaseOpenmrsObject, AT extends BaseAttributeType<?>, A extends BaseAttribute<AT, ?>> extends BaseLineProcessor<T> {
	
	public static final String HEADER_ATTRIBUTE_PREFIX = "attribute|";
	
	@Override
	public T fill(T instance, CsvLine line) throws IllegalArgumentException {
		
		Customizable<A> attributable = (Customizable<A>) instance;
		
		// First, retrieve all attributes that are defined in the CSV and tha values for the given row
		Map<AT, Object> rowValues = new LinkedHashMap<>();
		for (String header : line.getHeaderLine()) {
			if (header.toLowerCase().startsWith(HEADER_ATTRIBUTE_PREFIX)) {
				String attributeTypeIdentifier = StringUtils.removeStartIgnoreCase(header, HEADER_ATTRIBUTE_PREFIX);
				AT attributeType = getAttributeType(attributeTypeIdentifier);
				Object attributeValue = null;
				String attributeValueStr = line.getString(header, "");
				if (attributeType == null) {
					throw new IllegalArgumentException("An attribute value is specified ('" + attributeValueStr
					        + "') for an attribute type that cannot be resolved by the following identifier: '"
					        + attributeType + "'");
				}
				if (StringUtils.isNotBlank(attributeValueStr)) {
					String dtClass = attributeType.getDatatypeClassname();
					String dtConfig = attributeType.getDatatypeConfig();
					CustomDatatype<?> datatype = CustomDatatypeUtil.getDatatype(dtClass, dtConfig);
					attributeValue = datatype.fromReferenceString(attributeValueStr);
				}
				rowValues.put(attributeType, attributeValue);
			}
		}
		
		// Next, iterate over the existing attributes on the instance, and void and recreate if any have changed
		for (A existingAttribute : attributable.getActiveAttributes()) {
			AT type = existingAttribute.getAttributeType();
			Object existingValue = existingAttribute.getValue();
			// If the CSV does not define a column for an existing attribute type, do not modify it on the instance
			if (rowValues.containsKey(type)) {
				Object newValue = rowValues.remove(type);
				// Only process a change if the new value is different from the existing value
				if (!existingValue.equals(newValue)) {
					// Void existing attribute
					existingAttribute.setVoided(true);
					existingAttribute.setDateVoided(new Date());
					existingAttribute.setVoidedBy(Context.getAuthenticatedUser());
					existingAttribute.setVoidReason(InitializerConstants.DEFAULT_VOID_REASON);
					// Add new attribute, if there is a value defined for it
					if (newValue != null) {
						A newAttribute = newAttribute();
						newAttribute.setAttributeType(type);
						newAttribute.setValue(newValue);
						attributable.addAttribute(newAttribute);
					}
				}
			}
		}
		
		// Finally, add any remaining attributes that did not match any existing types on the instance
		for (AT type : rowValues.keySet()) {
			Object newValue = rowValues.get(type);
			if (newValue != null) {
				A newAttribute = newAttribute();
				newAttribute.setAttributeType(type);
				newAttribute.setValue(newValue);
				attributable.addAttribute(newAttribute);
			}
		}
		
		return instance;
	}
	
	/**
	 * Fetches an attribute type by its identifier.
	 * 
	 * @param identifier The attribute type identifier, eg. UUID, name, ...
	 * @throws IllegalArgumentException If no attribute type could be fetched.
	 */
	public abstract AT getAttributeType(String identifier) throws IllegalArgumentException;
	
	/**
	 * Constructs a new {@link Attribute} instance.
	 */
	public abstract A newAttribute();
}

package org.openmrs.module.initializer.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.attribute.Attribute;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.customdatatype.Customizable;

/**
 * Base class to any <code>AttributeLineProcessor</code> that processes all attributes of a domain
 * entity.
 */
public abstract class BaseAttributeLineProcessor<T extends BaseOpenmrsObject, AT extends BaseAttributeType<?>, A extends BaseAttribute<?, ?>> extends BaseLineProcessor<T> {
	
	public static final String HEADER_ATTRIBUTE_PREFIX = "attribute|";
	
	protected List<String> allAttributeHeaders;
	
	@Override
	public T fill(T instance, CsvLine line) throws IllegalArgumentException {
		
		if (allAttributeHeaders == null) {
			setAllAttributeHeaders(line.getHeaderLine());
		}
		
		Customizable<A> attributable = (Customizable<A>) instance;
		
		attributable.getAttributes().clear();
		
		for (String attributeHeader : allAttributeHeaders) {
			
			String strValue = line.get(attributeHeader);
			if (StringUtils.isNotBlank(strValue)) {
				
				String attTypeIdentifier = StringUtils.removeStartIgnoreCase(attributeHeader, HEADER_ATTRIBUTE_PREFIX);
				
				AT attType = getAttributeType(attTypeIdentifier);
				if (attType == null) {
					throw new IllegalArgumentException("An attribute value is specified ('" + strValue
					        + "') for an attribute type that cannot be resolved by the following identifier: '"
					        + attTypeIdentifier + "'");
				}
				CustomDatatype<?> datatype = CustomDatatypeUtil.getDatatype(attType.getDatatypeClassname(),
				    attType.getDatatypeConfig());
				Object value = datatype.fromReferenceString(strValue);
				
				attributable.addAttribute(newAttribute(attType, value));
			}
		}
		
		return instance;
	}
	
	/**
	 * Gathers all attributes headers in one collection.
	 */
	protected void setAllAttributeHeaders(String[] headerLine) {
		allAttributeHeaders = new ArrayList<>();
		for (String header : headerLine) {
			if (StringUtils.startsWithIgnoreCase(header, HEADER_ATTRIBUTE_PREFIX)) {
				allAttributeHeaders.add(header);
			}
		}
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
	public abstract A newAttribute(AT type, Object value);
}

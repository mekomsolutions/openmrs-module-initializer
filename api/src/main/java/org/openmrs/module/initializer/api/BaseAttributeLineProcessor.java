package org.openmrs.module.initializer.api;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

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
public abstract class BaseAttributeLineProcessor<T extends BaseOpenmrsObject, AT extends BaseAttributeType<?>, A extends BaseAttribute<AT, ?>> extends BaseLineProcessor<T> {
	
	public static final String HEADER_ATTRIBUTE_PREFIX = "attribute|";
	
	@Override
	public T fill(T instance, CsvLine line) throws IllegalArgumentException {
		
		Customizable<A> attributable = (Customizable<A>) instance;
		
		Consumer<? super SimpleEntry<String, String>> processAttributeData = attData -> {
			
			AT attType = getAttributeType(attData.getKey());
			if (attType == null) {
				throw new IllegalArgumentException("An attribute value is specified ('" + attData.getValue()
				        + "') for an attribute type that cannot be resolved by the following identifier: '"
				        + attData.getKey() + "'");
			}
			
			attributable.getAttributes().removeIf(att -> att.getAttributeType().equals(attType));
			
			CustomDatatype<?> datatype = CustomDatatypeUtil.getDatatype(attType.getDatatypeClassname(),
			    attType.getDatatypeConfig());
			Object value = datatype == null ? null : datatype.fromReferenceString(attData.getValue());
			
			A attribute = newAttribute();
			attribute.setAttributeType(attType);
			attribute.setValue(value);
			
			attributable.addAttribute(attribute);
		};
		
		// process the attribute value from each attribute header
		Arrays.stream(line.getHeaderLine()).filter(h -> StringUtils.startsWithIgnoreCase(h, HEADER_ATTRIBUTE_PREFIX)).map(
		    h -> new AbstractMap.SimpleEntry<>(StringUtils.removeStartIgnoreCase(h, HEADER_ATTRIBUTE_PREFIX), line.get(h)))
		        .filter(attData -> StringUtils.isNotBlank(attData.getValue())).forEach(processAttributeData);
		
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

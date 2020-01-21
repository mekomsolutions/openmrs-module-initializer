package org.openmrs.module.initializer.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.customdatatype.Customizable;

/**
 * Base class to any <code>AttributeLineProcessor</code>
 */
public abstract class BaseAttributeLineProcessor<T extends BaseOpenmrsObject, A extends BaseAttribute> extends BaseLineProcessor<T> {
	
	private static final String ATTRIBUTE_HEADER_SEPERATOR = "|";
	
	private static final String HEADER_PREFIX = "attribute|";
	
	protected List<String> attributeHeaders;
	
	@Override
	public T fill(T instance, CsvLine line) throws IllegalArgumentException {
		if (attributeHeaders == null) {
			initAttributeHeaders(line.getHeaderLine());
			
		}
		((Customizable) instance).getAttributes().clear();
		;
		for (String header : attributeHeaders) {
			String locAttributeValue = line.get(header);
			if (StringUtils.isNotBlank(locAttributeValue)) {
				A attribute = newAttributeInstance(header, locAttributeValue);
				((Customizable) instance).addAttribute(attribute);
			}
		}
		return instance;
	}
	
	/**
	 * Initialises {@code attributeHeaders}
	 */
	protected void initAttributeHeaders(String[] headerLine) {
		attributeHeaders = new ArrayList<>();
		for (String header : headerLine) {
			if (StringUtils.startsWithIgnoreCase(header, HEADER_PREFIX)) {
				attributeHeaders.add(header);
			}
		}
	}
	
	/**
	 * Retrieves the reference to the {@link AttributeType} from a header column
	 * 
	 * @param header
	 */
	protected String getAttributeTypeRef(String header) {
		return header.substring(header.indexOf(ATTRIBUTE_HEADER_SEPERATOR) + 1).trim();
	}
	
	/**
	 * Constructs a new {@link Attribute} instance
	 */
	protected abstract A newAttributeInstance(String header, String attributeValue);
}

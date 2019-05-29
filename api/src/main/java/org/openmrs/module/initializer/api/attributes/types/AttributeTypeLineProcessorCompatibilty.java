package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@SuppressWarnings("rawtypes")
public interface AttributeTypeLineProcessorCompatibilty {
	
	public AttributeType getAttributeType(CsvLine line);
	
	public BaseAttributeType newAttributeType(CsvLine line);
}

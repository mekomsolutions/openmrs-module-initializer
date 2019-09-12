package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

@SuppressWarnings("rawtypes")
public interface AttributeTypeCsvLineHandler {
	
	public AttributeTypeEntity getAttributeType(CsvLine line);
	
	public BaseAttributeType newAttributeType(CsvLine line);
}

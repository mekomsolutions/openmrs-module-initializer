package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.CsvLine;

public interface AttributeTypeCsvLineHandler {
	
	final static String HEADER_ENTITY_NAME = "entity name";
	
	public AttributeTypeEntity getAttributeType(CsvLine line);
	
	public BaseAttributeType<?> newAttributeType(CsvLine line);
}

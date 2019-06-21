package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.LocationAttributeType;

public enum AttributeTypeEnum {
	LOCATION(LocationAttributeType.class.getSimpleName()),
	VISIT(LocationAttributeType.class.getSimpleName()),
	CONCEPT(LocationAttributeType.class.getSimpleName()),
	PROVIDER(LocationAttributeType.class.getSimpleName()),
	PROGRAM(LocationAttributeType.class.getSimpleName());
	
	private final String attTypeName;
	
	AttributeTypeEnum(String attTypeName) {
		this.attTypeName = attTypeName;
	}
	
	public String getName() {
		return attTypeName;
	}
}
